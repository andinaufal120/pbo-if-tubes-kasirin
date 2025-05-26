package kasirin.data.dao;

import kasirin.data.model.Product;

/// Provides CRUD methods for "Product" entity in datasource.
///
/// @author yamaym
public interface ProductDAO {
    /// Inserts a new product to datasource.
    ///
    /// @param product "Product" transfer object
    /// @return newly created product ID or a {@code -1} on error
    public int insertProduct(Product product);

    /// Finds a product based on criteria.
    ///
    /// @param id product ID to search
    /// @return "Product" transfer object
    public Product findProduct(int id);

    /// Updates an existing product in datasource.
    ///
    /// @param id      product ID to update
    /// @param product "Product" transfer object with updated fields
    /// @return number of affected rows or a {@code -1} on error
    public int updateProduct(int id, Product product);

    /// Deletes an existing product in datasource.
    ///
    /// @param id product ID to be deleted
    /// @return number of affected rows or a {@code -1} on error
    public int deleteProduct(int id);
}
