package kasirin.data.dao;

import kasirin.data.model.Transaction;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySqlTransactionDAOTest {
    private static final MySqlTransactionDAO mySqlTransactionDAO = new MySqlTransactionDAO();
    private static int generatedTransactionId;

    /// Test if method insertTransaction() is able to insert a new transaction to <code>transaction</code> table in MySQL by not
    /// returning a <code>-1</code>.
    @Test
    @Order(1)
    void insertTransactionReturnTransactionId() {
        generatedTransactionId = mySqlTransactionDAO.insertTransaction(new Transaction(1, 1, null, 999999));
        assertNotEquals(-1, generatedTransactionId);
    }

    /// Test if method findTransaction() is able to find the just inserted transaction using the generated transaction id.
    @Test
    @Order(2)
    void findTransactionReturnNotNull() {
        assertNotNull(mySqlTransactionDAO.findTransaction(generatedTransactionId));
    }

    /// Test if method updateTransaction() is able to update a previously created transaction in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(3)
    void updateTransactionAffectARow() {
        assertEquals(1, mySqlTransactionDAO.updateTransaction(generatedTransactionId,
                new Transaction(1, 1, null, 888888)));
    }

    /// Test if method deleteTransaction() is able to delete a previously created transaction in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(4)
    void deleteTransactionAffectARow() {
        assertEquals(1, mySqlTransactionDAO.deleteTransaction(generatedTransactionId));
    }

    /// Test if method findAllTransactions() returns an ArrayList object.
    @Test
    @Order(5)
    void findAllTransactionsReturnArrayList() {
        assertEquals(ArrayList.class, mySqlTransactionDAO.findAllTransactions().getClass());
    }
}