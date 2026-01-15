package frontend.ui.controllers;

import dao.EncadreurDAO;
import dao.EncadreurDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.Encadreur;

import java.io.IOException;
import java.sql.SQLException;

public class EncadreurViewController {

    @FXML
    private TableView<Encadreur> tableEncadreurs;

    @FXML
    private TableColumn<Encadreur, Integer> colId;
    @FXML
    private TableColumn<Encadreur, String> colNom;
    @FXML
    private TableColumn<Encadreur, String> colPrenom;
    @FXML
    private TableColumn<Encadreur, String> colGrade;
    @FXML
    private TableColumn<Encadreur, String> colEmail;

    @FXML
    private javafx.scene.control.TextField txtSearch;

    private final EncadreurDAO encadreurDAO = new EncadreurDAOImpl();
    private final ObservableList<Encadreur> data = FXCollections.observableArrayList();
    private final ObservableList<Encadreur> filteredData = FXCollections.observableArrayList();

    @FXML
    private Label lblMessage;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idencadreur"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableEncadreurs.setItems(filteredData);

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTable(newValue);
            });
        }

        loadData();
    }

    private void filterTable(String searchText) {
        filteredData.clear();
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredData.addAll(data);
        } else {
            String lowerSearch = searchText.toLowerCase();
            for (Encadreur encadreur : data) {
                if ((encadreur.getNom() != null && encadreur.getNom().toLowerCase().contains(lowerSearch)) ||
                        (encadreur.getPrenom() != null && encadreur.getPrenom().toLowerCase().contains(lowerSearch)) ||
                        (encadreur.getEmail() != null && encadreur.getEmail().toLowerCase().contains(lowerSearch)) ||
                        (encadreur.getGrade() != null && encadreur.getGrade().toLowerCase().contains(lowerSearch))) {
                    filteredData.add(encadreur);
                }
            }
        }
    }

    private void loadData() {
        try {
            data.setAll(encadreurDAO.getAll());
            filterTable(txtSearch != null ? txtSearch.getText() : "");
        } catch (SQLException e) {
            showError("Erreur de chargement des encadreurs", e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditEncadreur.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditEncadreurController ctrl = loader.getController();

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableEncadreurs.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            // keep main window maximized
            Stage owner = (Stage) tableEncadreurs.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        Encadreur selected = tableEncadreurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun encadreur sélectionné");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditEncadreur.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditEncadreurController ctrl = loader.getController();
            ctrl.setEncadreur(selected);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableEncadreurs.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            Stage owner = (Stage) tableEncadreurs.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Encadreur selected = tableEncadreurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun encadreur sélectionné");
            return;
        }

        try {
            encadreurDAO.delete(encadreurDAO.get(selected.getIdencadreur()));
            loadData();
            if (MainLayoutController.INSTANCE != null)
                MainLayoutController.INSTANCE.refreshSoutenances();
        } catch (SQLException e) {
            showError("Erreur lors de la suppression", e.getMessage());
        }
    }

    private void showWarning(String message) {
        if (lblMessage != null) {
            lblMessage.setText(message);
            lblMessage.setVisible(true);
            PauseTransition pt = new PauseTransition(Duration.seconds(4));
            pt.setOnFinished(e -> lblMessage.setVisible(false));
            pt.play();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
