package frontend.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

import dao.SoutenanceDAO;
import dao.SoutenanceDAOImpl;
import model.Soutenance;

public class MainLayoutController {

    public static volatile MainLayoutController INSTANCE;

    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblPageTitle;
    @FXML
    private VBox navContainer;
    @FXML
    private VBox scheduleList;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label calendarMonthLabel;
    @FXML
    private Button btnPrevMonth;
    @FXML
    private Button btnToday;
    @FXML
    private Button btnNextMonth;

    private final SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();
    private YearMonth currentMonth;
    private Map<LocalDate, Integer> soutenanceCountByDate = new HashMap<>();

    @FXML
    public void initialize() {
        INSTANCE = this;

        currentMonth = YearMonth.now();
        onDashboard(null);
        loadRecentSoutenances();
        loadCalendar();
    }


    public void refreshSoutenances() {
        loadRecentSoutenances();
        loadCalendar();
    }

    private void loadRecentSoutenances() {
        try {
            List<Soutenance> allSoutenances = soutenanceDAO.getAll();

            LocalDate today = LocalDate.now();
            List<Soutenance> upcoming = allSoutenances.stream()
                    .filter(s -> s.getDate() != null && !s.getDate().isBefore(today))
                    .sorted(Comparator.comparing(Soutenance::getDate))
                    .toList();
            List<Soutenance> past = allSoutenances.stream()
                    .filter(s -> s.getDate() == null || s.getDate().isBefore(today))
                    .sorted(Comparator.comparing(Soutenance::getDate).reversed())
                    .toList();

            List<Soutenance> ordered = new java.util.ArrayList<>();
            ordered.addAll(upcoming);
            ordered.addAll(past);

            soutenanceCountByDate.clear();
            for (Soutenance s : allSoutenances) {
                if (s.getDate() != null) {
                    soutenanceCountByDate.merge(s.getDate(), 1, Integer::sum);
                }
            }

            scheduleList.getChildren().clear();

            int count = 0;
            for (Soutenance s : ordered) {
                if (count >= 10)
                    break; 

                VBox item = new VBox(5);
                item.getStyleClass().add("schedule-item");

                HBox dateRow = new HBox(10);
                Label dateLabel = new Label(formatDate(s.getDate()));
                dateLabel.getStyleClass().add("time-label");
                dateRow.getChildren().add(dateLabel);

                VBox infoBox = new VBox(3);
                String projectTitle = s.getPfe() != null && s.getPfe().getTitre() != null
                        ? s.getPfe().getTitre()
                        : "Projet non spécifié";
                Label titleLabel = new Label(projectTitle);
                titleLabel.getStyleClass().add("event-title");
                titleLabel.setWrapText(true);

                String student = "";
                if (s.getPfe() != null && s.getPfe().getEtudiant() != null) {
                    student = (s.getPfe().getEtudiant().getNom() == null ? "" : s.getPfe().getEtudiant().getNom()) + " "
                            +
                            (s.getPfe().getEtudiant().getPrenom() == null ? "" : s.getPfe().getEtudiant().getPrenom());
                }
                Label extra = new Label(
                        (student.isEmpty() ? "" : student) + (s.getSalle() != null ? " • " + s.getSalle() : ""));
                extra.getStyleClass().add("event-tag");

                infoBox.getChildren().addAll(titleLabel, extra);
                item.getChildren().addAll(dateRow, infoBox);

                scheduleList.getChildren().add(item);
                count++;
            }

            if (scheduleList.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Aucune soutenance récente");
                emptyLabel.getStyleClass().add("help-text-small");
                scheduleList.getChildren().add(emptyLabel);
            }
        } catch (SQLException e) {
        }
    }

    private void loadCalendar() {
        calendarGrid.getChildren().clear();

        try {
            List<Soutenance> all = soutenanceDAO.getAll();
            soutenanceCountByDate.clear();
            for (Soutenance s : all) {
                if (s.getDate() != null) {
                    soutenanceCountByDate.merge(s.getDate(), 1, Integer::sum);
                }
            }
        } catch (SQLException e) {
        }

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        calendarMonthLabel.setText(currentMonth.format(monthFormatter));

        String[] dayHeaders = { "L", "M", "M", "J", "V", "S", "D" };
        for (int i = 0; i < 7; i++) {
            Label dayHeader = new Label(dayHeaders[i]);
            dayHeader.getStyleClass().add("calendar-day-header");
            dayHeader.setMaxWidth(Double.MAX_VALUE);
            dayHeader.setAlignment(Pos.CENTER);
            calendarGrid.add(dayHeader, i, 0);
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int dayOfWeek = firstDay.getDayOfWeek().getValue() - 1; // Monday = 0

        int row = 1;
        int col = dayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);

            VBox dayCell = new VBox(2);
            dayCell.setAlignment(Pos.CENTER);
            dayCell.getStyleClass().add("calendar-day-cell");

            boolean hasSoutenance = soutenanceCountByDate.containsKey(date);
            boolean isToday = date.equals(LocalDate.now());

            if (isToday) {
                dayCell.getStyleClass().add("calendar-today");
            }
            if (hasSoutenance) {
                dayCell.getStyleClass().add("calendar-has-event");
            }

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("calendar-day-number");
            dayCell.getChildren().add(dayLabel);

            if (hasSoutenance) {
                int count = soutenanceCountByDate.get(date);
                Label indicator = new Label("•".repeat(Math.min(count, 3)));
                indicator.getStyleClass().add("calendar-event-indicator");
                dayCell.getChildren().add(indicator);
            }

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void onPrevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        loadCalendar();
    }

    @FXML
    private void onNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        loadCalendar();
    }

    @FXML
    private void onToday() {
        currentMonth = YearMonth.now();
        loadCalendar();
    }

    private String formatDate(LocalDate date) {
        if (date == null)
            return "Date non spécifiée";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH);
        return date.format(formatter);
    }

    private void updateUIState(ActionEvent event, String title) {
        lblPageTitle.setText(title);

        if (event != null && event.getSource() instanceof Button) {
            VBox sidebar = (VBox) navContainer.getParent();

            sidebar.lookupAll(".nav-button").forEach(node -> {
                node.getStyleClass().remove("active");
            });

            ((Button) event.getSource()).getStyleClass().add("active");
        }
    }

    @FXML
    private void onDashboard(ActionEvent event) {
        updateUIState(event, "Tableau de Bord");
        loadFeature("/frontend/ui/components/DashboardView.fxml");
    }

    @FXML
    private void onEtudiants(ActionEvent event) {
        updateUIState(event, "Gestion des Étudiants");
        loadFeature("/frontend/ui/components/EtudiantView.fxml");
    }

    @FXML
    private void onEncadreurs(ActionEvent event) {
        updateUIState(event, "Gestion des Encadreurs");
        loadFeature("/frontend/ui/components/EncadreursView.fxml");
    }

    @FXML
    private void onPfe(ActionEvent event) {
        updateUIState(event, "Gestion des PFEs");
        loadFeature("/frontend/ui/components/PfeView.fxml");
    }

    @FXML
    private void onSoutenances(ActionEvent event) {
        updateUIState(event, "Gestion des Soutenances");
        loadFeature("/frontend/ui/components/SoutenancesView.fxml");
    }

    @FXML
    private void onHelp(ActionEvent event) {
        updateUIState(event, "Centre d'Aide");
        loadFeature("/frontend/ui/components/HelpView.fxml");
    }

    @FXML
    private void onSettings(ActionEvent event) {
        updateUIState(event, "Paramètres");
        loadFeature("/frontend/ui/components/SettingsView.fxml");
    }

    private void loadFeature(String resource) {
        try {
            contentArea.getChildren().clear();
            java.net.URL url = getClass().getResource(resource);
            if (url == null) {
                showError("Ressource introuvable", "Impossible de trouver : " + resource);
                return;
            }
            Pane pane = FXMLLoader.load(url);
            pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pane.prefWidthProperty().bind(contentArea.widthProperty());
            pane.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().add(pane);
        } catch (IOException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}