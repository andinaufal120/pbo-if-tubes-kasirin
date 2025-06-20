package kasirin.data.dao;

import kasirin.data.model.ProductVariation;

import java.util.List;

/// Provides CRUD methods for "Product Variation" entity in datasource.
///
/// @author yamaym
public interface ProductVariationDAO {
    /// Inserts a new product variation to datasource.
    ///
    /// @param productVariation "Product Variation" transfer object
    /// @return newly created product variation ID or a {@code -1} on error
    public int insertProductVariation(ProductVariation productVariation);

    /// Finds a product variation based on criteria.
    ///
    /// @param id product variation ID to search
    /// @return "Product Variation" transfer object
    public ProductVariation findProductVariation(int id);

    /// Updates an existing product variation in datasource.
    ///
    /// @param id               product variation ID to update
    /// @param productVariation "Product Variation" transfer object with updated fields
    /// @return number of affected rows or a {@code -1} on error
    public int updateProductVariation(int id, ProductVariation productVariation);

    /// Deletes an existing product variation in datasource.
    ///
    /// @param id product variation ID to be deleted
    /// @return number of affected rows or a {@code -1} on error
    public int deleteProductVariation(int id);

    /// Gets a list of all product variations available in datasource.
    ///
    /// @return a list of "Product Variation" transfer objects
    public List<ProductVariation> findAllProductVariations();
}
