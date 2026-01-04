/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package frontend.ui.controllers;

import dao.EtudiantDAO;
import dao.EtudiantDAOimpl;
import dao.EncadreurDAO;
import dao.EncadreurDAOImpl;
import dao.PfeDAO;
import dao.PfeDAOImpl;
import dao.SoutenanceDAO;
import dao.SoutenanceDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import model.Pfe;
import model.Soutenance;

public class DashboardController implements Initializable {

    @FXML
    private Label lblEtudiants;

    @FXML
    private Label lblEncadreurs;

    @FXML
    private Label lblPfe;

    @FXML
    private Label lblSoutenances;

    @FXML
    private LineChart<String, Number> lineChartSoutenances;

    @FXML
    private CategoryAxis xAxisMonths;

    @FXML
    private NumberAxis yAxisCount;

    @FXML
    private PieChart pieChartEtat;

    @FXML
    private BarChart<String, Number> barChartNotes;

    @FXML
    private CategoryAxis xAxisNotes;

    @FXML
    private NumberAxis yAxisNotesCount;

    private final EtudiantDAO etudiantDAO = new EtudiantDAOimpl();
    private final EncadreurDAO encadreurDAO = new EncadreurDAOImpl();
    private final PfeDAO pfeDAO = new PfeDAOImpl();
    private final SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refresh();
        populateCharts();
    }

    private void refresh() {
        try {
            lblEtudiants.setText(String.valueOf(etudiantDAO.getAll().size()));
            lblEncadreurs.setText(String.valueOf(encadreurDAO.getAll().size()));
            lblPfe.setText(String.valueOf(pfeDAO.getAll().size()));
            lblSoutenances.setText(String.valueOf(soutenanceDAO.getAll().size()));
        } catch (SQLException e) {
            showError("Erreur de chargement du tableau de bord", e.getMessage());
        }
    }

    private void populateCharts() {
        try {
            List<Soutenance> soutenances = soutenanceDAO.getAll();
            List<Pfe> pfeList = pfeDAO.getAll();

            // Populate line chart - Soutenances per month
            populateLineChart(soutenances);

            // Populate pie chart - Distribution by etat
            populatePieChart(pfeList);

            // Populate bar chart - Notes distribution
            populateBarChart(soutenances);

        } catch (SQLException e) {
            showError("Erreur de chargement des graphiques", e.getMessage());
        }
    }

    private void populateLineChart(List<Soutenance> soutenances) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Soutenances");

        // Group by month for last 12 months
        Map<String, Long> monthlyCount = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            String monthKey = monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH));
            monthlyCount.put(monthKey, 0L);
        }

        for (Soutenance s : soutenances) {
            if (s.getDate() != null) {
                LocalDate date = s.getDate();
                String monthKey = date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH));
                monthlyCount.put(monthKey, monthlyCount.getOrDefault(monthKey, 0L) + 1);
            }
        }

        for (Map.Entry<String, Long> entry : monthlyCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChartSoutenances.getData().clear();
        lineChartSoutenances.getData().add(series);
    }

    private void populatePieChart(List<Pfe> pfeList) {
        Map<String, Long> etatCount = pfeList.stream()
            .filter(p -> p.getEtat() != null && !p.getEtat().isEmpty())
            .collect(Collectors.groupingBy(Pfe::getEtat, Collectors.counting()));

        pieChartEtat.getData().clear();
        for (Map.Entry<String, Long> entry : etatCount.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChartEtat.getData().add(data);
        }
    }

    private void populateBarChart(List<Soutenance> soutenances) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Notes");

        // Define note ranges
        String[] ranges = {"0-5", "5-10", "10-12", "12-14", "14-16", "16-18", "18-20"};
        Map<String, Long> rangeCount = new LinkedHashMap<>();
        
        for (String range : ranges) {
            rangeCount.put(range, 0L);
        }

        for (Soutenance s : soutenances) {
            double note = s.getNote();
            String range;
            if (note < 5) range = "0-5";
            else if (note < 10) range = "5-10";
            else if (note < 12) range = "10-12";
            else if (note < 14) range = "12-14";
            else if (note < 16) range = "14-16";
            else if (note < 18) range = "16-18";
            else range = "18-20";
            
            rangeCount.put(range, rangeCount.get(range) + 1);
        }

        for (Map.Entry<String, Long> entry : rangeCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartNotes.getData().clear();
        barChartNotes.getData().add(series);
    }

    private void showError(String title, String details) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
