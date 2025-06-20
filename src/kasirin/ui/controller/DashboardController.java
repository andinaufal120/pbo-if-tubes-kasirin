package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import kasirin.data.model.Store;
import kasirin.data.model.User;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Dashboard View
 * Displays store analytics and summary information
 *
 * @author yamaym
 */
public class DashboardController implements Initializable {

    @FXML private Label dateLabel;
    @FXML private Label todaySalesLabel;
    @FXML private Label transactionCountLabel;
    @FXML private Label productCountLabel;
    @FXML private Label lowStockLabel;
    @FXML private LineChart<String, Number> salesChart;
    @FXML private ListView<String> topProductsList;
    @FXML private TableView<Object> recentTransactionsTable;
    @FXML private TableColumn<Object, String> transactionIdCol;
    @FXML private TableColumn<Object, String> transactionTimeCol;
    @FXML private TableColumn<Object, String> transactionTotalCol;
    @FXML private TableColumn<Object, String> transactionUserCol;

    private User currentUser;
    private Store currentStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupDateLabel();
            setupCharts();
            setupTables();

            System.out.println("DashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing DashboardController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with user and store data
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            loadDashboardData();

            System.out.println("Dashboard loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing dashboard with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup date label with current date
     */
    private void setupDateLabel() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateLabel.setText(now.format(formatter));
    }

    /**
     * Setup charts with sample data
     */
    private void setupCharts() {
        // Setup sales chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        // Sample data - replace with actual data from service
        series.getData().add(new XYChart.Data<>("Mon", 1200000));
        series.getData().add(new XYChart.Data<>("Tue", 1500000));
        series.getData().add(new XYChart.Data<>("Wed", 1800000));
        series.getData().add(new XYChart.Data<>("Thu", 1300000));
        series.getData().add(new XYChart.Data<>("Fri", 2100000));
        series.getData().add(new XYChart.Data<>("Sat", 2500000));
        series.getData().add(new XYChart.Data<>("Sun", 1900000));

        salesChart.getData().add(series);

        // Setup top products list
        topProductsList.getItems().addAll(
                "1. Coffee - 45 sold",
                "2. Sandwich - 32 sold",
                "3. Juice - 28 sold",
                "4. Cake - 21 sold",
                "5. Tea - 18 sold"
        );
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        // Setup table columns
        transactionIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("TXN001"));
        transactionTimeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("14:30"));
        transactionTotalCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 125,000"));
        transactionUserCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("John Doe"));
    }

    /**
     * Load dashboard data from services
     */
    private void loadDashboardData() {
        try {
            // TODO: Replace with actual service calls

            // Sample data - replace with actual calculations
            todaySalesLabel.setText("Rp 2,450,000");
            transactionCountLabel.setText("47");
            productCountLabel.setText("156");
            lowStockLabel.setText("3");

            System.out.println("Dashboard data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
