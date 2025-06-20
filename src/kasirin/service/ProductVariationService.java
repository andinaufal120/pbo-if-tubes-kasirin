package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.ProductVariationDAO;
import kasirin.data.model.ProductVariation;

import java.util.List;

/**
 * Service class that provides operation logic for product variations management
 * Handles business logic for product variations CRUD operations
 *
 * @author yamaym
 */
public class ProductVariationService {
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
    private final ProductVariationDAO productVariationDAO = daoFactory.getProductVariationDAO();

    /**
     * Adds a new validated product variation to the datasource
     *
     * @param productVariation a "ProductVariation" transfer object
     * @return The ID of the newly created variation, or -1 if validation fails
     */
    public int addProductVariation(ProductVariation productVariation) {
        if (validateProductVariation(productVariation)) {
            return productVariationDAO.insertProductVariation(productVariation);
        }
        return -1;
    }

    /**
     * Updates an existing product variation with new validated properties
     *
     * @param id the variation id to be updated
     * @param productVariation a "ProductVariation" transfer object
     * @return true if update was successful, false otherwise
     * @throws IllegalArgumentException if variation ID doesn't exist or validation fails
     */
    public boolean updateProductVariation(int id, ProductVariation productVariation) {
        if (validateProductVariation(productVariation)) {
            int rowsAffected = productVariationDAO.updateProductVariation(id, productVariation);
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Product variation id does not exist");
            }
            return rowsAffected > 0;
        }
        return false;
    }

    /**
     * Deletes an existing product variation
     *
     * @param id the variation id to be deleted
     * @return true if deletion was successful, false otherwise
     * @throws IllegalArgumentException if variation ID doesn't exist
     */
    public boolean deleteProductVariation(int id) {
        int affectedRows = productVariationDAO.deleteProductVariation(id);
        if (affectedRows == 0) {
            throw new IllegalArgumentException("Product variation id does not exist");
        }
        return affectedRows > 0;
    }

    /**
     * Gets an existing product variation
     *
     * @param id the variation id to be found
     * @return a "ProductVariation" transfer object
     * @throws IllegalArgumentException if variation ID doesn't exist
     */
    public ProductVariation getProductVariation(int id) {
        ProductVariation variation = productVariationDAO.findProductVariation(id);
        if (variation == null) {
            throw new IllegalArgumentException("Product variation id does not exist");
        }
        return variation;
    }

    /**
     * Gets all variations for a specific product
     *
     * @param productId the product id
     * @return a list of "ProductVariation" transfer objects
     */
    public List<ProductVariation> getVariationsByProductId(int productId) {
        // Cast to MySqlProductVariationDAO to access additional methods
        if (productVariationDAO instanceof kasirin.data.dao.MySqlProductVariationDAO) {
            kasirin.data.dao.MySqlProductVariationDAO mysqlDAO =
                    (kasirin.data.dao.MySqlProductVariationDAO) productVariationDAO;
            return mysqlDAO.findVariationsByProductId(productId);
        }
        return List.of(); // Return empty list if cast fails
    }

    /**
     * Gets a list of all product variations
     *
     * @return a list of "ProductVariation" transfer objects
     */
    public List<ProductVariation> getAllProductVariations() {
        return productVariationDAO.findAllProductVariations();
    }

    /**
     * Updates stock for a specific variation
     *
     * @param variationId the variation id
     * @param newStock the new stock amount
     * @return true if update was successful
     */
    public boolean updateStock(int variationId, int newStock) {
        if (productVariationDAO instanceof kasirin.data.dao.MySqlProductVariationDAO) {
            kasirin.data.dao.MySqlProductVariationDAO mysqlDAO =
                    (kasirin.data.dao.MySqlProductVariationDAO) productVariationDAO;
            return mysqlDAO.updateStock(variationId, newStock) > 0;
        }
        return false;
    }

    /**
     * Reduces stock for a specific variation (used in transactions)
     *
     * @param variationId the variation id
     * @param quantity the quantity to reduce
     * @return true if reduction was successful
     */
    public boolean reduceStock(int variationId, int quantity) {
        if (productVariationDAO instanceof kasirin.data.dao.MySqlProductVariationDAO) {
            kasirin.data.dao.MySqlProductVariationDAO mysqlDAO =
                    (kasirin.data.dao.MySqlProductVariationDAO) productVariationDAO;
            return mysqlDAO.reduceStock(variationId, quantity) > 0;
        }
        return false;
    }

    /**
     * Validates each field of a given product variation
     *
     * @param productVariation a "ProductVariation" transfer object
     * @return true when validation succeeds
     * @throws IllegalArgumentException if validation fails
     */
    public boolean validateProductVariation(ProductVariation productVariation) {
        String type = productVariation.getType();
        String value = productVariation.getValue();
        int stocks = productVariation.getStocks();
        double additionalPrice = productVariation.getAdditionalPrice();

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Variation type is empty");
        } else if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Variation value is empty");
        } else if (stocks < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        } else if (additionalPrice < 0) {
            throw new IllegalArgumentException("Additional price cannot be negative");
        } else {
            return true;
        }
    }

    /**
     * Calculates total stock for a product across all variations
     *
     * @param productId the product id
     * @return total stock amount
     */
    public int getTotalStockForProduct(int productId) {
        List<ProductVariation> variations = getVariationsByProductId(productId);
        return variations.stream().mapToInt(ProductVariation::getStocks).sum();
    }

    /**
     * Deletes all variations for a specific product (used when deleting a product)
     *
     * @param productId the product id
     * @return number of variations deleted
     */
    public int deleteVariationsByProductId(int productId) {
        List<ProductVariation> variations = getVariationsByProductId(productId);
        int deletedCount = 0;

        for (ProductVariation variation : variations) {
            try {
                if (deleteProductVariation(variation.getId())) {
                    deletedCount++;
                }
            } catch (Exception e) {
                System.err.println("Error deleting variation " + variation.getId() + ": " + e.getMessage());
            }
        }

        return deletedCount;
    }
}
