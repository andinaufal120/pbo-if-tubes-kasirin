package kasirin.data.dao;

import kasirin.data.model.TransactionDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced MySQL implementation of TransactionDetailDAO with improved error handling
 */
public class MySqlTransactionDetailDAO implements TransactionDetailDAO {

    @Override
    public int insertTransactionDetail(TransactionDetail transactionDetail) {
        System.out.println("=== Inserting TransactionDetail ===");
        System.out.println("TransactionID: " + transactionDetail.getTransactionId());
        System.out.println("ProductID: " + transactionDetail.getProductID());
        System.out.println("VariationID: " + transactionDetail.getVariationID());
        System.out.println("Quantity: " + transactionDetail.getQuantity());
        System.out.println("PricePerUnit: " + transactionDetail.getPricePerUnit());

        // Validate input before insertion
        if (transactionDetail.getTransactionId() <= 0) {
            System.err.println("ERROR: Invalid transaction ID: " + transactionDetail.getTransactionId());
            return -1;
        }

        if (transactionDetail.getProductID() <= 0) {
            System.err.println("ERROR: Invalid product ID: " + transactionDetail.getProductID());
            return -1;
        }

        if (transactionDetail.getQuantity() <= 0) {
            System.err.println("ERROR: Invalid quantity: " + transactionDetail.getQuantity());
            return -1;
        }

        if (transactionDetail.getPricePerUnit() < 0) {
            System.err.println("ERROR: Invalid price per unit: " + transactionDetail.getPricePerUnit());
            return -1;
        }

        int result = -1;
        String query = "INSERT INTO TransactionDetails (transaction_id, products_id, variation_id, quantity, price_per_unit) VALUES (?,?,?,?,?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = MySqlDAOFactory.getConnection();
            if (conn == null) {
                System.err.println("ERROR: Failed to get database connection");
                return -1;
            }

            System.out.println("Database connection obtained");
            System.out.println("Executing query: " + query);

            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // Set parameters with logging
            stmt.setInt(1, transactionDetail.getTransactionId());
            System.out.println("Parameter 1 (transaction_id): " + transactionDetail.getTransactionId());

            stmt.setInt(2, transactionDetail.getProductID());
            System.out.println("Parameter 2 (products_id): " + transactionDetail.getProductID());

            stmt.setInt(3, transactionDetail.getVariationID());
            System.out.println("Parameter 3 (variation_id): " + transactionDetail.getVariationID());

            stmt.setInt(4, transactionDetail.getQuantity());
            System.out.println("Parameter 4 (quantity): " + transactionDetail.getQuantity());

            stmt.setDouble(5, transactionDetail.getPricePerUnit());
            System.out.println("Parameter 5 (price_per_unit): " + transactionDetail.getPricePerUnit());

            // Execute the insert
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    result = rs.getInt(1);
                    System.out.println("Generated key (detail ID): " + result);
                } else {
                    System.err.println("ERROR: No generated keys returned");
                }
            } else {
                System.err.println("ERROR: No rows were inserted");
            }

        } catch (SQLException e) {
            System.err.println("SQL ERROR in insertTransactionDetail:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();

            // Check for specific constraint violations
            if (e.getErrorCode() == 1452) { // Foreign key constraint fails
                System.err.println("FOREIGN KEY CONSTRAINT VIOLATION:");
                if (e.getMessage().contains("transaction_id")) {
                    System.err.println("- Transaction ID " + transactionDetail.getTransactionId() + " does not exist in Transactions table");
                }
                if (e.getMessage().contains("products_id")) {
                    System.err.println("- Product ID " + transactionDetail.getProductID() + " does not exist in Products table");
                }
                if (e.getMessage().contains("variation_id")) {
                    System.err.println("- Variation ID " + transactionDetail.getVariationID() + " does not exist in ProductsVariations table");
                }
            }

        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR in insertTransactionDetail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("Database resources cleaned up");
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        System.out.println("Insert result: " + result);
        System.out.println("=== End TransactionDetail Insert ===");
        return result;
    }

    @Override
    public TransactionDetail findTransactionDetail(int id) {
        TransactionDetail transactionDetail = null;
        String query = "SELECT * FROM TransactionDetails WHERE id=?";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int detailId = rs.getInt("id");
                    int transactionId = rs.getInt("transaction_id");
                    int productId = rs.getInt("products_id");
                    int variationId = rs.getInt("variation_id");
                    int quantity = rs.getInt("quantity");
                    double pricePerUnit = rs.getDouble("price_per_unit");

                    transactionDetail = new TransactionDetail(productId, variationId, quantity, pricePerUnit);
                    transactionDetail.setId(detailId);
                    transactionDetail.setTransactionId(transactionId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in findTransactionDetail: " + e.getMessage());
            e.printStackTrace();
        }
        return transactionDetail;
    }

    @Override
    public int updateTransactionDetail(int id, TransactionDetail transactionDetail) {
        int result = -1;
        String query = "UPDATE TransactionDetails SET transaction_id=?,products_id=?,variation_id=?,quantity=?,price_per_unit=? WHERE id=?";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, transactionDetail.getTransactionId());
            stmt.setInt(2, transactionDetail.getProductID());
            stmt.setInt(3, transactionDetail.getVariationID());
            stmt.setInt(4, transactionDetail.getQuantity());
            stmt.setDouble(5, transactionDetail.getPricePerUnit());
            stmt.setInt(6, id);

            result = stmt.executeUpdate();
            System.out.println("Updated TransactionDetail ID " + id + ", rows affected: " + result);

        } catch (Exception e) {
            System.err.println("Error in updateTransactionDetail: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int deleteTransactionDetail(int id) {
        int result = -1;
        String query = "DELETE FROM TransactionDetails WHERE id=?";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            result = stmt.executeUpdate();
            System.out.println("Deleted TransactionDetail ID " + id + ", rows affected: " + result);

        } catch (Exception e) {
            System.err.println("Error in deleteTransactionDetail: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<TransactionDetail> findAllTransactionDetails() {
        List<TransactionDetail> result = new ArrayList<>();
        String query = "SELECT * FROM TransactionDetails ORDER BY id";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int detailId = rs.getInt("id");
                int transactionId = rs.getInt("transaction_id");
                int productId = rs.getInt("products_id");
                int variationId = rs.getInt("variation_id");
                int quantity = rs.getInt("quantity");
                double pricePerUnit = rs.getDouble("price_per_unit");

                TransactionDetail transactionDetail = new TransactionDetail(productId, variationId, quantity, pricePerUnit);
                transactionDetail.setId(detailId);
                transactionDetail.setTransactionId(transactionId);
                result.add(transactionDetail);
            }

            System.out.println("Retrieved " + result.size() + " transaction details");

        } catch (Exception e) {
            System.err.println("Error in findAllTransactionDetails: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Find transaction details by transaction ID with enhanced error handling
     */
    public List<TransactionDetail> findDetailsByTransactionId(int transactionId) {
        List<TransactionDetail> result = new ArrayList<>();
        String query = "SELECT * FROM TransactionDetails WHERE transaction_id = ? ORDER BY id";

        System.out.println("Finding transaction details for transaction ID: " + transactionId);

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, transactionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int detailId = rs.getInt("id");
                    int productId = rs.getInt("products_id");
                    int variationId = rs.getInt("variation_id");
                    int quantity = rs.getInt("quantity");
                    double pricePerUnit = rs.getDouble("price_per_unit");

                    TransactionDetail transactionDetail = new TransactionDetail(productId, variationId, quantity, pricePerUnit);
                    transactionDetail.setId(detailId);
                    transactionDetail.setTransactionId(transactionId);
                    result.add(transactionDetail);

                    System.out.println("Found detail: ID=" + detailId + ", ProductID=" + productId +
                            ", VariationID=" + variationId + ", Qty=" + quantity + ", Price=" + pricePerUnit);
                }
            }

            System.out.println("Found " + result.size() + " details for transaction " + transactionId);

        } catch (Exception e) {
            System.err.println("Error in findDetailsByTransactionId: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Verify if a transaction exists (for debugging foreign key issues)
     */
    public boolean transactionExists(int transactionId) {
        String query = "SELECT COUNT(*) FROM Transactions WHERE id = ?";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, transactionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Transaction " + transactionId + " exists: " + (count > 0));
                    return count > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking if transaction exists: " + e.getMessage());
        }

        return false;
    }

    /**
     * Verify if a product exists (for debugging foreign key issues)
     */
    public boolean productExists(int productId) {
        String query = "SELECT COUNT(*) FROM Products WHERE id = ?";

        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Product " + productId + " exists: " + (count > 0));
                    return count > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking if product exists: " + e.getMessage());
        }

        return false;
    }
}
