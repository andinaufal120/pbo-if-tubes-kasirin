package kasirin.data.dao;

import kasirin.data.model.TransactionDetail;

import java.util.List;

public class MySqlTransactionDetailDAO implements TransactionDetailDAO {
    @Override
    public int insertTransactionDetail(TransactionDetail transactionDetail) {
        return 0;
    }

    @Override
    public TransactionDetail findTransactionDetail(int id) {
        return null;
    }

    @Override
    public int updateTransactionDetail(int id, TransactionDetail transactionDetail) {
        return 0;
    }

    @Override
    public int deleteTransactionDetail(int id) {
        return 0;
    }

    @Override
    public List<TransactionDetail> findAllTransactionDetails() {
        return List.of();
    }
}
