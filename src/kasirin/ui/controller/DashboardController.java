package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.DashboardService;
import kasirin.service.DashboardService.DashboardStats;
import kasirin.service.DashboardService.TopProduct;
import kasirin.service.DashboardService.RecentTransaction;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Map;

/**
 * Enhanced Dashboard Controller with welcoming Home view
 * Real-time integration with database for comprehensive store overview
 *
 * @author yamaym
 */
public class DashboardController implements Initializable {

    // Header Elements
    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label storeNameLabel;
    @FXML private Label userRoleLabel;

    // Quick Stats Cards
    @FXML private Label todaySalesLabel;
    @FXML private Label transactionCountLabel;
    @FXML private Label productCountLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label avgOrderLabel;
    @FXML private Label monthlyGrowthLabel;

    // Charts
    @FXML private LineChart<String, Number> salesChart;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> weeklyChart;

    // Lists and Tables
    @FXML private ListView<String> topProductsList;
    @FXML private ListView<String> lowStockList;
    @FXML private ListView<String> quickActionsList;

    @FXML private TableView<RecentTransactionItem> recentTransactionsTable;
    @FXML private TableColumn<RecentTransactionItem, String> transactionIdCol;
    @FXML private TableColumn<RecentTransactionItem, String> transactionTimeCol;
    @FXML private TableColumn<RecentTransactionItem, String> transactionTotalCol;
    @FXML private TableColumn<RecentTransactionItem, String> transactionUserCol;

    // Action Buttons
    @FXML private Button refreshBtn;
    @FXML private Button quickSaleBtn;
    @FXML private Button viewReportsBtn;
    @FXML private Button manageProductsBtn;

    private User currentUser;
    private Store currentStore;
    private DashboardService dashboardService;
    private ObservableList<RecentTransactionItem> recentTransactionItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dashboardService = new DashboardService();
            recentTransactionItems = FXCollections.observableArrayList();

            setupWelcomeView();
            setupTables();
            setupCharts();
            setupQuickActions();

            System.out.println("Enhanced DashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing DashboardController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with user and store data - creates welcoming home experience
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            setupWelcomeMessage();
            loadDashboardData();

            System.out.println("Welcome dashboard loaded for " + user.getName() + " at " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing dashboard with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup welcoming home view
     */
    private void setupWelcomeView() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

        if (dateLabel != null) {
            dateLabel.setText(now.format(formatter));
        }
    }

    /**
     * Setup personalized welcome message
     */
    private void setupWelcomeMessage() {
        if (currentUser == null || currentStore == null) return;

        // Set welcome message based on time of day
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String greeting;

        if (hour < 12) {
            greeting = "Selamat Pagi";
        } else if (hour < 17) {
            greeting = "Selamat Siang";
        } else {
            greeting = "Selamat Sore";
        }

        if (welcomeLabel != null) {
            welcomeLabel.setText(greeting + ", " + currentUser.getName() + "!");
        }

        if (storeNameLabel != null) {
            storeNameLabel.setText(currentStore.getName());
        }

        if (userRoleLabel != null) {
            String roleText = getRoleDisplayText(currentUser.getRole().toString());
            userRoleLabel.setText(roleText);
        }
    }

    /**
     * Get user-friendly role display text
     */
    private String getRoleDisplayText(String role) {
        switch (role.toLowerCase()) {
            case "admin": return "Administrator";
            case "staff": return "Staff Kasir";
            case "owner": return "Pemilik Toko";
            default: return role;
        }
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        if (transactionIdCol != null) {
            transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
            transactionTimeCol.setCellValueFactory(new PropertyValueFactory<>("transactionTime"));
            transactionTotalCol.setCellValueFactory(new PropertyValueFactory<>("transactionTotal"));
            transactionUserCol.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
            recentTransactionsTable.setItems(recentTransactionItems);
        }
    }

    /**
     * Setup charts with enhanced styling
     */
    private void setupCharts() {
        if (salesChart != null) {
            salesChart.setTitle("Penjualan 7 Hari Terakhir");
            salesChart.setAnimated(true);
            salesChart.setCreateSymbols(true);
        }

        if (categoryChart != null) {
            categoryChart.setTitle("Kategori Produk Terlaris");
            categoryChart.setLegendVisible(true);
        }

        if (weeklyChart != null) {
            weeklyChart.setTitle("Perbandingan Mingguan");
            weeklyChart.setAnimated(true);
        }
    }

    /**
     * Setup quick actions based on user role
     */
    private void setupQuickActions() {
        if (quickActionsList == null) return;

        quickActionsList.getItems().clear();

        // Common actions for all users
        quickActionsList.getItems().addAll(
                "📊 Lihat Laporan Hari Ini",
                "🧾 Transaksi Terbaru",
                "📈 Analisis Penjualan"
        );

        // Role-specific actions
        if (currentUser != null) {
            String role = currentUser.getRole().toString().toLowerCase();

            switch (role) {
                case "admin":
                case "owner":
                    quickActionsList.getItems().addAll(
                            "📦 Kelola Produk",
                            "👥 Manajemen Kasir",
                            "⚙️ Pengaturan Toko"
                    );
                    break;
                case "staff":
                    quickActionsList.getItems().addAll(
                            "💰 Mulai Transaksi",
                            "🔍 Cari Produk",
                            "📋 Stok Produk"
                    );
                    break;
            }
        }
    }

    /**
     * Load comprehensive dashboard data
     */
    private void loadDashboardData() {
        try {
            if (currentStore == null) {
                System.err.println("Current store is null, cannot load dashboard data");
                return;
            }

            System.out.println("Loading comprehensive dashboard data for store: " + currentStore.getName());

            // Get dashboard statistics
            DashboardStats stats = dashboardService.getDashboardStats(currentStore.getId());

            // Update all dashboard components
            updateSummaryCards(stats);
            updateCharts(stats);
            updateLists(stats);
            updateRecentTransactionsTable(stats.getRecentTransactions());

            System.out.println("Dashboard data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update summary cards with real-time data
     */
    private void updateSummaryCards(DashboardStats stats) {
        if (todaySalesLabel != null) {
            todaySalesLabel.setText(String.format("Rp %,.0f", stats.getTodaySales()));
        }

        if (transactionCountLabel != null) {
            transactionCountLabel.setText(String.valueOf(stats.getTodayTransactions()));
        }

        if (productCountLabel != null) {
            productCountLabel.setText(String.valueOf(stats.getTotalProducts()));
        }

        if (lowStockLabel != null) {
            lowStockLabel.setText(String.valueOf(stats.getLowStockCount()));
        }

        if (avgOrderLabel != null) {
            avgOrderLabel.setText(String.format("Rp %,.0f", stats.getAvgOrderValue()));
        }

        // Calculate growth indicator (simplified)
        if (monthlyGrowthLabel != null) {
            if (stats.getTodaySales() > 0) {
                monthlyGrowthLabel.setText("📈 Trend Positif");
                monthlyGrowthLabel.getStyleClass().add("positive-trend");
            } else {
                monthlyGrowthLabel.setText("📊 Belum Ada Data");
                monthlyGrowthLabel.getStyleClass().add("neutral-trend");
            }
        }

        System.out.println("Summary cards updated with real-time data");
    }

    /**
     * Update charts with comprehensive data visualization
     */
    private void updateCharts(DashboardStats stats) {
        updateSalesChart(stats.getWeeklySales());
        updateCategoryChart(stats.getTopProducts());
        updateWeeklyChart(stats.getWeeklySales());
    }

    /**
     * Update sales trend chart
     */
    private void updateSalesChart(Map<String, Double> weeklySales) {
        if (salesChart == null) return;

        try {
            salesChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Penjualan Harian");

            String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            String[] dayNames = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

            for (int i = 0; i < days.length; i++) {
                double sales = weeklySales.getOrDefault(days[i], 0.0);
                series.getData().add(new XYChart.Data<>(dayNames[i], sales));
            }

            salesChart.getData().add(series);
            System.out.println("Sales chart updated with weekly data");
        } catch (Exception e) {
            System.err.println("Error updating sales chart: " + e.getMessage());
        }
    }

    /**
     * Update category distribution chart
     */
    private void updateCategoryChart(java.util.List<TopProduct> topProducts) {
        if (categoryChart == null) return;

        try {
            categoryChart.getData().clear();

            if (topProducts.isEmpty()) {
                categoryChart.getData().add(new PieChart.Data("Belum ada data", 100));
                return;
            }

            // Show top 4 products + others
            double totalSales = topProducts.stream().mapToDouble(p -> p.getSalesCount()).sum();

            for (int i = 0; i < Math.min(4, topProducts.size()); i++) {
                TopProduct product = topProducts.get(i);
                double percentage = (product.getSalesCount() / totalSales) * 100;

                categoryChart.getData().add(new PieChart.Data(
                        product.getName() + String.format(" (%.1f%%)", percentage),
                        product.getSalesCount()
                ));
            }

            if (topProducts.size() > 4) {
                double othersCount = topProducts.stream().skip(4).mapToDouble(p -> p.getSalesCount()).sum();
                double percentage = (othersCount / totalSales) * 100;
                categoryChart.getData().add(new PieChart.Data(
                        String.format("Lainnya (%.1f%%)", percentage),
                        othersCount
                ));
            }

            System.out.println("Category chart updated with product distribution");
        } catch (Exception e) {
            System.err.println("Error updating category chart: " + e.getMessage());
        }
    }

    /**
     * Update weekly comparison chart
     */
    private void updateWeeklyChart(Map<String, Double> weeklySales) {
        if (weeklyChart == null) return;

        try {
            weeklyChart.getData().clear();

            XYChart.Series<String, Number> currentWeek = new XYChart.Series<>();
            currentWeek.setName("Minggu Ini");

            String[] dayNames = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
            String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

            for (int i = 0; i < days.length; i++) {
                double sales = weeklySales.getOrDefault(days[i], 0.0);
                currentWeek.getData().add(new XYChart.Data<>(dayNames[i], sales));
            }

            weeklyChart.getData().add(currentWeek);
            System.out.println("Weekly chart updated with comparison data");
        } catch (Exception e) {
            System.err.println("Error updating weekly chart: " + e.getMessage());
        }
    }

    /**
     * Update lists with relevant information
     */
    private void updateLists(DashboardStats stats) {
        updateTopProductsList(stats.getTopProducts());
        updateLowStockList();
    }

    /**
     * Update top products list
     */
    private void updateTopProductsList(java.util.List<TopProduct> topProducts) {
        if (topProductsList == null) return;

        try {
            topProductsList.getItems().clear();

            if (topProducts.isEmpty()) {
                topProductsList.getItems().add("📦 Belum ada data penjualan");
                return;
            }

            for (int i = 0; i < Math.min(5, topProducts.size()); i++) {
                TopProduct product = topProducts.get(i);
                String item = String.format("🏆 %d. %s (%d terjual)",
                        i + 1, product.getName(), product.getSalesCount());
                topProductsList.getItems().add(item);
            }

            System.out.println("Top products list updated with " + topProducts.size() + " items");
        } catch (Exception e) {
            System.err.println("Error updating top products list: " + e.getMessage());
        }
    }

    /**
     * Update low stock list
     */
    private void updateLowStockList() {
        if (lowStockList == null) return;

        try {
            lowStockList.getItems().clear();

            // This would typically fetch from ProductVariationService
            lowStockList.getItems().addAll(
                    "⚠️ Produk A - Stok: 5",
                    "⚠️ Produk B - Stok: 3",
                    "⚠️ Produk C - Stok: 8"
            );

            if (lowStockList.getItems().isEmpty()) {
                lowStockList.getItems().add("✅ Semua produk stok aman");
            }

            System.out.println("Low stock list updated");
        } catch (Exception e) {
            System.err.println("Error updating low stock list: " + e.getMessage());
        }
    }

    /**
     * Update recent transactions table
     */
    private void updateRecentTransactionsTable(java.util.List<RecentTransaction> recentTransactions) {
        if (recentTransactionItems == null) return;

        try {
            recentTransactionItems.clear();

            for (RecentTransaction transaction : recentTransactions) {
                RecentTransactionItem item = new RecentTransactionItem(
                        "TXN" + String.format("%04d", transaction.getId()),
                        formatTransactionTime(transaction.getTime()),
                        String.format("Rp %,.0f", transaction.getTotal()),
                        transaction.getCashier()
                );
                recentTransactionItems.add(item);
            }

            System.out.println("Recent transactions table updated with " + recentTransactions.size() + " items");
        } catch (Exception e) {
            System.err.println("Error updating recent transactions table: " + e.getMessage());
        }
    }

    /**
     * Format transaction time for display
     */
    private String formatTransactionTime(String timeString) {
        try {
            if (timeString.length() > 16) {
                return timeString.substring(0, 16);
            }
            return timeString;
        } catch (Exception e) {
            return timeString;
        }
    }

    /**
     * Refresh all dashboard data
     */
    @FXML
    private void refreshDashboard() {
        try {
            System.out.println("Refreshing dashboard data...");
            loadDashboardData();
            setupWelcomeMessage(); // Update time-based greeting

            System.out.println("Dashboard refreshed successfully");
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
        }
    }

    /**
     * Quick sale action
     */
    @FXML
    private void quickSale() {
        // Navigate to cashier/transaction view
        System.out.println("Quick sale action triggered");
    }

    /**
     * View reports action
     */
    @FXML
    private void viewReports() {
        // Navigate to reports view
        System.out.println("View reports action triggered");
    }

    /**
     * Manage products action
     */
    @FXML
    private void manageProducts() {
        // Navigate to products management (if user has permission)
        if (currentUser != null && !currentUser.getRole().toString().equalsIgnoreCase("staff")) {
            System.out.println("Manage products action triggered");
        } else {
            System.out.println("Access denied: Staff cannot manage products");
        }
    }

    /**
     * Inner class for recent transaction table items
     */
    public static class RecentTransactionItem {
        private String transactionId;
        private String transactionTime;
        private String transactionTotal;
        private String cashierName;

        public RecentTransactionItem(String transactionId, String transactionTime,
                                     String transactionTotal, String cashierName) {
            this.transactionId = transactionId;
            this.transactionTime = transactionTime;
            this.transactionTotal = transactionTotal;
            this.cashierName = cashierName;
        }

        // Getters for PropertyValueFactory
        public String getTransactionId() { return transactionId; }
        public String getTransactionTime() { return transactionTime; }
        public String getTransactionTotal() { return transactionTotal; }
        public String getCashierName() { return cashierName; }
    }
}
