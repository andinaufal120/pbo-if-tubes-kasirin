package kasirin.data.dao;

import kasirin.data.model.ProductVariation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlProductVariationDAO implements ProductVariationDAO {
    @Override
    public int insertProductVariation(ProductVariation productVariation) {
        int result = -1;

        String query = "INSERT INTO ProductsVariations (product_id, type, value, stocks, additional_price) VALUES (?,?,?,?,?)";
        // either use try-with-resources or finally block, so ur computer don't explode.
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, productVariation.getProductId());
            stmt.setString(2, productVariation.getType());
            stmt.setString(3, productVariation.getValue());
            stmt.setInt(4, productVariation.getStocks());
            stmt.setDouble(5, productVariation.getAdditionalPrice());
            stmt.executeUpdate();

            // gets newly created primary key
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return new product variation ID on success, return -1 on failure
    }

    @Override
    public ProductVariation findProductVariation(int id) {
        ProductVariation productVariation = null;

        String query = "SELECT * FROM Products WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int productVariationId = rs.getInt("id");
                    int productId = rs.getInt("product_id");
                    String type = rs.getString("type");
                    String value = rs.getString("value");
                    int stocks = rs.getInt("stocks");
                    double additionalPrice = rs.getDouble("additional_price");

                    productVariation = new ProductVariation(productId, type, value, additionalPrice, stocks);
                    productVariation.setId(productVariationId);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return productVariation;
    }

    @Override
    public int updateProductVariation(int id, ProductVariation productVariation) {
        int result = -1;

        String query = "UPDATE ProductsVariations SET product_id=?,type=?,value=?,stocks=?,additional_price=? WHERE id=?";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productVariation.getProductId());
            stmt.setString(2, productVariation.getType());
            stmt.setString(3, productVariation.getValue());
            stmt.setInt(4, productVariation.getStocks());
            stmt.setDouble(5, productVariation.getAdditionalPrice());
            stmt.setInt(6, id);
            result = stmt.executeUpdate(); // number of affected rows
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result; // return the number of affected rows on success, return -1 on failure
    }

    @Override
    public int deleteProductVariation(int id) {
        int result = -1;

        String query = "DELETE FROM ProductsVariations WHERE id=?";
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
    public List<ProductVariation> findAllProductVariations() {
        List<ProductVariation> result = new ArrayList<>();

        String query = "SELECT * FROM Products";
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int productVariationId = rs.getInt("id");
                int productId = rs.getInt("product_id");
                String type = rs.getString("type");
                String value = rs.getString("value");
                int stocks = rs.getInt("stocks");
                double additionalPrice = rs.getDouble("additional_price");

                ProductVariation productVariation = new ProductVariation(productId, type, value, additionalPrice, stocks);
                productVariation.setId(productVariationId);
                result.add(productVariation);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
