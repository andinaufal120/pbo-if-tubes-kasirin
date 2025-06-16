package kasirin.data.dao;

import com.mysql.cj.jdbc.ConnectionImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDAOFactoryTest {
    /// Test if the static method getConnection() is not returning <code>null</code>.
    @Test
    void getConnectionNotNull() {
        assertNotNull(MySqlDAOFactory.getConnection());
    }

    /// Test if the static method getConnection() returns a <code>ConnectionImpl</code> object.
    ///
    /// <p><strong>Note:</strong> to successfully get <code>ConnectionImpl</code> object, the JDBC must be able to
    /// connect to the MySQL database.</p>
    @Test
    void getConnectionReturnsConnectionImpl() {
        assertEquals(ConnectionImpl.class, MySqlDAOFactory.getConnection().getClass());
    }

    /// Test if the method getProductDAO() returns a <code>MySqlProductDAO</code> object.
    @Test
    void getProductDAOReturnsProductDAO() {
        assertEquals(MySqlProductDAO.class, new MySqlDAOFactory().getProductDAO().getClass());
    }

    /// Test if the method getProductVariationDAO() returns a <code>MySqlProductVariationDAO</code> object.
    @Test
    void getProductVariationDAOReturnsProductVariationDAO() {
        assertEquals(MySqlProductVariationDAO.class, new MySqlDAOFactory().getProductVariationDAO().getClass());
    }

    /// Test if the method getStoreDAO() returns a <code>MySqlStoreDAO</code> object.
    @Test
    void getStoreDAOReturnsStoreDAO() {
        assertEquals(MySqlStoreDAO.class, new MySqlDAOFactory().getStoreDAO().getClass());
    }

    /// Test if the method getTransaction() is able to return a <code>MySqlTransaction</code> object.
    @Test
    void getTransactionDAOReturnsTransactionDAO() {
        assertEquals(MySqlTransactionDAO.class, new MySqlDAOFactory().getTransactionDAO().getClass());
    }

    /// Test if the method getTransactionDetail() is able to return a <code>MySqlTransactionDetail</code> object.
    @Test
    void getTransactionDetailDAOReturnsTransactionDetailDAO() {
        assertEquals(MySqlTransactionDetailDAO.class, new MySqlDAOFactory().getTransactionDetailDAO().getClass());
    }

    /// Test if the method getUserDAO() is able to return a <code>MySqlUserDAO</code> object.
    @Test
    void getUserDAOReturnsUserDAO() {
        assertEquals(MySqlUserDAO.class, new MySqlDAOFactory().getUserDAO().getClass());
    }
}