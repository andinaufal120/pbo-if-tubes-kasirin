package kasirin.service;

import kasirin.data.dao.ProductDAO;
import kasirin.data.dao.MySqlProductDAO;
import kasirin.data.model.Product;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new MySqlProductDAO();
    }

    public int addProduct(Product product) {
        validateProduct(product);
        return productDAO.insertProduct(product);
    }

    public Product getProductById(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID Produk Tidak Valid.");
        return productDAO.findProduct(id);
    }

    public int updateProduct(int id, Product updateProduct) {
        if (id <= 0) throw new IllegalArgumentException("ID Produk Tidak Valid.");
        validateProduct(updateProduct);
        return productDAO.updateProduct(id, updateProduct);
    }

    public int deleteProduct(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID Produk Tidak Valid.");
        return productDAO.deleteProduct(id);
    }

    public void validateProduct(Product product) {
        if (product == null) throw new NullPointerException("Produk Tidak Boleh Null.");
        if (product.getName() == null || product.getName().isEmpty()) 
            throw new IllegalArgumentException("Nama Produk Tidak Boleh Kosong.");
        if (product.getBasePrice() < 0)
            throw new IllegalArgumentException("Harga Produk Tidak Boleh Negatif.");
        if (product.getStoreID() <= 0)
            throw new IllegalArgumentException("ID Toko Tidak Valid.");
    }
}
