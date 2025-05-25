package kasirin.data.dao;

import kasirin.data.model.Product;

/// Provides CRUD methods for "Product" entity in datasource.
///
/// @author yamaym
public interface ProductDAO {
    public int insertProduct(Product product);
    public int findProduct(int id);
    public boolean updateProduct(int id, Product product);
    public boolean deleteProduct(int id);
}
