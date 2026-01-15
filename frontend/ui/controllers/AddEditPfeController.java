package frontend.ui.controllers;

import dao.EtudiantDAO;
import dao.EtudiantDAOimpl;
import dao.PfeDAO;
import dao.PfeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Etudiant;
import model.Encadreur;
import model.Pfe;
import model.PfeEncadreur;
import dao.EncadreurDAO;
import dao.EncadreurDAOImpl;
import dao.pfe_encadreurDAO;
import dao.pfe_encadreurDAOImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddEditPfeController {

    @FXML
    private TextField txtTitre;
    @FXML
    private TextArea txtDescription;
    @FXML
    private ComboBox<String> choiceEtat;
    @FXML
    private DatePicker dateSoutenance;
    @FXML
    private ComboBox<Etudiant> choiceEtudiant;

    @FXML
    private ListView<Encadreur> choiceEncadreurs;

    @FXML
    private Label lblError;

    private Pfe pfe;
    private final PfeDAO pfeDAO = new PfeDAOImpl();
    private final EtudiantDAO etudiantDAO = new EtudiantDAOimpl();
    private final EncadreurDAO encadreurDAO = new EncadreurDAOImpl();
    private final pfe_encadreurDAO peDAO = new pfe_encadreurDAOImpl();

    private final ObservableList<Etudiant> etudiants = FXCollections.observableArrayList();
    private final ObservableList<Encadreur> encadreurs = FXCollections.observableArrayList();
    private final ObservableList<String> etats = FXCollections.observableArrayList();
    private boolean saved = false;

    private Runnable onSaved = null;

    public void setOnSaved(Runnable r) {
        this.onSaved = r;
    }

    private StackPane modalOverlay = null;

    public void setModalOverlay(StackPane overlay) {
        this.modalOverlay = overlay;
    }

    @FXML
    private void initialize() {
        try {
            List<Etudiant> list = etudiantDAO.getAll();
            etudiants.setAll(list);
            choiceEtudiant.setItems(etudiants);

            etats.setAll("En cours", "Validé", "Soutenu", "Refusé");
            choiceEtat.setItems(etats);

            choiceEtudiant.setCellFactory(lv -> new ListCell<Etudiant>() {
                @Override
                protected void updateItem(Etudiant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom() + " " + item.getPrenom());
                }
            });
            choiceEtudiant.setButtonCell(new ListCell<Etudiant>() {
                @Override
                protected void updateItem(Etudiant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom() + " " + item.getPrenom());
                }
            });

            List<Encadreur> listEnc = encadreurDAO.getAll();
            encadreurs.setAll(listEnc);
            choiceEncadreurs.setItems(encadreurs);
            choiceEncadreurs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            choiceEncadreurs.setCellFactory(lv -> new ListCell<Encadreur>() {
                @Override
                protected void updateItem(Encadreur item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom() + " " + item.getPrenom());
                }
            });
            choiceEncadreurs.setPlaceholder(new javafx.scene.control.Label("Aucun encadreur disponible"));

        } catch (SQLException e) {
            showError("Erreur de chargement des étudiants", e.getMessage());
        }
    }

    public void setPfe(Pfe p) {
        this.pfe = p;
        if (p != null) {
            txtTitre.setText(p.getTitre());
            txtDescription.setText(p.getDescription());
            if (p.getEtat() != null)
                choiceEtat.getSelectionModel().select(p.getEtat());
            if (p.getDateSoutenance() != null)
                dateSoutenance.setValue(p.getDateSoutenance());
            if (p.getEtudiant() != null) {
                Etudiant target = p.getEtudiant();
                boolean found = false;
                for (Etudiant e : etudiants) {
                    if (e.getIdetudiant() == target.getIdetudiant()) {
                        choiceEtudiant.getSelectionModel().select(e);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    etudiants.add(target);
                    choiceEtudiant.getSelectionModel().select(target);
                }
            }

            if (p.getEncadreur() != null && !p.getEncadreur().isEmpty()) {
                for (Encadreur enc : p.getEncadreur()) {
                    for (Encadreur item : encadreurs) {
                        if (item.getIdencadreur() == enc.getIdencadreur()) {
                            choiceEncadreurs.getSelectionModel().select(item);
                            break;
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        String titre = txtTitre.getText();
        String desc = txtDescription.getText();
        String etat = choiceEtat.getSelectionModel().getSelectedItem();
        LocalDate d = dateSoutenance.getValue();

        try {
            if (lblError != null) {
                lblError.setVisible(false);
            }
            if (d == null) {
                showError("Validation", "Sélectionnez une date de soutenance valide.");
                return;
            }

            Etudiant selected = choiceEtudiant.getSelectionModel().getSelectedItem();
            if (selected == null) {
                // if editing, keep previous student
                if (pfe != null && pfe.getEtudiant() != null) {
                    selected = pfe.getEtudiant();
                } else {
                    showError("Validation", "Sélectionnez un étudiant pour le PFE.");
                    return;
                }
            }

            Integer existing = pfeDAO.findPfeIdByEtudiant(selected.getIdetudiant());
            List<Encadreur> selectedEnc = choiceEncadreurs.getSelectionModel().getSelectedItems();

            if (pfe == null) {
                if (existing != null) {
                    showError("Validation", "Cet étudiant a déjà un PFE assigné.");
                    return;
                }
                Pfe p = new Pfe(titre, desc, etat, d, selected, selectedEnc);
                pfeDAO.insert(p);

                Integer newId = pfeDAO.findPfeIdByEtudiant(selected.getIdetudiant());
                if (newId != null && selectedEnc != null) {
                    for (Encadreur enc : selectedEnc) {
                        peDAO.insert(new PfeEncadreur(newId, enc.getIdencadreur()));
                    }
                }
            } else {
                if (existing != null && existing != pfe.getIdpfe()) {
                    showError("Validation", "Cet étudiant a déjà un PFE assigné.");
                    return;
                }
                pfe.setTitre(titre);
                pfe.setDescription(desc);
                pfe.setEtat(etat);
                pfe.setDateSoutenance(d);
                pfe.setEtudiant(selected);
                pfe.setEncadreur(selectedEnc);
                pfeDAO.update(pfe);

                List<PfeEncadreur> existingMappings = peDAO.getByPfe(pfe.getIdpfe());
                if (existingMappings != null) {
                    for (PfeEncadreur pm : existingMappings) {
                        peDAO.delete(pm);
                    }
                }
                if (selectedEnc != null) {
                    for (Encadreur enc : selectedEnc) {
                        peDAO.insert(new PfeEncadreur(pfe.getIdpfe(), enc.getIdencadreur()));
                    }
                }
            }
            saved = true;
            if (onSaved != null)
                onSaved.run();
            closeWindow();
        } catch (SQLException ex) {
            showError("Erreur lors de l'enregistrement", ex.getMessage());
        } catch (Exception ex) {
            showError("Donnée invalide", ex.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        if (modalOverlay != null) {
            Pane parent = (Pane) modalOverlay.getParent();
            if (parent != null)
                parent.getChildren().remove(modalOverlay);
            modalOverlay = null;
        } else {
            Stage stage = (Stage) txtTitre.getScene().getWindow();
            stage.close();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private void showError(String title, String details) {
        if (lblError != null) {
            lblError.setText(title + ": " + details);
            lblError.setVisible(true);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
