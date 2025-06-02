package kasirin.data.dao;

import kasirin.data.model.Transaction;

import java.util.List;

public class MySqlTransactionDAO implements TransactionDAO {
    @Override
    public int insertTransaction(Transaction transaction) {
        return 0;
    }

    @Override
    public Transaction findTransaction(int id) {
        return null;
    }

    @Override
    public int updateTransaction(int id, Transaction transaction) {
        return 0;
    }

    @Override
    public int deleteTransaction(int id) {
        return 0;
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return List.of();
    }
}
