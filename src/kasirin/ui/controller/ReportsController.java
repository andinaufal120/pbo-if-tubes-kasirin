package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for the Reports View
 * Handles sales reports and analytics
 *
 * @author yamaym
 */
public class ReportsController implements Initializable {

    @FXML private Button exportBtn;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeFilter;
    @FXML private Button applyFilterBtn;
    @FXML private Button resetFilterBtn;

    @FXML private Label totalRevenueLabel;
    @FXML private Label revenueChangeLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label transactionChangeLabel;
    @FXML private Label avgOrderLabel;
    @FXML private Label avgOrderChangeLabel;
    @FXML private Label topProductLabel;
    @FXML private Label topProductSalesLabel;

    @FXML private AreaChart<String, Number> revenueChart;
    @FXML private PieChart productChart;

    @FXML private TableView<Object> transactionsTable;
    @FXML private TableColumn<Object, String> transactionIdCol;
    @FXML private TableColumn<Object, String> transactionDateCol;
    @FXML private TableColumn<Object, String> transactionCashierCol;
    @FXML private TableColumn<Object, String> transactionItemsCol;
    @FXML private TableColumn<Object, String> transactionTotalCol;
    @FXML private TableColumn<Object, String> transactionActionsCol;

    @FXML private TextField transactionSearchField;
    @FXML private Button searchTransactionBtn;
    @FXML private Button prevPageBtn;
    @FXML private Label pageInfoLabel;
    @FXML private Button nextPageBtn;

    private User currentUser;
    private Store currentStore;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupFilters();
            setupCharts();
            setupTables();
            setupDatePickers();

            System.out.println("ReportsController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing ReportsController: " + e.getMessage());
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

            loadReportsData();

            System.out.println("Reports loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing reports with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup filters
     */
    private void setupFilters() {
        reportTypeFilter.getItems().addAll("All Reports", "Daily Sales", "Weekly Sales", "Monthly Sales", "Product Performance");
        reportTypeFilter.setValue("All Reports");
    }

    /**
     * Setup date pickers
     */
    private void setupDatePickers() {
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.minusDays(30)); // Last 30 days
        endDatePicker.setValue(today);
    }

    /**
     * Setup charts
     */
    private void setupCharts() {
        // Setup revenue chart
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");

        // Sample data
        revenueSeries.getData().add(new XYChart.Data<>("Week 1", 8500000));
        revenueSeries.getData().add(new XYChart.Data<>("Week 2", 9200000));
        revenueSeries.getData().add(new XYChart.Data<>("Week 3", 7800000));
        revenueSeries.getData().add(new XYChart.Data<>("Week 4", 10500000));

        revenueChart.getData().add(revenueSeries);

        // Setup product chart
        productChart.getData().addAll(
                new PieChart.Data("Coffee", 35),
                new PieChart.Data("Sandwich", 25),
                new PieChart.Data("Juice", 20),
                new PieChart.Data("Cake", 15),
                new PieChart.Data("Others", 5)
        );
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        // Setup transactions table columns
        transactionIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("TXN001"));
        transactionDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("2024-12-06 14:30"));
        transactionCashierCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("John Doe"));
        transactionItemsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("3"));
        transactionTotalCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 125,000"));
        transactionActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("View | Print"));
    }

    /**
     * Load reports data
     */
    private void loadReportsData() {
        try {
            // TODO: Replace with actual service calls

            // Sample data
            totalRevenueLabel.setText("Rp 35,750,000");
            revenueChangeLabel.setText("+12.5% from last period");
            totalTransactionsLabel.setText("1,247");
            transactionChangeLabel.setText("+8.3% from last period");
            avgOrderLabel.setText("Rp 28,650");
            avgOrderChangeLabel.setText("+3.7% from last period");
            topProductLabel.setText("Coffee");
            topProductSalesLabel.setText("435 sold");

            updatePageInfo();

            System.out.println("Reports data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading reports data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update pagination info
     */
    private void updatePageInfo() {
        pageInfoLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    @FXML
    private void exportReport() {
        AlertUtil.showInfo("Export Report", "Report export functionality will be implemented soon!");
    }

    @FXML
    private void applyFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String reportType = reportTypeFilter.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            AlertUtil.showError("Invalid Date Range", "Start date must be before end date.");
            return;
        }

        // TODO: Implement filter logic
        AlertUtil.showInfo("Filter Applied", "Filtering reports from " + startDate + " to " + endDate + " for " + reportType);
        loadReportsData();
    }

    @FXML
    private void resetFilter() {
        setupDatePickers();
        reportTypeFilter.setValue("All Reports");
        loadReportsData();
        AlertUtil.showInfo("Filter Reset", "Filters have been reset to default values.");
    }

    @FXML
    private void searchTransactions() {
        String searchTerm = transactionSearchField.getText().trim();

        // TODO: Implement transaction search
        AlertUtil.showInfo("Search", "Searching transactions for: " + searchTerm);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePageInfo();
            // TODO: Load previous page data
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePageInfo();
            // TODO: Load next page data
        }
    }
}
