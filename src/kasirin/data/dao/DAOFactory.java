package kasirin.data.dao;

import kasirin.data.model.*;

/// Provides abstract DAO factory object that can construct one or more types of concrete DAO factories,
/// e.g. MySQL DAO factory.
///
/// @author yamaym
public abstract class DAOFactory {
    // List of available datasource option(s)
    public static final int MYSQL = 1;

    // Will return DAOs

    /// Provides DAO for "Product" entity from datasource.
    public abstract ProductDAO getProductDAO();

    /// Provides DAO for "Product Variation" entity from datasource.
    public abstract ProductVariationDAO getProductVariationDAO();

    /// Provides DAO for "Store" entity from datasource.
    public abstract StoreDAO getStoreDAO();

    /// Provides DAO for "Transaction" entity from datasource.
    public abstract TransactionDAO getTransactionDAO();

    /// Provides DAO for "Transaction Detail" entity from datasource.
    public abstract TransactionDetailDAO getTransactionDetailDAO();

    /// Provides DAO for "User" entity from datasource.
    public abstract UserDAO getUserDAO();

    // Will return specific DAO Factory
    public static DAOFactory getDAOFactory(int factoryType) {
        if (factoryType == MYSQL) {
            return new MySqlDAOFactory();
        } else {
            throw new IllegalArgumentException("Invalid DAO Factory");
        }

//        switch (factoryType) {
//            case MYSQL:
//                return new MySqlDAOFactory();
//            break;
//            case SQLITE:
//                return new SqliteDAOFactory();
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid DAO Factory");
//        }
    }
}
