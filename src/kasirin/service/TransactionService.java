package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.TransactionDAO;
import kasirin.data.dao.TransactionDetailDAO;
import kasirin.data.dao.ProductVariationDAO;
import kasirin.data.dao.ProductDAO;
import kasirin.data.dao.MySqlDAOFactory;
import kasirin.data.model.Transaction;
import kasirin.data.model.TransactionDetail;
import kasirin.data.model.ProductVariation;
import kasirin.data.model.Product;
import kasirin.data.model.User;
import kasirin.data.model.Store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Enhanced TransactionService with improved error handling and transaction detail management
 *
 * @author yamaym
 */
public class TransactionService {
    private final DAOFactory daoFactory;
    private final TransactionDAO transactionDAO;
    private final TransactionDetailDAO transactionDetailDAO;
    private final ProductVariationDAO productVariationDAO;
    private final ProductDAO productDAO;

    public TransactionService() {
        this.daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.transactionDAO = daoFactory.getTransactionDAO();
        this.transactionDetailDAO = daoFactory.getTransactionDetailDAO();
        this.productVariationDAO = daoFactory.getProductVariationDAO();
        this.productDAO = daoFactory.getProductDAO();
    }

    /**
     * Process a complete transaction with enhanced error handling and logging
     */
    public TransactionResult processCompleteTransaction(List<TransactionItem> items, User user, Store store, double paymentAmount)
            throws TransactionException {

        System.out.println("=== Starting Transaction Processing ===");
        System.out.println("Store: " + store.getName() + " (ID: " + store.getId() + ")");
        System.out.println("User: " + user.getName() + " (ID: " + user.getId() + ")");
        System.out.println("Items: " + items.size());
        System.out.println("Payment: Rp " + String.format("%,.2f", paymentAmount));

        // Input validation
        validateTransactionInput(items, user, store, paymentAmount);

        Connection conn = null;
        try {
            // Get database connection and start transaction
            conn = MySqlDAOFactory.getConnection();
            if (conn == null) {
                throw new TransactionException("Failed to establish database connection");
            }

            System.out.println("Database connection established");
            conn.setAutoCommit(false);
            System.out.println("Auto-commit disabled, transaction started");

            // Step 1: Validate and calculate transaction details
            TransactionCalculation calculation = validateAndCalculateTransaction(items, paymentAmount);
            System.out.println("Transaction calculation completed. Total: Rp " + String.format("%,.2f", calculation.getTotalAmount()));

            // Step 2: Validate stock availability for all items
            validateStockAvailability(items);
            System.out.println("Stock validation completed");

            // Step 3: Create main transaction record
            Transaction transaction = createTransactionRecord(store, user, calculation.getTotalAmount());
            int transactionId = insertTransactionRecord(transaction);
            System.out.println("Transaction record created with ID: " + transactionId);

            if (transactionId <= 0) {
                throw new TransactionException("Failed to create transaction record - invalid ID returned: " + transactionId);
            }

            // Step 4: Process each transaction item with detailed logging
            List<TransactionDetail> transactionDetails = processTransactionItems(transactionId, items);
            System.out.println("Transaction details created: " + transactionDetails.size() + " items");

            // Step 5: Update product variation stocks
            updateProductStocks(items);
            System.out.println("Product stocks updated");

            // Step 6: Commit transaction
            conn.commit();
            System.out.println("Transaction committed successfully");

            // Step 7: Create and return result
            TransactionResult result = new TransactionResult(
                    transactionId,
                    calculation.getTotalAmount(),
                    paymentAmount,
                    calculation.getChangeAmount(),
                    transactionDetails,
                    transaction.getTimestamp()
            );

            System.out.println("=== Transaction Processing Completed Successfully ===");
            return result;

        } catch (TransactionException e) {
            System.err.println("Transaction error: " + e.getMessage());
            rollbackTransaction(conn);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Unexpected error during transaction processing: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            rollbackTransaction(conn);
            throw new TransactionException(errorMessage, e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Enhanced validation with detailed logging
     */
    private void validateTransactionInput(List<TransactionItem> items, User user, Store store, double paymentAmount)
            throws TransactionException {

        System.out.println("Validating transaction input...");

        if (items == null || items.isEmpty()) {
            throw new TransactionException("Transaction must contain at least one item");
        }

        if (user == null || user.getId() <= 0) {
            throw new TransactionException("Valid user information is required. User: " + user);
        }

        if (store == null || store.getId() <= 0) {
            throw new TransactionException("Valid store information is required. Store: " + store);
        }

        if (paymentAmount < 0) {
            throw new TransactionException("Payment amount cannot be negative: " + paymentAmount);
        }

        // Validate each transaction item with detailed logging
        for (int i = 0; i < items.size(); i++) {
            TransactionItem item = items.get(i);
            System.out.println("Validating item " + (i + 1) + ": " + item);

            if (item == null) {
                throw new TransactionException("Transaction item " + (i + 1) + " is null");
            }

            if (item.getProductId() <= 0) {
                throw new TransactionException("Invalid product ID for item " + (i + 1) + ": " + item.getProductId());
            }

            if (item.getQuantity() <= 0) {
                throw new TransactionException("Quantity must be greater than 0 for item " + (i + 1) + ": " + item.getQuantity());
            }

            if (item.getPricePerUnit() < 0) {
                throw new TransactionException("Price per unit cannot be negative for item " + (i + 1) + ": " + item.getPricePerUnit());
            }
        }

        System.out.println("Input validation completed successfully");
    }

    /**
     * Enhanced transaction calculation with detailed validation
     */
    private TransactionCalculation validateAndCalculateTransaction(List<TransactionItem> items, double paymentAmount)
            throws TransactionException {

        System.out.println("Calculating and validating transaction...");

        double totalAmount = 0.0;
        Map<String, Double> itemTotals = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            TransactionItem item = items.get(i);
            System.out.println("Processing item " + (i + 1) + ": Product ID " + item.getProductId());

            // Validate product exists
            Product product = productDAO.findProduct(item.getProductId());
            if (product == null) {
                throw new TransactionException("Product not found for ID: " + item.getProductId());
            }
            System.out.println("Product found: " + product.getName() + " (Base price: Rp " + product.getBasePrice() + ")");

            // Validate variation exists (if specified)
            ProductVariation variation = null;
            if (item.getVariationId() > 0) {
                variation = productVariationDAO.findProductVariation(item.getVariationId());
                if (variation == null) {
                    throw new TransactionException("Product variation not found for ID: " + item.getVariationId());
                }

                // Verify variation belongs to the product
                if (variation.getProductId() != item.getProductId()) {
                    throw new TransactionException("Product variation " + item.getVariationId() +
                            " does not belong to product " + item.getProductId());
                }
                System.out.println("Variation found: " + variation.getValue() + " (Additional price: Rp " + variation.getAdditionalPrice() + ")");
            }

            // Calculate and validate price
            double expectedPrice = calculateItemPrice(product, variation);
            System.out.println("Expected price: Rp " + expectedPrice + ", Item price: Rp " + item.getPricePerUnit());

            if (Math.abs(item.getPricePerUnit() - expectedPrice) > 0.01) {
                System.out.println("Price mismatch detected - using expected price");
                item.setPricePerUnit(expectedPrice); // Auto-correct the price
            }

            double itemTotal = item.getQuantity() * item.getPricePerUnit();
            totalAmount += itemTotal;

            String itemKey = product.getName() + (variation != null ? " (" + variation.getValue() + ")" : "");
            itemTotals.put(itemKey, itemTotal);

            System.out.println("Item total: Rp " + String.format("%,.2f", itemTotal));
        }

        System.out.println("Total amount: Rp " + String.format("%,.2f", totalAmount));

        // Validate payment amount
        if (paymentAmount < totalAmount) {
            throw new TransactionException(String.format(
                    "Insufficient payment. Total: Rp %,.2f, Payment: Rp %,.2f, Shortage: Rp %,.2f",
                    totalAmount, paymentAmount, totalAmount - paymentAmount
            ));
        }

        double changeAmount = paymentAmount - totalAmount;
        System.out.println("Change amount: Rp " + String.format("%,.2f", changeAmount));

        return new TransactionCalculation(totalAmount, changeAmount, itemTotals);
    }

    /**
     * Calculate price for a product with optional variation
     */
    private double calculateItemPrice(Product product, ProductVariation variation) {
        double basePrice = product.getBasePrice();
        double additionalPrice = variation != null ? variation.getAdditionalPrice() : 0.0;
        return basePrice + additionalPrice;
    }

    /**
     * Enhanced stock validation with detailed logging
     */
    private void validateStockAvailability(List<TransactionItem> items) throws TransactionException {
        System.out.println("Validating stock availability...");

        List<String> stockErrors = new ArrayList<>();

        // Group items by variation to handle multiple quantities of same variation
        Map<Integer, Integer> variationQuantities = items.stream()
                .filter(item -> item.getVariationId() > 0)
                .collect(Collectors.groupingBy(
                        TransactionItem::getVariationId,
                        Collectors.summingInt(TransactionItem::getQuantity)
                ));

        System.out.println("Checking stock for " + variationQuantities.size() + " variations");

        for (Map.Entry<Integer, Integer> entry : variationQuantities.entrySet()) {
            int variationId = entry.getKey();
            int requiredQuantity = entry.getValue();

            System.out.println("Checking variation ID " + variationId + " - required: " + requiredQuantity);

            ProductVariation variation = productVariationDAO.findProductVariation(variationId);
            if (variation == null) {
                stockErrors.add("Variation ID " + variationId + " not found");
                continue;
            }

            System.out.println("Available stock: " + variation.getStocks());

            if (variation.getStocks() < requiredQuantity) {
                Product product = productDAO.findProduct(variation.getProductId());
                String productName = product != null ? product.getName() : "Unknown Product";

                stockErrors.add(String.format(
                        "%s (%s): Available %d, Required %d",
                        productName, variation.getValue(), variation.getStocks(), requiredQuantity
                ));
            }
        }

        if (!stockErrors.isEmpty()) {
            throw new TransactionException("Insufficient stock for the following items:\n" +
                    String.join("\n", stockErrors));
        }

        System.out.println("Stock validation completed successfully");
    }

    /**
     * Create transaction record with proper validation
     */
    private Transaction createTransactionRecord(Store store, User user, double totalAmount) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Transaction transaction = new Transaction(store.getId(), user.getId(), currentTime, totalAmount);

        System.out.println("Created transaction record: Store ID=" + store.getId() +
                ", User ID=" + user.getId() + ", Total=" + totalAmount + ", Time=" + currentTime);

        return transaction;
    }

    /**
     * Insert transaction record with enhanced error handling
     */
    private int insertTransactionRecord(Transaction transaction) throws TransactionException {
        try {
            System.out.println("Inserting transaction record...");
            int transactionId = transactionDAO.insertTransaction(transaction);

            if (transactionId <= 0) {
                throw new TransactionException("Failed to insert transaction record - DAO returned invalid ID: " + transactionId);
            }

            System.out.println("Transaction record inserted successfully with ID: " + transactionId);
            return transactionId;

        } catch (Exception e) {
            String errorMsg = "Database error while creating transaction: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            throw new TransactionException(errorMsg, e);
        }
    }

    /**
     * Enhanced transaction items processing with detailed error handling
     */
    private List<TransactionDetail> processTransactionItems(int transactionId, List<TransactionItem> items)
            throws TransactionException {

        System.out.println("Processing " + items.size() + " transaction items for transaction ID: " + transactionId);

        List<TransactionDetail> transactionDetails = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            TransactionItem item = items.get(i);
            System.out.println("Processing item " + (i + 1) + "/" + items.size() + ": " + item);

            try {
                // Create transaction detail with proper transaction ID
                TransactionDetail detail = new TransactionDetail(
                        item.getProductId(),
                        item.getVariationId(),
                        item.getQuantity(),
                        item.getPricePerUnit()
                );

                // CRITICAL: Set transaction ID BEFORE insertion
                detail.setTransactionId(transactionId);

                System.out.println("Created TransactionDetail: " +
                        "TransactionID=" + detail.getTransactionId() +
                        ", ProductID=" + detail.getProductID() +
                        ", VariationID=" + detail.getVariationID() +
                        ", Quantity=" + detail.getQuantity() +
                        ", PricePerUnit=" + detail.getPricePerUnit());

                // Validate detail before insertion
                validateTransactionDetail(detail);

                // Insert transaction detail
                int detailId = transactionDetailDAO.insertTransactionDetail(detail);
                System.out.println("TransactionDetail insertion result: " + detailId);

                if (detailId <= 0) {
                    throw new TransactionException("Failed to create transaction detail for product " +
                            item.getProductId() + " - DAO returned invalid ID: " + detailId);
                }

                detail.setId(detailId);
                transactionDetails.add(detail);

                System.out.println("Successfully created transaction detail with ID: " + detailId);

            } catch (Exception e) {
                String errorMsg = "Error processing transaction item " + (i + 1) + " (Product ID: " +
                        item.getProductId() + "): " + e.getMessage();
                System.err.println(errorMsg);
                e.printStackTrace();
                throw new TransactionException(errorMsg, e);
            }
        }

        System.out.println("Successfully processed all " + transactionDetails.size() + " transaction items");
        return transactionDetails;
    }

    /**
     * Validate transaction detail before insertion
     */
    private void validateTransactionDetail(TransactionDetail detail) throws TransactionException {
        if (detail.getTransactionId() <= 0) {
            throw new TransactionException("Transaction ID must be set and greater than 0: " + detail.getTransactionId());
        }

        if (detail.getProductID() <= 0) {
            throw new TransactionException("Product ID must be greater than 0: " + detail.getProductID());
        }

        if (detail.getQuantity() <= 0) {
            throw new TransactionException("Quantity must be greater than 0: " + detail.getQuantity());
        }

        if (detail.getPricePerUnit() < 0) {
            throw new TransactionException("Price per unit cannot be negative: " + detail.getPricePerUnit());
        }

        System.out.println("TransactionDetail validation passed");
    }

    /**
     * Enhanced stock update with detailed logging
     */
    private void updateProductStocks(List<TransactionItem> items) throws TransactionException {
        System.out.println("Updating product stocks...");

        // Group items by variation to handle multiple quantities
        Map<Integer, Integer> variationQuantities = items.stream()
                .filter(item -> item.getVariationId() > 0)
                .collect(Collectors.groupingBy(
                        TransactionItem::getVariationId,
                        Collectors.summingInt(TransactionItem::getQuantity)
                ));

        System.out.println("Updating stock for " + variationQuantities.size() + " variations");

        for (Map.Entry<Integer, Integer> entry : variationQuantities.entrySet()) {
            int variationId = entry.getKey();
            int quantityToReduce = entry.getValue();

            System.out.println("Updating stock for variation " + variationId + " - reducing by " + quantityToReduce);

            try {
                ProductVariation variation = productVariationDAO.findProductVariation(variationId);
                if (variation == null) {
                    throw new TransactionException("Product variation not found during stock update: " + variationId);
                }

                int originalStock = variation.getStocks();
                int newStock = originalStock - quantityToReduce;

                System.out.println("Stock update: " + originalStock + " -> " + newStock);

                if (newStock < 0) {
                    throw new TransactionException("Stock would become negative for variation " + variationId +
                            ": " + originalStock + " - " + quantityToReduce + " = " + newStock);
                }

                variation.setStocks(newStock);
                int updateResult = productVariationDAO.updateProductVariation(variationId, variation);

                if (updateResult <= 0) {
                    throw new TransactionException("Failed to update stock for variation " + variationId +
                            " - DAO returned: " + updateResult);
                }

                System.out.println("Successfully updated stock for variation " + variationId +
                        ": " + originalStock + " -> " + newStock);

            } catch (Exception e) {
                String errorMsg = "Error updating stock for variation " + variationId + ": " + e.getMessage();
                System.err.println(errorMsg);
                e.printStackTrace();
                throw new TransactionException(errorMsg, e);
            }
        }

        System.out.println("Stock update completed successfully");
    }

    /**
     * Enhanced rollback with logging
     */
    private void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                System.out.println("Rolling back transaction...");
                conn.rollback();
                System.out.println("Transaction rolled back successfully");
            } catch (SQLException e) {
                System.err.println("Failed to rollback transaction: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Enhanced connection cleanup
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ==================== REPORTING METHODS ====================

    /**
     * Get transaction history for a specific store
     */
    public List<Transaction> getTransactionHistory(int storeId) {
        try {
            return transactionDAO.findAllTransactions().stream()
                    .filter(t -> t.getStoreID() == storeId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get transaction details for a specific transaction
     */
    public List<TransactionDetail> getTransactionDetails(int transactionId) {
        try {
            return transactionDetailDAO.findAllTransactionDetails().stream()
                    .filter(td -> td.getTransactionId() == transactionId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error retrieving transaction details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get daily revenue for a store
     */
    public double getDailyRevenue(int storeId, java.sql.Date date) {
        try {
            List<Transaction> transactions = getTransactionHistory(storeId);
            return transactions.stream()
                    .filter(t -> isSameDay(t.getTimestamp(), date))
                    .mapToDouble(Transaction::getTotal)
                    .sum();
        } catch (Exception e) {
            System.err.println("Error calculating daily revenue: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Get total revenue for a store within date range
     */
    public double getTotalRevenue(int storeId, java.sql.Date startDate, java.sql.Date endDate) {
        try {
            List<Transaction> transactions = getTransactionHistory(storeId);
            return transactions.stream()
                    .filter(t -> isWithinDateRange(t.getTimestamp(), startDate, endDate))
                    .mapToDouble(Transaction::getTotal)
                    .sum();
        } catch (Exception e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Get transaction count for a store within date range
     */
    public int getTransactionCount(int storeId, java.sql.Date startDate, java.sql.Date endDate) {
        try {
            List<Transaction> transactions = getTransactionHistory(storeId);
            return (int) transactions.stream()
                    .filter(t -> isWithinDateRange(t.getTimestamp(), startDate, endDate))
                    .count();
        } catch (Exception e) {
            System.err.println("Error calculating transaction count: " + e.getMessage());
            return 0;
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Check if timestamp is on the same day as date
     */
    private boolean isSameDay(Timestamp timestamp, java.sql.Date date) {
        java.sql.Date transactionDate = new java.sql.Date(timestamp.getTime());
        return transactionDate.equals(date);
    }

    /**
     * Check if timestamp is within date range
     */
    private boolean isWithinDateRange(Timestamp timestamp, java.sql.Date startDate, java.sql.Date endDate) {
        java.sql.Date transactionDate = new java.sql.Date(timestamp.getTime());
        return !transactionDate.before(startDate) && !transactionDate.after(endDate);
    }

    // ==================== INNER CLASSES ====================

    /**
     * Enhanced TransactionItem with better validation
     */
    public static class TransactionItem {
        private int productId;
        private int variationId;
        private int quantity;
        private double pricePerUnit;

        public TransactionItem(int productId, int variationId, int quantity, double pricePerUnit) {
            this.productId = productId;
            this.variationId = variationId;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
        }

        // Getters
        public int getProductId() { return productId; }
        public int getVariationId() { return variationId; }
        public int getQuantity() { return quantity; }
        public double getPricePerUnit() { return pricePerUnit; }

        // Setters with validation
        public void setProductId(int productId) {
            if (productId <= 0) throw new IllegalArgumentException("Product ID must be greater than 0");
            this.productId = productId;
        }

        public void setVariationId(int variationId) {
            this.variationId = variationId;
        }

        public void setQuantity(int quantity) {
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
            this.quantity = quantity;
        }

        public void setPricePerUnit(double pricePerUnit) {
            if (pricePerUnit < 0) throw new IllegalArgumentException("Price per unit cannot be negative");
            this.pricePerUnit = pricePerUnit;
        }

        public double getTotalPrice() {
            return quantity * pricePerUnit;
        }

        @Override
        public String toString() {
            return String.format("TransactionItem{productId=%d, variationId=%d, quantity=%d, pricePerUnit=%.2f, total=%.2f}",
                    productId, variationId, quantity, pricePerUnit, getTotalPrice());
        }
    }

    /**
     * Represents transaction calculation results
     */
    private static class TransactionCalculation {
        private final double totalAmount;
        private final double changeAmount;
        private final Map<String, Double> itemTotals;

        public TransactionCalculation(double totalAmount, double changeAmount, Map<String, Double> itemTotals) {
            this.totalAmount = totalAmount;
            this.changeAmount = changeAmount;
            this.itemTotals = itemTotals;
        }

        public double getTotalAmount() { return totalAmount; }
        public double getChangeAmount() { return changeAmount; }
        public Map<String, Double> getItemTotals() { return itemTotals; }
    }

    /**
     * Represents the result of a completed transaction
     */
    public static class TransactionResult {
        private final int transactionId;
        private final double totalAmount;
        private final double paymentAmount;
        private final double changeAmount;
        private final List<TransactionDetail> transactionDetails;
        private final Timestamp transactionTime;

        public TransactionResult(int transactionId, double totalAmount, double paymentAmount,
                                 double changeAmount, List<TransactionDetail> transactionDetails,
                                 Timestamp transactionTime) {
            this.transactionId = transactionId;
            this.totalAmount = totalAmount;
            this.paymentAmount = paymentAmount;
            this.changeAmount = changeAmount;
            this.transactionDetails = transactionDetails;
            this.transactionTime = transactionTime;
        }

        // Getters
        public int getTransactionId() { return transactionId; }
        public double getTotalAmount() { return totalAmount; }
        public double getPaymentAmount() { return paymentAmount; }
        public double getChangeAmount() { return changeAmount; }
        public List<TransactionDetail> getTransactionDetails() { return transactionDetails; }
        public Timestamp getTransactionTime() { return transactionTime; }

        @Override
        public String toString() {
            return String.format("TransactionResult{id=%d, total=%.2f, payment=%.2f, change=%.2f, items=%d}",
                    transactionId, totalAmount, paymentAmount, changeAmount, transactionDetails.size());
        }
    }

    /**
     * Custom exception for transaction-related errors
     */
    public static class TransactionException extends Exception {
        public TransactionException(String message) {
            super(message);
        }

        public TransactionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
