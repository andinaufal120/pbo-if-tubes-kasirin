package kasirin.data.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/// Provides concrete implementation of DAO factory for MySQL database that can construct DAO object for every
/// database entities.
///
/// @author yamaym
public class MySqlDAOFactory extends DAOFactory {
    // Static fields
    public static final String DBURL = "jdbc:mysql://localhost:3306/db_kasir";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    // Static method

    /// Provides MySQL database connection to be used in entity DAO classes. Always CLOSE the connection after usage!
    ///
    /// @return MySQL connection object
    public static Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }

        return conn;
    }

    // DAO getter methods
    @Override
    public ProductDAO getProductDAO() {
        return new MySqlProductDAO();
    }

    @Override
    public ProductVariationDAO getProductVariationDAO() {
        return new MySqlProductVariationDAO();
    }

    @Override
    public StoreDAO getStoreDAO() {
        return new MySqlStoreDAO();
    }

    @Override
    public TransactionDAO getTransactionDAO() {
        return new MySqlTransactionDAO();
    }

    @Override
    public TransactionDetailDAO getTransactionDetailDAO() {
        return new MySqlTransactionDetailDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new MySqlUserDAO();
    }
}
