package kasirin.data.dao;

import kasirin.data.model.TransactionDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlTransactionDetailDAO implements TransactionDetailDAO {
    @Override
    public int insertTransactionDetail(TransactionDetail transactionDetail) {
        int result = -1;

        String query = "INSERT INTO TransactionDetails (transaction_id, products_id, variation_id, quantity, price_per_unit) VALUES (?,?,?,?,?)";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transactionDetail.getTransactionId());
            stmt.setInt(2, transactionDetail.getProductID());
            stmt.setInt(3, transactionDetail.getVariationID());
            stmt.setInt(4, transactionDetail.getQuantity());
            stmt.setDouble(5, transactionDetail.getPricePerUnit());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
            System.out.println(e.getMessage());
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public List<TransactionDetail> findAllTransactionDetails() {
        List<TransactionDetail> result = new ArrayList<>();

        String query = "SELECT * FROM TransactionDetails";
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Find transaction details by transaction ID
     */
    public List<TransactionDetail> findDetailsByTransactionId(int transactionId) {
        List<TransactionDetail> result = new ArrayList<>();

        String query = "SELECT * FROM TransactionDetails WHERE transaction_id = ?";
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
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
