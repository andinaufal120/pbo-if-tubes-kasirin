package kasirin.data.dao;

import kasirin.data.model.Product;

public class MySqlProductDAO implements ProductDAO {
    @Override
    public int insertProduct(Product product) {
        return 0;
    }

    @Override
    public Product findProduct(int id) {
        return null;
    }

    @Override
    public boolean updateProduct(int id, Product product) {
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        return false;
    }
}
