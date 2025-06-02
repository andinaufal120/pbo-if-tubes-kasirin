package kasirin.data.dao;

import kasirin.data.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlProductDAO implements ProductDAO {

    public MySqlProductDAO() {}

    @Override
    public int insertProduct(Product product) {
        int result = -1;

        String query = "INSERT INTO products (store_id,name,category,base_price,description,image_url) VALUES (?,?,?,?,?,?)";
        // either use try-with-resources or finally block, so ur computer don't explode.
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1,product.getStoreID());
            stmt.setString(2,product.getName());
            stmt.setString(3,product.getCategory());
            stmt.setDouble(4,product.getBasePrice());
            stmt.setString(5,product.getDescription());
            stmt.setString(6,product.getImageURL());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return new product ID on success, return -1 on failure
    }

    @Override
    public Product findProduct(int id) {
        Product product = null;

        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1,id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    int productID = rs.getInt("id");
                    int storeID = rs.getInt("store_id");
                    String productName = rs.getString("name");
                    String productCategory = rs.getString("category");
                    double productBasePrice = rs.getDouble("base_price");
                    String productDescription = rs.getString("description");
                    String productImageURL = rs.getString("image_url");

                    product = new Product(productCategory,storeID,productName,productBasePrice,productDescription,productImageURL);
                    product.setId(productID);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return product;
    }

    @Override
    public int updateProduct(int id, Product product) {
        int result = -1;

        String query = "UPDATE products SET store_id=?,name=?,category=?,base_price=?,description=?,image_url=? WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,product.getStoreID());
            stmt.setString(2,product.getName());
            stmt.setString(3,product.getCategory());
            stmt.setDouble(4,product.getBasePrice());
            stmt.setString(5,product.getDescription());
            stmt.setString(6,product.getImageURL());
            stmt.setInt(7,id);
            result = stmt.executeUpdate(); // number of affected rows
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public int deleteProduct(int id) {
        int result = -1;

        String query = "DELETE FROM products WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            result = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public List<Product> findAllProducts() {
        List<Product> result = new ArrayList<>();

        String query = "SELECT * FROM products";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int productID = rs.getInt("id");
                int storeID = rs.getInt("store_id");
                String productName = rs.getString("name");
                String productCategory = rs.getString("category");
                double productBasePrice = rs.getDouble("base_price");
                String productDescription = rs.getString("description");
                String productImageURL = rs.getString("image_url");

                Product product = new Product(productCategory,storeID,productName,productBasePrice,productDescription,productImageURL);
                product.setId(productID);
                result.add(product);
            }
        }  catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
