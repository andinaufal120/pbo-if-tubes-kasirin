package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.TransactionDAO;
import kasirin.data.dao.ProductDAO;
import kasirin.data.dao.ProductVariationDAO;
import kasirin.data.dao.MySqlTransactionDAO;
import kasirin.data.model.Transaction;
import kasirin.data.model.Product;
import kasirin.data.model.ProductVariation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Service untuk Dashboard dengan integrasi database
 *
 * @author yamaym
 */
public class DashboardService {
    private final DAOFactory daoFactory;
    private final TransactionDAO transactionDAO;
    private final ProductDAO productDAO;
    private final ProductVariationDAO productVariationDAO;

    public DashboardService() {
        this.daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.transactionDAO = daoFactory.getTransactionDAO();
        this.productDAO = daoFactory.getProductDAO();
        this.productVariationDAO = daoFactory.getProductVariationDAO();
    }

    /**
     * Get dashboard statistics for a specific store
     */
    public DashboardStats getDashboardStats(int storeId) {
        try {
            LocalDate today = LocalDate.now();
            Date sqlToday = Date.valueOf(today);

            // Get today's sales
            double todaySales = getTodaySales(storeId, sqlToday);

            // Get today's transaction count
            int todayTransactions = getTodayTransactionCount(storeId, sqlToday);

            // Get total products count
            int totalProducts = getTotalProductsCount(storeId);

            // Get low stock products count
            int lowStockCount = getLowStockProductsCount(storeId);

            // Get average order value
            double avgOrderValue = todayTransactions > 0 ? todaySales / todayTransactions : 0;

            // Get weekly sales data for chart
            Map<String, Double> weeklySales = getWeeklySalesData(storeId);

            // Get top selling products
            List<TopProduct> topProducts = getTopSellingProducts(storeId, 5);

            // Get recent transactions
            List<RecentTransaction> recentTransactions = getRecentTransactions(storeId, 10);

            return new DashboardStats(
                    todaySales,
                    todayTransactions,
                    totalProducts,
                    lowStockCount,
                    avgOrderValue,
                    weeklySales,
                    topProducts,
                    recentTransactions
            );

        } catch (Exception e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
            return new DashboardStats(); // Return empty stats
        }
    }

    /**
     * Get today's sales for a store
     */
    private double getTodaySales(int storeId, Date today) {
        try {
            if (transactionDAO instanceof MySqlTransactionDAO) {
                MySqlTransactionDAO mysqlDAO = (MySqlTransactionDAO) transactionDAO;
                return mysqlDAO.getDailyRevenue(storeId, today);
            } else {
                // Fallback method
                List<Transaction> allTransactions = transactionDAO.findAllTransactions();
                return allTransactions.stream()
                        .filter(t -> t.getStoreID() == storeId)
                        .filter(t -> isSameDay(t.getTimestamp(), today))
                        .mapToDouble(Transaction::getTotal)
                        .sum();
            }
        } catch (Exception e) {
            System.err.println("Error getting today's sales: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Get today's transaction count
     */
    private int getTodayTransactionCount(int storeId, Date today) {
        try {
            List<Transaction> allTransactions = transactionDAO.findAllTransactions();
            return (int) allTransactions.stream()
                    .filter(t -> t.getStoreID() == storeId)
                    .filter(t -> isSameDay(t.getTimestamp(), today))
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting today's transaction count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get total products count for a store
     */
    private int getTotalProductsCount(int storeId) {
        try {
            List<Product> allProducts = productDAO.findAllProducts();
            return (int) allProducts.stream()
                    .filter(p -> p.getStoreID() == storeId)
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting total products count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get low stock products count (stock < 10)
     */
    private int getLowStockProductsCount(int storeId) {
        try {
            List<Product> storeProducts = productDAO.findAllProducts().stream()
                    .filter(p -> p.getStoreID() == storeId)
                    .collect(Collectors.toList());

            int lowStockCount = 0;
            for (Product product : storeProducts) {
                List<ProductVariation> variations = productVariationDAO.findVariationsByProductId(product.getId());
                for (ProductVariation variation : variations) {
                    if (variation.getStocks() < 10) {
                        lowStockCount++;
                        break; // Count product only once even if multiple variations are low
                    }
                }
            }
            return lowStockCount;
        } catch (Exception e) {
            System.err.println("Error getting low stock count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get weekly sales data for chart
     */
    private Map<String, Double> getWeeklySalesData(int storeId) {
        Map<String, Double> weeklySales = new HashMap<>();
        try {
            LocalDate today = LocalDate.now();

            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                Date sqlDate = Date.valueOf(date);
                double dailySales = getTodaySales(storeId, sqlDate);

                String dayName = date.getDayOfWeek().toString().substring(0, 3);
                weeklySales.put(dayName, dailySales);
            }
        } catch (Exception e) {
            System.err.println("Error getting weekly sales data: " + e.getMessage());
        }
        return weeklySales;
    }

    /**
     * Get top selling products (simplified - based on recent transactions)
     */
    private List<TopProduct> getTopSellingProducts(int storeId, int limit) {
        List<TopProduct> topProducts = new ArrayList<>();
        try {
            // Get recent transactions for the store
            List<Transaction> storeTransactions = transactionDAO.findAllTransactions().stream()
                    .filter(t -> t.getStoreID() == storeId)
                    .collect(Collectors.toList());

            // For now, return sample data - would need TransactionDetails to get actual product sales
            List<Product> storeProducts = productDAO.findAllProducts().stream()
                    .filter(p -> p.getStoreID() == storeId)
                    .limit(limit)
                    .collect(Collectors.toList());

            for (int i = 0; i < storeProducts.size(); i++) {
                Product product = storeProducts.get(i);
                topProducts.add(new TopProduct(
                        product.getName(),
                        (limit - i) * 10 // Sample sales count
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting top selling products: " + e.getMessage());
        }
        return topProducts;
    }

    /**
     * Get recent transactions
     */
    private List<RecentTransaction> getRecentTransactions(int storeId, int limit) {
        List<RecentTransaction> recentTransactions = new ArrayList<>();
        try {
            List<Transaction> storeTransactions = transactionDAO.findAllTransactions().stream()
                    .filter(t -> t.getStoreID() == storeId)
                    .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                    .limit(limit)
                    .collect(Collectors.toList());

            for (Transaction transaction : storeTransactions) {
                recentTransactions.add(new RecentTransaction(
                        transaction.getId(),
                        transaction.getTimestamp().toString(),
                        transaction.getTotal(),
                        "User " + transaction.getUserID() // Would need to join with Users table for actual name
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting recent transactions: " + e.getMessage());
        }
        return recentTransactions;
    }

    /**
     * Check if timestamp is on the same day as date
     */
    private boolean isSameDay(java.sql.Timestamp timestamp, Date date) {
        Date transactionDate = new Date(timestamp.getTime());
        return transactionDate.equals(date);
    }

    // Inner classes for data transfer
    public static class DashboardStats {
        private double todaySales;
        private int todayTransactions;
        private int totalProducts;
        private int lowStockCount;
        private double avgOrderValue;
        private Map<String, Double> weeklySales;
        private List<TopProduct> topProducts;
        private List<RecentTransaction> recentTransactions;

        public DashboardStats() {
            this.weeklySales = new HashMap<>();
            this.topProducts = new ArrayList<>();
            this.recentTransactions = new ArrayList<>();
        }

        public DashboardStats(double todaySales, int todayTransactions, int totalProducts,
                              int lowStockCount, double avgOrderValue, Map<String, Double> weeklySales,
                              List<TopProduct> topProducts, List<RecentTransaction> recentTransactions) {
            this.todaySales = todaySales;
            this.todayTransactions = todayTransactions;
            this.totalProducts = totalProducts;
            this.lowStockCount = lowStockCount;
            this.avgOrderValue = avgOrderValue;
            this.weeklySales = weeklySales;
            this.topProducts = topProducts;
            this.recentTransactions = recentTransactions;
        }

        // Getters
        public double getTodaySales() { return todaySales; }
        public int getTodayTransactions() { return todayTransactions; }
        public int getTotalProducts() { return totalProducts; }
        public int getLowStockCount() { return lowStockCount; }
        public double getAvgOrderValue() { return avgOrderValue; }
        public Map<String, Double> getWeeklySales() { return weeklySales; }
        public List<TopProduct> getTopProducts() { return topProducts; }
        public List<RecentTransaction> getRecentTransactions() { return recentTransactions; }
    }

    public static class TopProduct {
        private String name;
        private int salesCount;

        public TopProduct(String name, int salesCount) {
            this.name = name;
            this.salesCount = salesCount;
        }

        public String getName() { return name; }
        public int getSalesCount() { return salesCount; }
    }

    public static class RecentTransaction {
        private int id;
        private String time;
        private double total;
        private String cashier;

        public RecentTransaction(int id, String time, double total, String cashier) {
            this.id = id;
            this.time = time;
            this.total = total;
            this.cashier = cashier;
        }

        public int getId() { return id; }
        public String getTime() { return time; }
        public double getTotal() { return total; }
        public String getCashier() { return cashier; }
    }
}
