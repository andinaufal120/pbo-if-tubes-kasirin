package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.*;
import kasirin.data.model.Store;
import kasirin.data.model.Transaction;
import kasirin.data.model.User;
import kasirin.service.TransactionService;
import kasirin.service.UserService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Controller for the Reports View
 * Handles sales reports and analytics with real data from database
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

    @FXML private TableView<TransactionReportItem> transactionsTable;
    @FXML private TableColumn<TransactionReportItem, String> transactionIdCol;
    @FXML private TableColumn<TransactionReportItem, String> transactionDateCol;
    @FXML private TableColumn<TransactionReportItem, String> transactionCashierCol;
    @FXML private TableColumn<TransactionReportItem, Integer> transactionItemsCol;
    @FXML private TableColumn<TransactionReportItem, String> transactionTotalCol;
    @FXML private TableColumn<TransactionReportItem, Void> transactionActionsCol;

    @FXML private TextField transactionSearchField;
    @FXML private Button searchTransactionBtn;
    @FXML private Button prevPageBtn;
    @FXML private Label pageInfoLabel;
    @FXML private Button nextPageBtn;

    private User currentUser;
    private Store currentStore;
    private TransactionService transactionService;
    private UserService userService;
    private ObservableList<TransactionReportItem> transactionItems;
    private List<Transaction> allTransactions;
    private int currentPage = 1;
    private int itemsPerPage = 20;
    private int totalPages = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            transactionService = new TransactionService();
            userService = new UserService();
            transactionItems = FXCollections.observableArrayList();

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
        // Charts will be populated with real data in loadReportsData()
        revenueChart.setTitle("Revenue Trend");
        productChart.setTitle("Product Sales Distribution");
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        // Setup transactions table columns
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        transactionCashierCol.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
        transactionItemsCol.setCellValueFactory(new PropertyValueFactory<>("itemCount"));
        transactionTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalFormatted"));

        // Setup actions column
        transactionActionsCol.setCellFactory(col -> {
            TableCell<TransactionReportItem, Void> cell = new TableCell<TransactionReportItem, Void>() {
                private final Button viewBtn = new Button("View");
                private final Button printBtn = new Button("Print");

                {
                    viewBtn.getStyleClass().add("small-button");
                    printBtn.getStyleClass().add("small-button");

                    viewBtn.setOnAction(event -> {
                        TransactionReportItem item = getTableView().getItems().get(getIndex());
                        viewTransactionDetails(item);
                    });

                    printBtn.setOnAction(event -> {
                        TransactionReportItem item = getTableView().getItems().get(getIndex());
                        printReceipt(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(viewBtn, printBtn);
                        setGraphic(buttons);
                    }
                }
            };
            return cell;
        });

        transactionsTable.setItems(transactionItems);
    }

    /**
     * Load reports data from database
     */
    private void loadReportsData() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null || endDate == null) {
                setupDatePickers();
                startDate = startDatePicker.getValue();
                endDate = endDatePicker.getValue();
            }

            Date sqlStartDate = Date.valueOf(startDate);
            Date sqlEndDate = Date.valueOf(endDate);

            // Load transactions for the store
            allTransactions = transactionService.getTransactionHistory(currentStore.getId())
                    .stream()
                    .filter(t -> {
                        Date transactionDate = new Date(t.getTimestamp().getTime());
                        return !transactionDate.before(sqlStartDate) && !transactionDate.after(sqlEndDate);
                    })
                    .collect(Collectors.toList());

            // Calculate statistics
            calculateStatistics(allTransactions);

            // Update charts
            updateCharts(allTransactions);

            // Load transaction table
            loadTransactionTable();

            System.out.println("Reports data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading reports data: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load reports data: " + e.getMessage());
        }
    }

    /**
     * Calculate and display statistics
     */
    private void calculateStatistics(List<Transaction> transactions) {
        // Total revenue
        double totalRevenue = transactions.stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
        totalRevenueLabel.setText(String.format("Rp %,.0f", totalRevenue));

        // Transaction count
        int transactionCount = transactions.size();
        totalTransactionsLabel.setText(String.valueOf(transactionCount));

        // Average order value
        double avgOrder = transactionCount > 0 ? totalRevenue / transactionCount : 0;
        avgOrderLabel.setText(String.format("Rp %,.0f", avgOrder));

        // Calculate percentage changes (comparing with previous period)
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

        LocalDate prevStartDate = startDate.minusDays(daysDiff + 1);
        LocalDate prevEndDate = startDate.minusDays(1);

        Date sqlPrevStartDate = Date.valueOf(prevStartDate);
        Date sqlPrevEndDate = Date.valueOf(prevEndDate);

        List<Transaction> prevTransactions = transactionService.getTransactionHistory(currentStore.getId())
                .stream()
                .filter(t -> {
                    Date transactionDate = new Date(t.getTimestamp().getTime());
                    return !transactionDate.before(sqlPrevStartDate) && !transactionDate.after(sqlPrevEndDate);
                })
                .collect(Collectors.toList());

        double prevRevenue = prevTransactions.stream().mapToDouble(Transaction::getTotal).sum();
        int prevTransactionCount = prevTransactions.size();

        // Calculate percentage changes
        double revenueChange = prevRevenue > 0 ? ((totalRevenue - prevRevenue) / prevRevenue) * 100 : 0;
        double transactionChange = prevTransactionCount > 0 ? ((double)(transactionCount - prevTransactionCount) / prevTransactionCount) * 100 : 0;

        revenueChangeLabel.setText(String.format("%+.1f%% from previous period", revenueChange));
        transactionChangeLabel.setText(String.format("%+.1f%% from previous period", transactionChange));
        avgOrderChangeLabel.setText("N/A"); // Can be calculated if needed

        // Top product (placeholder - would need transaction details analysis)
        topProductLabel.setText("Analysis needed");
        topProductSalesLabel.setText("Requires detail data");
    }

    /**
     * Update charts with real data
     */
    private void updateCharts(List<Transaction> transactions) {
        // Update revenue chart
        revenueChart.getData().clear();
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Daily Revenue");

        // Group transactions by date
        Map<LocalDate, Double> dailyRevenue = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().toLocalDateTime().toLocalDate(),
                        Collectors.summingDouble(Transaction::getTotal)
                ));

        // Add data points for the last 7 days
        LocalDate endDate = endDatePicker.getValue();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            double revenue = dailyRevenue.getOrDefault(date, 0.0);
            revenueSeries.getData().add(new XYChart.Data<>(
                    date.format(DateTimeFormatter.ofPattern("MM/dd")),
                    revenue
            ));
        }

        revenueChart.getData().add(revenueSeries);

        // Update product chart (placeholder data)
        productChart.getData().clear();
        productChart.getData().addAll(
                new PieChart.Data("Product Analysis", 100)
        );
    }

    /**
     * Load transaction table with pagination
     */
    private void loadTransactionTable() {
        try {
            transactionItems.clear();

            // Convert transactions to report items
            List<TransactionReportItem> reportItems = new ArrayList<>();
            for (Transaction transaction : allTransactions) {
                User cashier = userService.getUserById(transaction.getUserID());
                String cashierName = cashier != null ? cashier.getName() : "Unknown";

                TransactionReportItem item = new TransactionReportItem(
                        transaction.getId(),
                        transaction.getTimestamp().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        cashierName,
                        1, // Placeholder - would need transaction details count
                        transaction.getTotal()
                );
                reportItems.add(item);
            }

            // Calculate pagination
            totalPages = (int) Math.ceil((double) reportItems.size() / itemsPerPage);
            if (totalPages == 0) totalPages = 1;

            // Get items for current page
            int startIndex = (currentPage - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, reportItems.size());

            if (startIndex < reportItems.size()) {
                transactionItems.addAll(reportItems.subList(startIndex, endIndex));
            }

            updatePageInfo();

        } catch (Exception e) {
            System.err.println("Error loading transaction table: " + e.getMessage());
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

        currentPage = 1; // Reset to first page
        loadReportsData();
        AlertUtil.showInfo("Filter Applied", "Reports filtered successfully.");
    }

    @FXML
    private void resetFilter() {
        setupDatePickers();
        reportTypeFilter.setValue("All Reports");
        currentPage = 1;
        loadReportsData();
        AlertUtil.showInfo("Filter Reset", "Filters have been reset to default values.");
    }

    @FXML
    private void searchTransactions() {
        String searchTerm = transactionSearchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadTransactionTable();
            return;
        }

        // Filter transactions based on search term
        List<TransactionReportItem> filteredItems = transactionItems.stream()
                .filter(item ->
                        String.valueOf(item.getTransactionId()).contains(searchTerm) ||
                                item.getCashierName().toLowerCase().contains(searchTerm) ||
                                item.getTransactionDate().toLowerCase().contains(searchTerm)
                )
                .collect(Collectors.toList());

        transactionItems.clear();
        transactionItems.addAll(filteredItems);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadTransactionTable();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadTransactionTable();
        }
    }

    /**
     * View transaction details
     */
    private void viewTransactionDetails(TransactionReportItem item) {
        AlertUtil.showInfo("Transaction Details",
                "Transaction ID: " + item.getTransactionId() + "\n" +
                        "Date: " + item.getTransactionDate() + "\n" +
                        "Cashier: " + item.getCashierName() + "\n" +
                        "Total: " + item.getTotalFormatted());
    }

    /**
     * Print receipt
     */
    private void printReceipt(TransactionReportItem item) {
        AlertUtil.showInfo("Print Receipt", "Receipt printing for transaction " + item.getTransactionId() + " will be implemented soon!");
    }

    /**
     * Inner class to represent transaction report items
     */
    public static class TransactionReportItem {
        private int transactionId;
        private String transactionDate;
        private String cashierName;
        private int itemCount;
        private double total;

        public TransactionReportItem(int transactionId, String transactionDate, String cashierName,
                                     int itemCount, double total) {
            this.transactionId = transactionId;
            this.transactionDate = transactionDate;
            this.cashierName = cashierName;
            this.itemCount = itemCount;
            this.total = total;
        }

        // Getters
        public int getTransactionId() { return transactionId; }
        public String getTransactionDate() { return transactionDate; }
        public String getCashierName() { return cashierName; }
        public int getItemCount() { return itemCount; }
        public double getTotal() { return total; }

        public String getTotalFormatted() {
            return String.format("Rp %,.0f", total);
        }
    }
}
