package kasirin.data.dao;

import kasirin.data.model.ProductVariation;

/// Provides CRUD methods for "Product Variation" entity in datasource.
///
/// @author yamaym
public interface ProductVariationDAO {
    public int insertProductVariation(ProductVariation productVariation);
    public int findProductVariation(int id);
    public int updateProductVariation(int id, ProductVariation productVariation);
    public int deleteProductVariation(int id);
}
