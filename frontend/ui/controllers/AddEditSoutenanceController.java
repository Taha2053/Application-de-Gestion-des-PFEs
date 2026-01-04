package frontend.ui.controllers;

import dao.PfeDAO;
import dao.PfeDAOImpl;
import dao.SoutenanceDAO;
import dao.SoutenanceDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Pfe;
import model.Soutenance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddEditSoutenanceController {

    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField txtSalle;
    @FXML
    private TextField txtNote;
    @FXML
    private ComboBox<Pfe> choicePfe;

    @FXML
    private Label lblError;

    private Soutenance soutenance;
    private final SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();
    private final PfeDAO pfeDAO = new PfeDAOImpl();
    private final ObservableList<Pfe> pfes = FXCollections.observableArrayList();
    private boolean saved = false;

    // callback invoked when this dialog saved successfully (used by parent to refresh lists)
    private Runnable onSaved = null;
    public void setOnSaved(Runnable r) { this.onSaved = r; }

    // modal overlay (if opened inside main window)
    private StackPane modalOverlay = null;

    public void setModalOverlay(StackPane overlay) {
        this.modalOverlay = overlay;
    }

    @FXML
    private void initialize() {
        try {
            List<Pfe> list = pfeDAO.getAll();
            pfes.setAll(list);
            choicePfe.setItems(pfes);

            // show only the PFE title in the combobox
            choicePfe.setCellFactory(lv -> new ListCell<Pfe>() {
                @Override
                protected void updateItem(Pfe item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTitre());
                }
            });
            choicePfe.setButtonCell(new ListCell<Pfe>() {
                @Override
                protected void updateItem(Pfe item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTitre());
                }
            });

        } catch (SQLException e) {
            showError("Erreur de chargement des PFE", e.getMessage());
        }
    }

    public void setSoutenance(Soutenance s) {
        this.soutenance = s;
        if (s != null) {
            if (s.getDate() != null)
                dpDate.setValue(s.getDate());
            txtSalle.setText(s.getSalle());
            txtNote.setText(String.valueOf(s.getNote()));
            if (s.getPfe() != null) {
                Pfe target = s.getPfe();
                boolean found = false;
                for (Pfe p : pfes) {
                    if (p.getIdpfe() == target.getIdpfe()) {
                        choicePfe.getSelectionModel().select(p);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    pfes.add(target);
                    choicePfe.getSelectionModel().select(target);
                }
            }
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        try {
            if (lblError != null) { lblError.setVisible(false); }
            LocalDate d = dpDate.getValue();
            if (d == null)
                throw new IllegalArgumentException("Date invalide");
            String salle = txtSalle.getText();
            double note = 0.0;
            if (txtNote.getText() != null && !txtNote.getText().trim().isEmpty())
                note = Double.parseDouble(txtNote.getText());
            Pfe selected = choicePfe.getSelectionModel().getSelectedItem();
            if (selected == null) {
                // if editing, keep previous PFE association
                if (soutenance != null && soutenance.getPfe() != null) selected = soutenance.getPfe();
            }

            // prevent creating multiple soutenances for same PFE
            Integer existing = selected != null ? soutenanceDAO.findSoutenanceIdByPfe(selected.getIdpfe()) : null;
            if (soutenance == null) {
                if (selected != null && existing != null) {
                    showError("Validation", "Ce PFE a déjà une soutenance planifiée.");
                    return;
                }
                Soutenance s = new Soutenance(d, salle, note, selected);
                soutenanceDAO.insert(s);
            } else {
                if (selected != null && existing != null && existing != soutenance.getIdsoutenance()) {
                    showError("Validation", "Ce PFE a déjà une soutenance planifiée.");
                    return;
                }
                soutenance.setDate(d);
                soutenance.setSalle(salle);
                soutenance.setNote(note);
                soutenance.setPfe(selected);
                soutenanceDAO.update(soutenance);
            }
            saved = true;
            if (onSaved != null) onSaved.run();
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
            if (parent != null) parent.getChildren().remove(modalOverlay);
            modalOverlay = null;
        } else {
            Stage stage = (Stage) txtSalle.getScene().getWindow();
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