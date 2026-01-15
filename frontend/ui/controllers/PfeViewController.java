/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package frontend.ui.controllers;

import dao.PfeDAO;
import dao.PfeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.stream.Collectors;
import model.Pfe;

import java.io.IOException;
import java.sql.SQLException;

public class PfeViewController {

    @FXML
    private TableView<Pfe> tablePfe;
    @FXML
    private TableColumn<Pfe, Integer> colId;
    @FXML
    private TableColumn<Pfe, String> colTitre;
    @FXML
    private TableColumn<Pfe, String> colEtudiant;
    @FXML
    private TableColumn<Pfe, String> colEncadreurs;
    @FXML
    private TableColumn<Pfe, String> colEtat;
    @FXML
    private TableColumn<Pfe, Object> colDate;

    @FXML
    private javafx.scene.control.TextField txtSearch;

    private final PfeDAO pfeDAO = new PfeDAOImpl();
    private final ObservableList<Pfe> data = FXCollections.observableArrayList();
    private final ObservableList<Pfe> filteredData = FXCollections.observableArrayList();

    @FXML
    private Label lblMessage;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idpfe"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colEtudiant.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEtudiant() != null) {
                String full = (cellData.getValue().getEtudiant().getNom() == null ? ""
                        : cellData.getValue().getEtudiant().getNom())
                        + " " + (cellData.getValue().getEtudiant().getPrenom() == null ? ""
                                : cellData.getValue().getEtudiant().getPrenom());
                return new ReadOnlyStringWrapper(full.trim());
            }
            return new ReadOnlyStringWrapper("");
        });
        colEncadreurs.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEncadreur() != null && !cellData.getValue().getEncadreur().isEmpty()) {
                String joined = cellData.getValue().getEncadreur().stream()
                        .map(e -> (e.getNom() == null ? "" : e.getNom()) + " "
                                + (e.getPrenom() == null ? "" : e.getPrenom()))
                        .collect(Collectors.joining(", "));
                return new ReadOnlyStringWrapper(joined);
            }
            return new ReadOnlyStringWrapper("");
        });
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSoutenance"));

        tablePfe.setItems(filteredData);

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
            for (Pfe pfe : data) {
                String etu = "";
                if (pfe.getEtudiant() != null) {
                    etu = ((pfe.getEtudiant().getNom() == null) ? "" : pfe.getEtudiant().getNom()) + " "
                            + ((pfe.getEtudiant().getPrenom() == null) ? "" : pfe.getEtudiant().getPrenom());
                }
                String encs = "";
                if (pfe.getEncadreur() != null) {
                    encs = pfe.getEncadreur().stream()
                            .map(e -> ((e.getNom() == null) ? "" : e.getNom()) + " "
                                    + ((e.getPrenom() == null) ? "" : e.getPrenom()))
                            .collect(Collectors.joining(" "));
                }
                String combined = ((pfe.getTitre() == null ? "" : pfe.getTitre()) + " "
                        + (pfe.getEtat() == null ? "" : pfe.getEtat()) + " " + etu + " " + encs).toLowerCase();
                if (combined.contains(lowerSearch)) {
                    filteredData.add(pfe);
                }
            }
        }
    }

    private void loadData() {
        try {
            data.setAll(pfeDAO.getAll());
            filterTable(txtSearch != null ? txtSearch.getText() : "");
        } catch (SQLException e) {
            showError("Erreur de chargement des PFE", e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditPfe.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditPfeController ctrl = loader.getController();

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tablePfe.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            Stage owner = (Stage) tablePfe.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        Pfe selected = tablePfe.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun PFE sélectionné");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/ui/components/AddEditPfe.fxml"));
            javafx.scene.Parent dialogRoot = loader.load();
            AddEditPfeController ctrl = loader.getController();
            ctrl.setPfe(selected);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            overlay.setPickOnBounds(true);
            StackPane.setAlignment(dialogRoot, javafx.geometry.Pos.CENTER);
            overlay.getChildren().add(dialogRoot);

            StackPane contentArea = (StackPane) tablePfe.getScene().lookup("#contentArea");
            contentArea.getChildren().add(overlay);
            ctrl.setModalOverlay(overlay);
            ctrl.setOnSaved(() -> {
                loadData();
                if (MainLayoutController.INSTANCE != null)
                    MainLayoutController.INSTANCE.refreshSoutenances();
            });

            Stage owner = (Stage) tablePfe.getScene().getWindow();
            owner.setMaximized(true);

            dialogRoot.requestFocus();
        } catch (IOException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Pfe selected = tablePfe.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucun PFE sélectionné");
            return;
        }

        try {
            pfeDAO.delete(pfeDAO.get(selected.getIdpfe()));
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
