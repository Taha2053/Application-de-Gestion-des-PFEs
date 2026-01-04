package frontend.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class CompteController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;

    @FXML
    private void initialize() {
        // Load profile from settings or DB if available
    }

    @FXML
    private void onSave(ActionEvent event) {
        // Minimal: show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compte");
        alert.setHeaderText(null);
        alert.setContentText("Informations enregistrées.");
        alert.showAndWait();
    }
}
