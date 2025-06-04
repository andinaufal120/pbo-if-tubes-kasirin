package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.ProductDAO;
import kasirin.data.dao.StoreDAO;
import kasirin.data.model.Product;

/// A service class that provides operation logics for products management in Kasirin application.
///
/// @author yamaym
public class ProductService {
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
    private final ProductDAO productDAO = daoFactory.getProductDAO();

    /// Adds a new validated product to the datasource.
    ///
    /// @param product a "Product" transfer object
    public void addProduct(Product product) {
        if (validateProduct(product)) {
            productDAO.insertProduct(product);
        }
    }

    /// Validates each fields of a given product, useful when trying to write in to datasource.
    /// <br><br>
    /// To pass the validation, the given Product object must have a:
    /// <ul>
    /// <li>Valid store id - which mean the given id must exist in the datasource</li>
    /// <li>Non-empty product name</li>
    /// <li>Non-empty category</li>
    /// <li>Non-negative base price</li>
    /// </ul>
    /// Otherwise, it'll throw an <code>IllegalArgumentException</code>.
    ///
    /// @param product a "Product" transfer object
    /// @return <code>true</code> when validation succeed
    public boolean validateProduct(Product product) {
        StoreDAO storeDAO = daoFactory.getStoreDAO();

        int storeID = product.getStoreID();
        String name = product.getName();
        String category = product.getCategory();
        double basePrice = product.getBasePrice();
        // description and image url could be optional maybe...

        if (storeDAO.findStore(storeID) == null) {
            throw new IllegalArgumentException("Store id does not exist");
        } else if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is empty");
        } else if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is empty");
        } else if (basePrice <= 0) {
            throw new IllegalArgumentException("Base price is negative");
        } else {
            return true;
        }
    }
}
