package frontend.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/frontend/ui/components/Login.fxml"));

        // 1. Load the root without defining fixed size
        Parent root = loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/frontend/ui/css/style.css").toExternalForm());

        stage.setTitle("Application de Gestion des PFE");
        stage.setScene(scene);

        // 2. Force the window to be maximized (Full Screen but keeps the taskbar)
        stage.setMaximized(true); 

        // Optional: If you want "Kiosk" mode (no taskbar, no title bar):
        // stage.setFullScreen(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}