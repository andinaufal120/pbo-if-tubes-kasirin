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
 * Service class for handling transaction-related business logic
 * Manages complete transaction processing including stock updates and price calculations
 *
 * Features:
 * - Product selection and stock validation
 * - Automatic stock reduction on successful transactions
 * - Price calculation with base_price + additional_price
 * - Database transaction management with rollback capability
 * - Comprehensive error handling and validation
 *
 * @author yamaym
 */
public class TransactionService {
    private final DAOFactory daoFactory;
    private final TransactionDAO transactionDAO;
    private final TransactionDetailDAO transactionDetailDAO;
    private final ProductVariationDAO productVariationDAO;
    private final ProductDAO productDAO;

    /**
     * Constructor initializes all required DAOs
     */
    public TransactionService() {
        this.daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.transactionDAO = daoFactory.getTransactionDAO();
        this.transactionDetailDAO = daoFactory.getTransactionDetailDAO();
        this.productVariationDAO = daoFactory.getProductVariationDAO();
        this.productDAO = daoFactory.getProductDAO();
    }

    /**
     * Process a complete transaction with comprehensive validation and error handling
     *
     * @param items List of transaction items to process
     * @param user User performing the transaction (cashier)
     * @param store Store where transaction occurs
     * @param paymentAmount Amount paid by customer
     * @return TransactionResult containing transaction ID and details
     * @throws TransactionException if transaction processing fails
     */
    public TransactionResult processCompleteTransaction(List<TransactionItem> items, User user, Store store, double paymentAmount)
            throws TransactionException {

        // Input validation
        validateTransactionInput(items, user, store, paymentAmount);

        Connection conn = null;
        try {
            // Get database connection and start transaction
            conn = MySqlDAOFactory.getConnection();
            if (conn == null) {
                throw new TransactionException("Failed to establish database connection");
            }

            conn.setAutoCommit(false);

            // Step 1: Validate and calculate transaction details
            TransactionCalculation calculation = validateAndCalculateTransaction(items, paymentAmount);

            // Step 2: Validate stock availability for all items
            validateStockAvailability(items);

            // Step 3: Create main transaction record
            Transaction transaction = createTransactionRecord(store, user, calculation.getTotalAmount());
            int transactionId = insertTransactionRecord(transaction);

            if (transactionId <= 0) {
                throw new TransactionException("Failed to create transaction record");
            }

            // Step 4: Process each transaction item
            List<TransactionDetail> transactionDetails = processTransactionItems(transactionId, items);

            // Step 5: Update product variation stocks
            updateProductStocks(items);

            // Step 6: Commit transaction
            conn.commit();

            // Step 7: Create and return result
            TransactionResult result = new TransactionResult(
                    transactionId,
                    calculation.getTotalAmount(),
                    paymentAmount,
                    calculation.getChangeAmount(),
                    transactionDetails,
                    transaction.getTimestamp()
            );

            System.out.println("Transaction processed successfully. ID: " + transactionId);
            return result;

        } catch (TransactionException e) {
            rollbackTransaction(conn);
            throw e;
        } catch (Exception e) {
            rollbackTransaction(conn);
            String errorMessage = "Unexpected error during transaction processing: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            throw new TransactionException(errorMessage, e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Validate transaction input parameters
     */
    private void validateTransactionInput(List<TransactionItem> items, User user, Store store, double paymentAmount)
            throws TransactionException {

        if (items == null || items.isEmpty()) {
            throw new TransactionException("Transaction must contain at least one item");
        }

        if (user == null) {
            throw new TransactionException("User information is required");
        }

        if (store == null) {
            throw new TransactionException("Store information is required");
        }

        if (paymentAmount < 0) {
            throw new TransactionException("Payment amount cannot be negative");
        }

        // Validate each transaction item
        for (int i = 0; i < items.size(); i++) {
            TransactionItem item = items.get(i);
            if (item == null) {
                throw new TransactionException("Transaction item " + (i + 1) + " is null");
            }

            if (item.getQuantity() <= 0) {
                throw new TransactionException("Quantity must be greater than 0 for item " + (i + 1));
            }

            if (item.getPricePerUnit() < 0) {
                throw new TransactionException("Price per unit cannot be negative for item " + (i + 1));
            }
        }
    }

    /**
     * Validate transaction items and calculate totals
     */
    private TransactionCalculation validateAndCalculateTransaction(List<TransactionItem> items, double paymentAmount)
            throws TransactionException {

        double totalAmount = 0.0;
        Map<String, Double> itemTotals = new HashMap<>();

        for (TransactionItem item : items) {
            // Validate product exists
            Product product = productDAO.findProduct(item.getProductId());
            if (product == null) {
                throw new TransactionException("Product not found: ID " + item.getProductId());
            }

            // Validate variation exists (if specified)
            ProductVariation variation = null;
            if (item.getVariationId() > 0) {
                variation = productVariationDAO.findProductVariation(item.getVariationId());
                if (variation == null) {
                    throw new TransactionException("Product variation not found: ID " + item.getVariationId());
                }

                // Verify variation belongs to the product
                if (variation.getProductId() != item.getProductId()) {
                    throw new TransactionException("Product variation " + item.getVariationId() +
                            " does not belong to product " + item.getProductId());
                }
            }

            // Calculate and validate price
            double expectedPrice = calculateItemPrice(product, variation);
            if (Math.abs(item.getPricePerUnit() - expectedPrice) > 0.01) {
                throw new TransactionException("Price mismatch for product " + product.getName() +
                        ". Expected: " + expectedPrice + ", Provided: " + item.getPricePerUnit());
            }

            double itemTotal = item.getQuantity() * item.getPricePerUnit();
            totalAmount += itemTotal;

            String itemKey = product.getName() + (variation != null ? " (" + variation.getValue() + ")" : "");
            itemTotals.put(itemKey, itemTotal);
        }

        // Validate payment amount
        if (paymentAmount < totalAmount) {
            throw new TransactionException(String.format(
                    "Insufficient payment. Total: Rp %,.2f, Payment: Rp %,.2f, Shortage: Rp %,.2f",
                    totalAmount, paymentAmount, totalAmount - paymentAmount
            ));
        }

        double changeAmount = paymentAmount - totalAmount;

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
     * Validate stock availability for all transaction items
     */
    private void validateStockAvailability(List<TransactionItem> items) throws TransactionException {
        List<String> stockErrors = new ArrayList<>();

        // Group items by variation to handle multiple quantities of same variation
        Map<Integer, Integer> variationQuantities = items.stream()
                .filter(item -> item.getVariationId() > 0)
                .collect(Collectors.groupingBy(
                        TransactionItem::getVariationId,
                        Collectors.summingInt(TransactionItem::getQuantity)
                ));

        for (Map.Entry<Integer, Integer> entry : variationQuantities.entrySet()) {
            int variationId = entry.getKey();
            int requiredQuantity = entry.getValue();

            ProductVariation variation = productVariationDAO.findProductVariation(variationId);
            if (variation == null) {
                stockErrors.add("Variation ID " + variationId + " not found");
                continue;
            }

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
    }

    /**
     * Create transaction record with current timestamp
     */
    private Transaction createTransactionRecord(Store store, User user, double totalAmount) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return new Transaction(store.getId(), user.getId(), currentTime, totalAmount);
    }

    /**
     * Insert transaction record to database
     */
    private int insertTransactionRecord(Transaction transaction) throws TransactionException {
        try {
            int transactionId = transactionDAO.insertTransaction(transaction);
            if (transactionId <= 0) {
                throw new TransactionException("Failed to insert transaction record");
            }
            return transactionId;
        } catch (Exception e) {
            throw new TransactionException("Database error while creating transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Process all transaction items and create transaction details
     */
    private List<TransactionDetail> processTransactionItems(int transactionId, List<TransactionItem> items)
            throws TransactionException {

        List<TransactionDetail> transactionDetails = new ArrayList<>();

        for (TransactionItem item : items) {
            try {
                TransactionDetail detail = new TransactionDetail(
                        item.getProductId(),
                        item.getVariationId(),
                        item.getQuantity(),
                        item.getPricePerUnit()
                );
                detail.setTransactionId(transactionId);

                int detailId = transactionDetailDAO.insertTransactionDetail(detail);
                if (detailId <= 0) {
                    throw new TransactionException("Failed to create transaction detail for product " + item.getProductId());
                }

                detail.setId(detailId);
                transactionDetails.add(detail);

            } catch (Exception e) {
                throw new TransactionException("Error processing transaction item: " + e.getMessage(), e);
            }
        }

        return transactionDetails;
    }

    /**
     * Update product variation stocks after successful transaction
     */
    private void updateProductStocks(List<TransactionItem> items) throws TransactionException {
        // Group items by variation to handle multiple quantities
        Map<Integer, Integer> variationQuantities = items.stream()
                .filter(item -> item.getVariationId() > 0)
                .collect(Collectors.groupingBy(
                        TransactionItem::getVariationId,
                        Collectors.summingInt(TransactionItem::getQuantity)
                ));

        for (Map.Entry<Integer, Integer> entry : variationQuantities.entrySet()) {
            int variationId = entry.getKey();
            int quantityToReduce = entry.getValue();

            try {
                ProductVariation variation = productVariationDAO.findProductVariation(variationId);
                if (variation == null) {
                    throw new TransactionException("Product variation not found during stock update: " + variationId);
                }

                int newStock = variation.getStocks() - quantityToReduce;
                if (newStock < 0) {
                    throw new TransactionException("Stock would become negative for variation " + variationId);
                }

                variation.setStocks(newStock);
                int updateResult = productVariationDAO.updateProductVariation(variationId, variation);

                if (updateResult <= 0) {
                    throw new TransactionException("Failed to update stock for variation " + variationId);
                }

                System.out.println("Updated stock for variation " + variationId +
                        ": " + (variation.getStocks() + quantityToReduce) + " -> " + newStock);

            } catch (Exception e) {
                throw new TransactionException("Error updating stock for variation " + variationId + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Rollback database transaction
     */
    private void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back successfully");
            } catch (SQLException e) {
                System.err.println("Failed to rollback transaction: " + e.getMessage());
            }
        }
    }

    /**
     * Close database connection and restore auto-commit
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
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
     * Represents a transaction item for processing
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

        // Setters
        public void setProductId(int productId) { this.productId = productId; }
        public void setVariationId(int variationId) { this.variationId = variationId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

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
