package frontend.ui.controllers;

import dao.EncadreurDAO;
import dao.EncadreurDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Encadreur;

import java.sql.SQLException;

public class AddEditEncadreurController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtGrade;
    @FXML
    private TextField txtEmail;

    private Encadreur encadreur;
    private final EncadreurDAO encadreurDAO = new EncadreurDAOImpl();
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

    public void setEncadreur(Encadreur e) {
        this.encadreur = e;
        if (e != null) {
            txtNom.setText(e.getNom());
            txtPrenom.setText(e.getPrenom());
            txtGrade.setText(e.getGrade());
            txtEmail.setText(e.getEmail());
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String grade = txtGrade.getText();
        String email = txtEmail.getText();

        if (nom == null || nom.trim().isEmpty() || prenom == null || prenom.trim().isEmpty()) {
            lblError.setText("Le nom et le prénom sont requis.");
            lblError.setVisible(true);
            return;
        }

        try {
            if (encadreur == null) {
                model.Encadreur e = new model.Encadreur(nom, prenom, grade, email);
                encadreurDAO.insert(e);
            } else {
                encadreur.setNom(nom);
                encadreur.setPrenom(prenom);
                encadreur.setGrade(grade);
                encadreur.setEmail(email);
                encadreurDAO.update(encadreur);
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
        if (lblError != null) {
            lblError.setText(title + ": " + details);
            lblError.setVisible(true);
        }
    }
}
