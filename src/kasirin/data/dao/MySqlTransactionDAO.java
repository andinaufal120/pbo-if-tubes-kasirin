package kasirin.data.dao;

import kasirin.data.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MySqlTransactionDAO implements TransactionDAO {
    @Override
    public int insertTransaction(Transaction transaction) {
        int result = -1;

        String query = "INSERT INTO Transactions (store_id, user_id, transaction_time, total) VALUES (?,?,?,?)";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getStoreID());
            stmt.setInt(2, transaction.getUserID());
            stmt.setTimestamp(3, transaction.getTimestamp());
            stmt.setDouble(4, transaction.getTotal());
            stmt.executeUpdate();

            // gets newly created primary key
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
    public Transaction findTransaction(int id) {
        Transaction transaction = null;

        String query = "SELECT * FROM Transactions WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int transactionId = rs.getInt("id");
                    int storeId = rs.getInt("store_id");
                    int userId = rs.getInt("user_id");
                    Timestamp transactionTime = rs.getTimestamp("transaction_time");
                    double total = rs.getDouble("total");

                    transaction = new Transaction(storeId, userId, transactionTime, total);
                    transaction.setId(transactionId);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return transaction;
    }

    @Override
    public int updateTransaction(int id, Transaction transaction) {
        int result = -1;

        String query = "UPDATE Transactions SET store_id=?,user_id=?,transaction_time=?,total=? WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transaction.getStoreID());
            stmt.setInt(2, transaction.getUserID());
            stmt.setTimestamp(3, transaction.getTimestamp());
            stmt.setDouble(4, transaction.getTotal());
            stmt.setInt(5, id);
            result = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public int deleteTransaction(int id) {
        int result = -1;

        String query = "DELETE FROM Transactions WHERE id=?";
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
    public List<Transaction> findAllTransactions() {
        List<Transaction> result = new ArrayList<>();

        String query = "SELECT * FROM Transactions ORDER BY transaction_time DESC";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int transactionId = rs.getInt("id");
                int storeId = rs.getInt("store_id");
                int userId = rs.getInt("user_id");
                Timestamp transactionTime = rs.getTimestamp("transaction_time");
                double total = rs.getDouble("total");

                Transaction transaction = new Transaction(storeId, userId, transactionTime, total);
                transaction.setId(transactionId);
                result.add(transaction);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Get transactions by store ID
     */
    public List<Transaction> findTransactionsByStore(int storeId) {
        List<Transaction> result = new ArrayList<>();

        String query = "SELECT * FROM Transactions WHERE store_id = ? ORDER BY transaction_time DESC";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int transactionId = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    Timestamp transactionTime = rs.getTimestamp("transaction_time");
                    double total = rs.getDouble("total");

                    Transaction transaction = new Transaction(storeId, userId, transactionTime, total);
                    transaction.setId(transactionId);
                    result.add(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Get transactions by store and date range
     */
    public List<Transaction> findTransactionsByStoreAndDateRange(int storeId, java.sql.Date startDate, java.sql.Date endDate) {
        List<Transaction> result = new ArrayList<>();

        String query = "SELECT * FROM Transactions WHERE store_id = ? AND DATE(transaction_time) BETWEEN ? AND ? ORDER BY transaction_time DESC";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int transactionId = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    Timestamp transactionTime = rs.getTimestamp("transaction_time");
                    double total = rs.getDouble("total");

                    Transaction transaction = new Transaction(storeId, userId, transactionTime, total);
                    transaction.setId(transactionId);
                    result.add(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Get daily revenue for a store
     */
    public double getDailyRevenue(int storeId, java.sql.Date date) {
        double revenue = 0.0;

        String query = "SELECT SUM(total) as daily_revenue FROM Transactions WHERE store_id = ? AND DATE(transaction_time) = ?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId);
            stmt.setDate(2, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    revenue = rs.getDouble("daily_revenue");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return revenue;
    }

    /**
     * Get total revenue for a store within date range
     */
    public double getTotalRevenue(int storeId, java.sql.Date startDate, java.sql.Date endDate) {
        double revenue = 0.0;

        String query = "SELECT SUM(total) as total_revenue FROM Transactions WHERE store_id = ? AND DATE(transaction_time) BETWEEN ? AND ?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, storeId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    revenue = rs.getDouble("total_revenue");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return revenue;
    }
}
