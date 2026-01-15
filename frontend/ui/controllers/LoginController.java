package frontend.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Scene;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private javafx.scene.control.Label lblError;

    @FXML
    private void onLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (lblError != null) {
            lblError.setText("");
            lblError.setVisible(false);
        }

        if ("admin@example.com".equals(email) && "admin".equals(password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/MainLayout.fxml"));
                Pane root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1000, 600);
                scene.getStylesheets().add(getClass().getResource("/frontend/ui/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setMaximized(true);
            } catch (IOException ex) {
                if (lblError != null) {
                    lblError.setText("Erreur lors de l'ouverture de l'application: " + ex.getMessage());
                    lblError.setVisible(true);
                    return;
                }
                showError("Erreur lors de l'ouverture de l'application", ex.getMessage());
            }
        } else {
            if (lblError != null) {
                lblError.setText("Email ou mot de passe incorrect.");
                lblError.setVisible(true);
                return;
            }
            showError("Échec de l'authentification", "Email ou mot de passe incorrect.");
        }
    }

    private void showError(String title, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
