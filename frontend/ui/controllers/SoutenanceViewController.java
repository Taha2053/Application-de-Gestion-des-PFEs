/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package frontend.ui.controllers;

import dao.SoutenanceDAO;
import dao.SoutenanceDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.beans.property.ReadOnlyStringWrapper;
import model.Soutenance;

import java.io.IOException;
import java.sql.SQLException;

public class SoutenanceViewController {

    @FXML
    private TableView<Soutenance> tableSoutenances;
    @FXML
    private TableColumn<Soutenance, Integer> colId;
    @FXML
    private TableColumn<Soutenance, String> colPfe;
    @FXML
    private TableColumn<Soutenance, String> colEtudiant;
    @FXML
    private TableColumn<Soutenance, Object> colDate;
    @FXML
    private TableColumn<Soutenance, String> colSalle;
    @FXML
    private TableColumn<Soutenance, Double> colNote;

    @FXML
    private javafx.scene.control.TextField txtSearch;

    private final SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();
    private final ObservableList<Soutenance> data = FXCollections.observableArrayList();
    private final ObservableList<Soutenance> filteredData = FXCollections.observableArrayList();

    @FXML
    private Label lblMessage;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idsoutenance"));
        colPfe.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPfe() != null && cellData.getValue().getPfe().getTitre() != null) {
                return new ReadOnlyStringWrapper(cellData.getValue().getPfe().getTitre());
            }
            return new ReadOnlyStringWrapper("");
        });
        colEtudiant.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPfe() != null && cellData.getValue().getPfe().getEtudiant() != null) {
                String nom = (cellData.getValue().getPfe().getEtudiant().getNom() == null ? ""
                        : cellData.getValue().getPfe().getEtudiant().getNom());
                String prenom = (cellData.getValue().getPfe().getEtudiant().getPrenom() == null ? ""
                        : cellData.getValue().getPfe().getEtudiant().getPrenom());
                return new ReadOnlyStringWrapper((nom + " " + prenom).trim());
            }
            return new ReadOnlyStringWrapper("");
        });
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        tableSoutenances.setItems(filteredData);

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
            for (Soutenance s : data) {
                boolean matches = false;

                if (s.getSalle() != null && s.getSalle().toLowerCase().contains(lowerSearch)) {
                    matches = true;
                }

                if (s.getDate() != null) {
                    String dateStr = s.getDate().toString();
                    if (dateStr.contains(lowerSearch)) {
                        matches = true;
                    }
                }

                if (s.getPfe() != null && s.getPfe().getTitre() != null) {
                    if (s.getPfe().getTitre().toLowerCase().contains(lowerSearch)) {
                        matches = true;
                    }
                }

                if (s.getPfe() != null && s.getPfe().getEtudiant() != null) {
                    String nom = (s.getPfe().getEtudiant().getNom() == null ? "" : s.getPfe().getEtudiant().getNom());
                    String prenom = (s.getPfe().getEtudiant().getPrenom() == null ? ""
                            : s.getPfe().getEtudiant().getPrenom());
                    String full = (nom + " " + prenom).trim().toLowerCase();
                    if (full.contains(lowerSearch)) {
                        matches = true;
                    }
                }

                if (matches) {
                    filteredData.add(s);
                }
            }
        }
    }

    private void loadData() {
        try {
            data.setAll(soutenanceDAO.getAll());
            filterTable(txtSearch != null ? txtSearch.getText() : "");
        } catch (SQLException e) {
            showError("Erreur de chargement des soutenances", e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/frontend/ui/components/AddEditSoutenance.fxml"));
            Parent dialogRoot = loader.load();
            AddEditSoutenanceController ctrl = loader.getController();

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableSoutenances.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            Stage owner = (Stage) tableSoutenances.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        Soutenance selected = tableSoutenances.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune soutenance sélectionnée");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/frontend/ui/components/AddEditSoutenance.fxml"));
            Parent dialogRoot = loader.load();
            AddEditSoutenanceController ctrl = loader.getController();
            ctrl.setSoutenance(selected);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tableSoutenances.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            Stage owner = (Stage) tableSoutenances.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Soutenance selected = tableSoutenances.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune soutenance sélectionnée");
            return;
        }

        try {
            soutenanceDAO.delete(soutenanceDAO.get(selected.getIdsoutenance()));
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
