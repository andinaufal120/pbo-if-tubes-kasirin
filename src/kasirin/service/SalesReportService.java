package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.TransactionDAO;
import kasirin.data.dao.TransactionDetailDAO;
import kasirin.data.dao.UserDAO;
import kasirin.data.dao.ProductDAO;
import kasirin.data.model.Transaction;
import kasirin.data.model.TransactionDetail;
import kasirin.data.model.User;
import kasirin.data.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating comprehensive sales reports
 * Integrates data from transactions and transaction_details tables
 *
 * @author yamaym
 */
public class SalesReportService {

    private final DAOFactory daoFactory;
    private final TransactionDAO transactionDAO;
    private final TransactionDetailDAO transactionDetailDAO;
    private final UserDAO userDAO;
    private final ProductDAO productDAO;

    public SalesReportService() {
        this.daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.transactionDAO = daoFactory.getTransactionDAO();
        this.transactionDetailDAO = daoFactory.getTransactionDetailDAO();
        this.userDAO = daoFactory.getUserDAO();
        this.productDAO = daoFactory.getProductDAO();
    }

    /**
     * Generate comprehensive sales report for a store within date range
     */
    public SalesReportData generateSalesReport(int storeId, LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("Generating sales report for store " + storeId + " from " + startDate + " to " + endDate);

            // Get all transactions in date range
            List<Transaction> transactions = getTransactionsInDateRange(storeId, startDate, endDate);

            if (transactions.isEmpty()) {
                System.out.println("No transactions found in the specified date range");
                return createEmptyReport(startDate, endDate);
            }

            // Generate comprehensive report
            SalesReportData reportData = new SalesReportData(startDate, endDate);

            // Calculate summary metrics
            SalesSummary summary = calculateSalesSummary(transactions);
            reportData.setSummary(summary);

            // Generate itemized sales data
            List<ItemizedSales> itemizedSales = generateItemizedSales(transactions);
            reportData.setItemizedSales(itemizedSales);

            // Generate top products
            List<TopSellingProduct> topProducts = generateTopProducts(itemizedSales);
            reportData.setTopProducts(topProducts);

            // Generate daily sales data
            Map<LocalDate, DailySales> dailySales = generateDailySales(transactions);
            reportData.setDailySales(dailySales);

            // Generate cashier performance
            List<CashierPerformance> cashierPerformance = generateCashierPerformance(transactions);
            reportData.setCashierPerformance(cashierPerformance);

            // Generate hourly sales pattern
            Map<Integer, Double> hourlySales = generateHourlySales(transactions);
            reportData.setHourlySales(hourlySales);

            System.out.println("Sales report generated successfully with " + transactions.size() + " transactions");
            return reportData;

        } catch (Exception e) {
            System.err.println("Error generating sales report: " + e.getMessage());
            e.printStackTrace();
            return createEmptyReport(startDate, endDate);
        }
    }

    /**
     * Get transactions within date range for a specific store
     */
    private List<Transaction> getTransactionsInDateRange(int storeId, LocalDate startDate, LocalDate endDate) {
        try {
            List<Transaction> allTransactions = transactionDAO.findAllTransactions();
            return allTransactions.stream()
                    .filter(t -> t.getStoreID() == storeId)
                    .filter(t -> {
                        LocalDate transactionDate = t.getTimestamp().toLocalDateTime().toLocalDate();
                        return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                    })
                    .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting transactions in date range: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculate sales summary metrics
     */
    private SalesSummary calculateSalesSummary(List<Transaction> transactions) {
        SalesSummary summary = new SalesSummary();

        // Basic metrics
        summary.setTotalTransactions(transactions.size());
        summary.setTotalRevenue(transactions.stream().mapToDouble(Transaction::getTotal).sum());
        summary.setAverageOrderValue(summary.getTotalRevenue() / Math.max(1, summary.getTotalTransactions()));

        // Get transaction details for item metrics
        int totalItems = 0;
        Set<Integer> uniqueProducts = new HashSet<>();

        try {
            List<TransactionDetail> allDetails = transactionDetailDAO.findAllTransactionDetails();
            Set<Integer> transactionIds = transactions.stream()
                    .map(Transaction::getId)
                    .collect(Collectors.toSet());

            for (TransactionDetail detail : allDetails) {
                if (transactionIds.contains(detail.getTransactionId())) {
                    totalItems += detail.getQuantity();
                    uniqueProducts.add(detail.getProductID());
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating summary: " + e.getMessage());
        }

        summary.setTotalItemsSold(totalItems);
        summary.setUniqueProductsSold(uniqueProducts.size());
        summary.setAverageItemsPerTransaction((double) totalItems / Math.max(1, summary.getTotalTransactions()));

        return summary;
    }

    /**
     * Generate itemized sales data
     */
    private List<ItemizedSales> generateItemizedSales(List<Transaction> transactions) {
        Map<Integer, ItemizedSalesBuilder> salesMap = new HashMap<>();

        try {
            List<TransactionDetail> allDetails = transactionDetailDAO.findAllTransactionDetails();
            Set<Integer> transactionIds = transactions.stream()
                    .map(Transaction::getId)
                    .collect(Collectors.toSet());

            for (TransactionDetail detail : allDetails) {
                if (transactionIds.contains(detail.getTransactionId())) {
                    ItemizedSalesBuilder builder = salesMap.computeIfAbsent(detail.getProductID(),
                            k -> new ItemizedSalesBuilder(detail.getProductID()));

                    builder.addSale(detail.getQuantity(), detail.getPricePerUnit(), detail.getTransactionId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating itemized sales: " + e.getMessage());
        }

        // Convert to ItemizedSales objects
        List<ItemizedSales> itemizedSales = new ArrayList<>();
        for (ItemizedSalesBuilder builder : salesMap.values()) {
            itemizedSales.add(builder.build());
        }

        // Sort by total revenue descending
        itemizedSales.sort((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()));

        return itemizedSales;
    }

    /**
     * Generate top selling products
     */
    private List<TopSellingProduct> generateTopProducts(List<ItemizedSales> itemizedSales) {
        return itemizedSales.stream()
                .map(item -> new TopSellingProduct(
                        item.getProductName(),
                        item.getQuantitySold(),
                        item.getTotalRevenue()
                ))
                .sorted((a, b) -> Integer.compare(b.getQuantitySold(), a.getQuantitySold()))
                .collect(Collectors.toList());
    }

    /**
     * Generate daily sales data
     */
    private Map<LocalDate, DailySales> generateDailySales(List<Transaction> transactions) {
        Map<LocalDate, DailySales> dailySales = new HashMap<>();

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getTimestamp().toLocalDateTime().toLocalDate();

            DailySales dayData = dailySales.computeIfAbsent(date, k -> new DailySales(date));
            dayData.addTransaction(transaction.getTotal());
        }

        return dailySales;
    }

    /**
     * Generate cashier performance data
     */
    private List<CashierPerformance> generateCashierPerformance(List<Transaction> transactions) {
        Map<Integer, CashierPerformanceBuilder> performanceMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            CashierPerformanceBuilder builder = performanceMap.computeIfAbsent(transaction.getUserID(),
                    k -> new CashierPerformanceBuilder(transaction.getUserID()));

            builder.addTransaction(transaction.getTotal());
        }

        List<CashierPerformance> performance = new ArrayList<>();
        for (CashierPerformanceBuilder builder : performanceMap.values()) {
            performance.add(builder.build());
        }

        // Sort by total revenue descending
        performance.sort((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()));

        return performance;
    }

    /**
     * Generate hourly sales pattern
     */
    private Map<Integer, Double> generateHourlySales(List<Transaction> transactions) {
        Map<Integer, Double> hourlySales = new HashMap<>();

        for (Transaction transaction : transactions) {
            int hour = transaction.getTimestamp().toLocalDateTime().getHour();
            hourlySales.merge(hour, transaction.getTotal(), Double::sum);
        }

        return hourlySales;
    }

    /**
     * Create empty report for cases with no data
     */
    private SalesReportData createEmptyReport(LocalDate startDate, LocalDate endDate) {
        SalesReportData reportData = new SalesReportData(startDate, endDate);
        reportData.setSummary(new SalesSummary());
        reportData.setItemizedSales(new ArrayList<>());
        reportData.setTopProducts(new ArrayList<>());
        reportData.setDailySales(new HashMap<>());
        reportData.setCashierPerformance(new ArrayList<>());
        reportData.setHourlySales(new HashMap<>());
        return reportData;
    }

    // Helper classes for building aggregated data
    private class ItemizedSalesBuilder {
        private int productId;
        private String productName;
        private String category;
        private int quantitySold = 0;
        private double totalRevenue = 0.0;
        private Set<Integer> transactionIds = new HashSet<>();
        private double totalPrice = 0.0;
        private int priceCount = 0;

        public ItemizedSalesBuilder(int productId) {
            this.productId = productId;
            // Get product info
            try {
                Product product = productDAO.findProduct(productId);
                if (product != null) {
                    this.productName = product.getName();
                    this.category = product.getCategory();
                } else {
                    this.productName = "Unknown Product";
                    this.category = "Unknown";
                }
            } catch (Exception e) {
                this.productName = "Unknown Product";
                this.category = "Unknown";
            }
        }

        public void addSale(int quantity, double price, int transactionId) {
            this.quantitySold += quantity;
            this.totalRevenue += (quantity * price);
            this.transactionIds.add(transactionId);
            this.totalPrice += price;
            this.priceCount++;
        }

        public ItemizedSales build() {
            double avgPrice = priceCount > 0 ? totalPrice / priceCount : 0.0;
            return new ItemizedSales(productName, category, quantitySold, totalRevenue,
                    transactionIds.size(), avgPrice);
        }
    }

    private class CashierPerformanceBuilder {
        private int cashierId;
        private String cashierName;
        private int transactionCount = 0;
        private double totalRevenue = 0.0;

        public CashierPerformanceBuilder(int cashierId) {
            this.cashierId = cashierId;
            // Get cashier info
            try {
                User cashier = userDAO.findUser(cashierId);
                this.cashierName = cashier != null ? cashier.getName() : "Unknown Cashier";
            } catch (Exception e) {
                this.cashierName = "Unknown Cashier";
            }
        }

        public void addTransaction(double amount) {
            this.transactionCount++;
            this.totalRevenue += amount;
        }

        public CashierPerformance build() {
            double avgPerTransaction = transactionCount > 0 ? totalRevenue / transactionCount : 0.0;
            return new CashierPerformance(cashierName, transactionCount, totalRevenue, avgPerTransaction);
        }
    }

    // Data classes
    public static class SalesReportData {
        private LocalDate startDate;
        private LocalDate endDate;
        private SalesSummary summary;
        private List<ItemizedSales> itemizedSales;
        private List<TopSellingProduct> topProducts;
        private Map<LocalDate, DailySales> dailySales;
        private List<CashierPerformance> cashierPerformance;
        private Map<Integer, Double> hourlySales;

        public SalesReportData(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Getters and setters
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public SalesSummary getSummary() { return summary; }
        public void setSummary(SalesSummary summary) { this.summary = summary; }
        public List<ItemizedSales> getItemizedSales() { return itemizedSales; }
        public void setItemizedSales(List<ItemizedSales> itemizedSales) { this.itemizedSales = itemizedSales; }
        public List<TopSellingProduct> getTopProducts() { return topProducts; }
        public void setTopProducts(List<TopSellingProduct> topProducts) { this.topProducts = topProducts; }
        public Map<LocalDate, DailySales> getDailySales() { return dailySales; }
        public void setDailySales(Map<LocalDate, DailySales> dailySales) { this.dailySales = dailySales; }
        public List<CashierPerformance> getCashierPerformance() { return cashierPerformance; }
        public void setCashierPerformance(List<CashierPerformance> cashierPerformance) { this.cashierPerformance = cashierPerformance; }
        public Map<Integer, Double> getHourlySales() { return hourlySales; }
        public void setHourlySales(Map<Integer, Double> hourlySales) { this.hourlySales = hourlySales; }
    }

    public static class SalesSummary {
        private int totalTransactions;
        private double totalRevenue;
        private double averageOrderValue;
        private int totalItemsSold;
        private int uniqueProductsSold;
        private double averageItemsPerTransaction;

        // Getters and setters
        public int getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        public int getTotalItemsSold() { return totalItemsSold; }
        public void setTotalItemsSold(int totalItemsSold) { this.totalItemsSold = totalItemsSold; }
        public int getUniqueProductsSold() { return uniqueProductsSold; }
        public void setUniqueProductsSold(int uniqueProductsSold) { this.uniqueProductsSold = uniqueProductsSold; }
        public double getAverageItemsPerTransaction() { return averageItemsPerTransaction; }
        public void setAverageItemsPerTransaction(double averageItemsPerTransaction) { this.averageItemsPerTransaction = averageItemsPerTransaction; }
    }

    public static class ItemizedSales {
        private String productName;
        private String category;
        private int quantitySold;
        private double totalRevenue;
        private int transactionCount;
        private double averagePricePerUnit;

        public ItemizedSales(String productName, String category, int quantitySold,
                             double totalRevenue, int transactionCount, double averagePricePerUnit) {
            this.productName = productName;
            this.category = category;
            this.quantitySold = quantitySold;
            this.totalRevenue = totalRevenue;
            this.transactionCount = transactionCount;
            this.averagePricePerUnit = averagePricePerUnit;
        }

        // Getters
        public String getProductName() { return productName; }
        public String getCategory() { return category; }
        public int getQuantitySold() { return quantitySold; }
        public double getTotalRevenue() { return totalRevenue; }
        public int getTransactionCount() { return transactionCount; }
        public double getAveragePricePerUnit() { return averagePricePerUnit; }
    }

    public static class TopSellingProduct {
        private String productName;
        private int quantitySold;
        private double totalRevenue;

        public TopSellingProduct(String productName, int quantitySold, double totalRevenue) {
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.totalRevenue = totalRevenue;
        }

        // Getters
        public String getProductName() { return productName; }
        public int getQuantitySold() { return quantitySold; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    public static class DailySales {
        private LocalDate date;
        private int transactionCount = 0;
        private double totalRevenue = 0.0;

        public DailySales(LocalDate date) {
            this.date = date;
        }

        public void addTransaction(double amount) {
            this.transactionCount++;
            this.totalRevenue += amount;
        }

        // Getters
        public LocalDate getDate() { return date; }
        public int getTransactionCount() { return transactionCount; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    public static class CashierPerformance {
        private String cashierName;
        private int transactionCount;
        private double totalRevenue;
        private double averagePerTransaction;

        public CashierPerformance(String cashierName, int transactionCount,
                                  double totalRevenue, double averagePerTransaction) {
            this.cashierName = cashierName;
            this.transactionCount = transactionCount;
            this.totalRevenue = totalRevenue;
            this.averagePerTransaction = averagePerTransaction;
        }

        // Getters
        public String getCashierName() { return cashierName; }
        public int getTransactionCount() { return transactionCount; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAveragePerTransaction() { return averagePerTransaction; }
    }
}
