package frontend.ui.controllers;

import dao.EtudiantDAO;
import dao.EtudiantDAOimpl;
import model.Etudiant;
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

import java.io.IOException;
import java.sql.SQLException;

public class EtudiantViewController {

    @FXML
    private TableView<Etudiant> tableEtudiants;

    @FXML
    private TableColumn<Etudiant, Integer> colId;

    @FXML
    private TableColumn<Etudiant, String> colNom;

    @FXML
    private TableColumn<Etudiant, String> colPrenom;

    @FXML
    private TableColumn<Etudiant, String> colEmail;

    @FXML
    private TableColumn<Etudiant, String> colClasse;

    @FXML
    private javafx.scene.control.TextField txtSearch;

    private final EtudiantDAO etudiantDAO = new EtudiantDAOimpl();
    private final ObservableList<Etudiant> data = FXCollections.observableArrayList();
    private final ObservableList<Etudiant> filteredData = FXCollections.observableArrayList();

    @FXML
    private Label lblMessage;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idetudiant"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));

        tableEtudiants.setItems(filteredData);

        // Add filter listener
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
            for (Etudiant etudiant : data) {
                if ((etudiant.getNom() != null && etudiant.getNom().toLowerCase().contains(lowerSearch)) ||
                        (etudiant.getPrenom() != null && etudiant.getPrenom().toLowerCase().contains(lowerSearch)) ||
                        (etudiant.getEmail() != null && etudiant.getEmail().toLowerCase().contains(lowerSearch)) ||
                        (etudiant.getClasse() != null && etudiant.getClasse().toLowerCase().contains(lowerSearch))) {
                    filteredData.add(etudiant);
                }
            }
        }
    }

    private void loadData() {
        try {
            data.setAll(etudiantDAO.getAll());
            filterTable(txtSearch != null ? txtSearch.getText() : "");
        } catch (SQLException e) {
            showError("Erreur de chargement des étudiants", e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditEtudiant.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditEtudiantController ctrl = loader.getController();

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableEtudiants.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            // keep main window maximized
            Stage owner = (Stage) tableEtudiants.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun étudiant sélectionné");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditEtudiant.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(selected);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableEtudiants.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            // keep main window maximized
            Stage owner = (Stage) tableEtudiants.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun étudiant sélectionné");
            return;
        }

        try {
            etudiantDAO.delete(etudiantDAO.get(selected.getIdetudiant()));
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
