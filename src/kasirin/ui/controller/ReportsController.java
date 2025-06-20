package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.SalesReportService;
import kasirin.service.SalesReportService.SalesReportData;
import kasirin.service.SalesReportService.ItemizedSales;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Simplified Reports Controller for basic sales reporting
 * Works with simplified FXML structure
 *
 * @author yamaym
 */
public class ReportsController implements Initializable {

    // Summary Cards - only the ones that exist in FXML
    @FXML private Label totalRevenueLabel;
    @FXML private Label revenueChangeLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label transactionChangeLabel;
    @FXML private Label totalItemsLabel;

    // Table components that exist in FXML
    @FXML private TableView<ItemizedSalesItem> itemizedTable;
    @FXML private TableColumn<ItemizedSalesItem, String> productNameCol;
    @FXML private TableColumn<ItemizedSalesItem, String> categoryCol;
    @FXML private TableColumn<ItemizedSalesItem, Integer> quantityCol;
    @FXML private TableColumn<ItemizedSalesItem, String> revenueCol;
    @FXML private TableColumn<ItemizedSalesItem, Integer> transactionsCol;
    @FXML private TableColumn<ItemizedSalesItem, String> avgPriceCol;

    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button prevPageBtn;
    @FXML private Label pageInfoLabel;
    @FXML private Button nextPageBtn;

    private User currentUser;
    private Store currentStore;
    private SalesReportService salesReportService;
    private SalesReportData currentReportData;
    private ObservableList<ItemizedSalesItem> itemizedSalesItems;
    private List<ItemizedSalesItem> allItemizedSalesItems; // For search functionality
    private int currentPage = 1;
    private int itemsPerPage = 20;
    private int totalPages = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            salesReportService = new SalesReportService();
            itemizedSalesItems = FXCollections.observableArrayList();
            allItemizedSalesItems = FXCollections.observableArrayList();

            setupTables();

            System.out.println("Simplified ReportsController initialized successfully");
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

            loadSalesReportData();

            System.out.println("Sales reports loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing reports with data: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat data laporan: " + e.getMessage());
        }
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        if (itemizedTable != null) {
            // Setup itemized sales table
            if (productNameCol != null) productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            if (categoryCol != null) categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            if (quantityCol != null) quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
            if (revenueCol != null) revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenueFormatted"));
            if (transactionsCol != null) transactionsCol.setCellValueFactory(new PropertyValueFactory<>("transactionCount"));
            if (avgPriceCol != null) avgPriceCol.setCellValueFactory(new PropertyValueFactory<>("avgPriceFormatted"));

            itemizedTable.setItems(itemizedSalesItems);
        }
    }

    /**
     * Load comprehensive sales report data with default date range (last 30 days)
     */
    private void loadSalesReportData() {
        try {
            // Use default date range - last 30 days
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);

            System.out.println("Loading sales report from " + startDate + " to " + endDate);

            // Generate comprehensive sales report
            currentReportData = salesReportService.generateSalesReport(currentStore.getId(), startDate, endDate);

            // Update all UI components
            updateSummaryCards();
            updateItemizedSalesTable();

            System.out.println("Sales report data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading sales report data: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat data laporan penjualan: " + e.getMessage());
        }
    }

    /**
     * Update summary cards with comprehensive data
     */
    private void updateSummaryCards() {
        if (currentReportData == null || currentReportData.getSummary() == null) {
            // Set default values if no data
            if (totalRevenueLabel != null) totalRevenueLabel.setText("Rp 0");
            if (totalTransactionsLabel != null) totalTransactionsLabel.setText("0");
            if (totalItemsLabel != null) totalItemsLabel.setText("0");
            if (revenueChangeLabel != null) revenueChangeLabel.setText("Tidak ada data");
            if (transactionChangeLabel != null) transactionChangeLabel.setText("Tidak ada data");
            return;
        }

        var summary = currentReportData.getSummary();

        // Update main summary cards - only if they exist
        if (totalRevenueLabel != null) {
            totalRevenueLabel.setText(String.format("Rp %,.0f", summary.getTotalRevenue()));
        }
        if (totalTransactionsLabel != null) {
            totalTransactionsLabel.setText(String.valueOf(summary.getTotalTransactions()));
        }
        if (totalItemsLabel != null) {
            totalItemsLabel.setText(String.valueOf(summary.getTotalItemsSold()));
        }

        // Update change labels with useful info
        if (revenueChangeLabel != null) {
            revenueChangeLabel.setText("Periode: 30 hari terakhir");
        }
        if (transactionChangeLabel != null) {
            transactionChangeLabel.setText(String.format("Rata-rata: Rp %,.0f per transaksi", summary.getAverageOrderValue()));
        }

        System.out.println("Summary cards updated with sales data");
    }

    /**
     * Update itemized sales table
     */
    private void updateItemizedSalesTable() {
        if (itemizedTable == null) return;

        itemizedSalesItems.clear();
        allItemizedSalesItems.clear();

        if (currentReportData == null) {
            updatePageInfo();
            return;
        }

        List<ItemizedSales> itemizedSales = currentReportData.getItemizedSales();

        // Convert to display items
        for (ItemizedSales item : itemizedSales) {
            ItemizedSalesItem displayItem = new ItemizedSalesItem(
                    item.getProductName(),
                    item.getCategory(),
                    item.getQuantitySold(),
                    item.getTotalRevenue(),
                    item.getTransactionCount(),
                    item.getAveragePricePerUnit()
            );
            allItemizedSalesItems.add(displayItem);
        }

        // Apply pagination
        updatePaginatedItems();

        System.out.println("Itemized sales table updated with " + allItemizedSalesItems.size() + " total items");
    }

    /**
     * Update paginated items
     */
    private void updatePaginatedItems() {
        itemizedSalesItems.clear();

        // Calculate pagination
        totalPages = (int) Math.ceil((double) allItemizedSalesItems.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        // Get items for current page
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allItemizedSalesItems.size());

        if (startIndex < allItemizedSalesItems.size()) {
            List<ItemizedSalesItem> pageItems = allItemizedSalesItems.subList(startIndex, endIndex);
            itemizedSalesItems.addAll(pageItems);
        }

        updatePageInfo();
    }

    /**
     * Update pagination info
     */
    private void updatePageInfo() {
        if (pageInfoLabel != null) {
            pageInfoLabel.setText(String.format("Halaman %d dari %d", currentPage, totalPages));
        }
        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage <= 1);
        }
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages);
        }
    }

    @FXML
    private void searchItems() {
        if (searchField == null) return;

        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            // Reset to show all items
            updateItemizedSalesTable();
            return;
        }

        // Filter items based on search term
        List<ItemizedSalesItem> filteredItems = allItemizedSalesItems.stream()
                .filter(item ->
                        item.getProductName().toLowerCase().contains(searchTerm) ||
                                item.getCategory().toLowerCase().contains(searchTerm)
                )
                .collect(Collectors.toList());

        // Update the display list
        allItemizedSalesItems.clear();
        allItemizedSalesItems.addAll(filteredItems);

        // Reset to first page and update
        currentPage = 1;
        updatePaginatedItems();

        System.out.println("Search completed. Found " + filteredItems.size() + " items matching: " + searchTerm);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePaginatedItems();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePaginatedItems();
        }
    }

    /**
     * Refresh the report data
     */
    public void refreshReport() {
        loadSalesReportData();
        AlertUtil.showInfo("Laporan Diperbarui", "Data laporan penjualan telah diperbarui.");
    }

    // Inner class for table items
    public static class ItemizedSalesItem {
        private String productName;
        private String category;
        private int quantitySold;
        private double totalRevenue;
        private int transactionCount;
        private double avgPricePerUnit;

        public ItemizedSalesItem(String productName, String category, int quantitySold,
                                 double totalRevenue, int transactionCount, double avgPricePerUnit) {
            this.productName = productName;
            this.category = category;
            this.quantitySold = quantitySold;
            this.totalRevenue = totalRevenue;
            this.transactionCount = transactionCount;
            this.avgPricePerUnit = avgPricePerUnit;
        }

        // Getters
        public String getProductName() { return productName; }
        public String getCategory() { return category; }
        public int getQuantitySold() { return quantitySold; }
        public double getTotalRevenue() { return totalRevenue; }
        public int getTransactionCount() { return transactionCount; }
        public double getAvgPricePerUnit() { return avgPricePerUnit; }

        public String getRevenueFormatted() { return String.format("Rp %,.0f", totalRevenue); }
        public String getAvgPriceFormatted() { return String.format("Rp %,.0f", avgPricePerUnit); }
    }
}
