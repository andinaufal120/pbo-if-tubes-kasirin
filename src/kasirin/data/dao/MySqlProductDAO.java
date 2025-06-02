package kasirin.data.dao;

import kasirin.data.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlProductDAO implements ProductDAO {
    Connection conn;

    public MySqlProductDAO() {
        conn = MySqlDAOFactory.getConnection();
    }

    @Override
    public int insertProduct(Product product) {
        int result = -1;

        String query = "INSERT INTO products (store_id,name,category,base_price,description,image_url) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1,product.getStoreID());
            pstmt.setString(2,product.getName());
            pstmt.setString(3,product.getCategory());
            pstmt.setDouble(4,product.getBasePrice());
            pstmt.setString(5,product.getDescription());
            pstmt.setString(6,product.getImageURL());
            pstmt.executeUpdate();

            // Getting the new generated product ID
            try (ResultSet rs = pstmt.getGeneratedKeys()){
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in inserting product: " + e.getMessage());
        }
        return result; // return new product ID on success, return -1 on failure
    }

    @Override
    public Product findProduct(int id) {
        Product product = null;

        String query = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1,id);

            ResultSet rs = pstmt.executeQuery();
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
        } catch (Exception e) {
            System.out.println("Error in fetching product with id: " + e.getMessage());
        }
        return product;
    }

    @Override
    public int updateProduct(int id, Product product) {
        int result = -1;

        String query = "UPDATE products SET store_id=?,name=?,category=?,base_price=?,description=?,image_url=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1,product.getStoreID());
            pstmt.setString(2,product.getName());
            pstmt.setString(3,product.getCategory());
            pstmt.setDouble(4,product.getBasePrice());
            pstmt.setString(5,product.getDescription());
            pstmt.setString(6,product.getImageURL());
            pstmt.setInt(7,id);
            result = pstmt.executeUpdate(); // number of affected rows
        } catch (Exception e) {
            System.out.println("Error in updating product: " + e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public int deleteProduct(int id) {
        int result = -1;

        String query = "DELETE FROM products WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, id);
            result = pstmt.executeUpdate(); // number of affected rows
        } catch (Exception e) {
            System.out.println("Error in deleting product: " + e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public List<Product> findAllProducts() {
        List<Product> result = new ArrayList<>();

        String query = "SELECT * FROM products";
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();

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
        } catch (SQLException e) {
            System.out.println("Error in fetching all products: " + e.getMessage());
        }
        return result;
    }
}
