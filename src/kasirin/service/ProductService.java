package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.ProductDAO;
import kasirin.data.dao.StoreDAO;
import kasirin.data.model.Product;

import java.util.List;

/**
 * Service class that provides operation logic for products management
 * Decoupled from UI components to maintain separation of concerns
 *
 * @author yamaym
 */
public class ProductService {
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
    private final ProductDAO productDAO = daoFactory.getProductDAO();

    /**
     * Adds a new validated product to the datasource
     *
     * @param product a "Product" transfer object
     * @return The ID of the newly created product, or -1 if validation fails
     */
    public int addProduct(Product product) {
        if (validateProduct(product)) {
            return productDAO.insertProduct(product);
        }
        return -1;
    }

    /**
     * Updates an existing product with new validated properties
     *
     * @param id      the product id to be updated
     * @param product a "Product" transfer object
     * @return true if update was successful, false otherwise
     * @throws IllegalArgumentException if product ID doesn't exist or validation fails
     */
    public boolean updateProduct(int id, Product product) {
        if (validateProduct(product)) {
            int rowsAffected = productDAO.updateProduct(id, product);
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Product id does not exist");
            }
            return rowsAffected > 0;
        }
        return false;
    }

    /**
     * Deletes an existing product
     *
     * @param id the product id to be deleted
     * @return true if deletion was successful, false otherwise
     * @throws IllegalArgumentException if product ID doesn't exist
     */
    public boolean deleteProduct(int id) {
        int affectedRows = productDAO.deleteProduct(id);
        if (affectedRows == 0) {
            throw new IllegalArgumentException("Product id does not exist");
        }
        return affectedRows > 0;
    }

    /**
     * Gets an existing product
     *
     * @param id the product id to be found
     * @return a "Product" transfer object
     * @throws IllegalArgumentException if product ID doesn't exist
     */
    public Product getProduct(int id) {
        Product product = productDAO.findProduct(id);
        if (product == null) {
            throw new IllegalArgumentException("Product id does not exist");
        } else {
            return product;
        }
    }

    /**
     * Gets a list of products
     *
     * @return a list of "Product" transfer objects
     */
    public List<Product> getAllProducts() {
        return productDAO.findAllProducts();
    }

    /**
     * Validates each field of a given product, useful when trying to write to datasource
     *
     * @param product a "Product" transfer object
     * @return true when validation succeeds
     * @throws IllegalArgumentException if validation fails
     */
    public boolean validateProduct(Product product) {
        StoreDAO storeDAO = daoFactory.getStoreDAO();

        int storeID = product.getStoreID();
        String name = product.getName();
        String category = product.getCategory();
        double basePrice = product.getBasePrice();

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
