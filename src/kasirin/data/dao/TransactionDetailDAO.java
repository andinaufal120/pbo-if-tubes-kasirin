package kasirin.data.dao;

import kasirin.data.model.TransactionDetail;

/// Provides CRUD methods for "Transaction Detail" entity in datasource.
///
/// @author yamaym
public interface TransactionDetailDAO {
    public int insertTransactionDetail(TransactionDetail transactionDetail);
    public int findTransactionDetail(int id);
    public int updateTransactionDetail(int id, TransactionDetail transactionDetail);
    public int deleteTransactionDetail(int id);
}
