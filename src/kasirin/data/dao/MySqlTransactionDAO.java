package kasirin.data.dao;

import kasirin.data.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlTransactionDAO implements TransactionDAO {
    @Override
    public int insertTransaction(Transaction transaction) {
        int result = -1;

        String query = "INSERT INTO Transactions (store_id, user_id, transaction_time, total) VALUE (?,?,?,?)";
        // either use try-with-resource or finally block, so ur computer don't explode.
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getStoreID());
            stmt.setInt(2, transaction.getUserID());
            // TODO: set transaction_time
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
                    // TODO: add get datetime
                    double total = rs.getDouble("total");

                    transaction = new Transaction(storeId, userId, null, total);
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
            // TODO: add set datetime
            stmt.setDouble(4, transaction.getTotal());
            stmt.setInt(5, id);
            result = stmt.executeUpdate(); // number of affected rows
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
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
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public List<Transaction> findAllTransactions() {
        List<Transaction> result = new ArrayList<>();

        String query = "SELECT * FROM Transactions";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int transactionId = rs.getInt("id");
                int storeId = rs.getInt("store_id");
                int userId = rs.getInt("user_id");
                // TODO: add get datetime
                double total = rs.getDouble("total");

                Transaction transaction = new Transaction(storeId, userId, null, total);
                transaction.setId(transactionId);
                result.add(transaction);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
