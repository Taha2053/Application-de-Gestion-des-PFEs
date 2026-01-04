package frontend.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.event.ActionEvent;

public class SettingsController {

    @FXML
    private ChoiceBox<String> choiceTheme;
    @FXML
    private ChoiceBox<String> choiceLang;

    @FXML
    private void initialize() {
        choiceTheme.getItems().addAll("Clair", "Sombre");
        choiceLang.getItems().addAll("Français", "Anglais");
    }

    @FXML
    private void onSave(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres");
        alert.setHeaderText(null);
        alert.setContentText("Paramètres enregistrés.");
        alert.showAndWait();
    }
}
