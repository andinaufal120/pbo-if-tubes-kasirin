package kasirin.data.dao;

import kasirin.data.model.Transaction;

import java.util.List;

/// Provides CRUD methods for "Transaction" entity in datasource.
///
/// @author yamaym
public interface TransactionDAO {
    /// Inserts a new transaction to datasource.
    ///
    /// @param transaction "Transaction" transfer object
    /// @return newly created transaction ID or a {@code -1} on error
    public int insertTransaction(Transaction transaction);

    /// Finds a transaction based on criteria.
    ///
    /// @param id transaction ID to search
    /// @return "Transaction" transfer object
    public Transaction findTransaction(int id);

    /// Updates an existing transaction in datasource.
    ///
    /// @param id          transaction ID to update
    /// @param transaction "Transaction" transfer object with updated fields
    /// @return number of affected rows or a {@code -1} on error
    public int updateTransaction(int id, Transaction transaction);

    /// Deletes an existing transaction in datasource.
    ///
    /// @param id transaction ID to be deleted
    /// @return number of affected rows or a {@code -1} on error
    public int deleteTransaction(int id);

    /// Gets a list of all transactions available in datasource.
    ///
    /// @return a list of "Transaction" transfer objects.
    public List<Transaction> findAllTransactions();
}
