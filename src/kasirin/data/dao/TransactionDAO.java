package kasirin.data.dao;

import kasirin.data.model.Transaction;

/// Provides CRUD methods for "Transaction" entity in datasource.
///
/// @author yamaym
public interface TransactionDAO {
    public int insertTransaction(Transaction transaction);
    public int findTransaction(int id);
    public int updateTransaction(int id, Transaction transaction);
    public int deleteTransaction(int id);
}
