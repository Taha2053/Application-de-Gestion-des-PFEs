package frontend.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Etudiant;
import dao.EtudiantDAO;
import dao.EtudiantDAOimpl;

import java.sql.SQLException;

public class AddEditEtudiantController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtClasse;

    private Etudiant etudiant;
    private final EtudiantDAO etudiantDAO = new EtudiantDAOimpl();
    private boolean saved = false;

    private StackPane modalOverlay = null;
    private Runnable onSaved = null;

    @FXML
    private Label lblError;

    public void setModalOverlay(StackPane overlay) {
        this.modalOverlay = overlay;
    }

    public void setOnSaved(Runnable r) {
        this.onSaved = r;
    }

    public void setEtudiant(Etudiant e) {
        this.etudiant = e;
        if (e != null) {
            txtNom.setText(e.getNom());
            txtPrenom.setText(e.getPrenom());
            txtEmail.setText(e.getEmail());
            txtClasse.setText(e.getClasse());
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String email = txtEmail.getText();
        String classe = txtClasse.getText();

        // validation: nom and prenom required
        if (nom == null || nom.trim().isEmpty() || prenom == null || prenom.trim().isEmpty()) {
            lblError.setText("Le nom et le prénom sont requis.");
            lblError.setVisible(true);
            return;
        }

        try {
            if (etudiant == null) {
                Etudiant e = new Etudiant(nom, prenom, email, classe);
                etudiantDAO.insert(e);
            } else {
                etudiant.setNom(nom);
                etudiant.setPrenom(prenom);
                etudiant.setEmail(email);
                etudiant.setClasse(classe);
                etudiantDAO.update(etudiant);
            }
            saved = true;
            if (onSaved != null) onSaved.run();
            closeWindow();
        } catch (SQLException ex) {
            lblError.setText("Erreur lors de l'enregistrement: " + ex.getMessage());
            lblError.setVisible(true);
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
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.close();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private void showError(String title, String details) {
        // fallback to label if available
        if (lblError != null) {
            lblError.setText(title + ": " + details);
            lblError.setVisible(true);
        }
    }
}
