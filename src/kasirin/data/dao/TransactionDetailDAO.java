package kasirin.data.dao;

import kasirin.data.model.TransactionDetail;

/// Provides CRUD methods for "Transaction Detail" entity in datasource.
///
/// @author yamaym
public interface TransactionDetailDAO {
    /// Inserts a new transaction detail to datasource.
    ///
    /// @param transactionDetail "Transaction Detail" transfer object
    /// @return newly created transaction detail ID or a {@code -1} on error
    public int insertTransactionDetail(TransactionDetail transactionDetail);

    /// Finds a transaction detail based on criteria.
    ///
    /// @param id transaction detail ID to search
    /// @return "Transaction Detail" transfer object
    public int findTransactionDetail(int id);

    /// Updates an existing transaction detail in datasource.
    ///
    /// @param id                transaction detail ID to update
    /// @param transactionDetail "Transaction Detail" transfer object with updated fields
    /// @return {@code true} on success, {@code false} on error
    public int updateTransactionDetail(int id, TransactionDetail transactionDetail);

    /// Deletes an existing transaction detail in datasource.
    ///
    /// @param id transaction detail ID to be deleted
    /// @return {@code true} on success, {@code false} on error
    public int deleteTransactionDetail(int id);
}
