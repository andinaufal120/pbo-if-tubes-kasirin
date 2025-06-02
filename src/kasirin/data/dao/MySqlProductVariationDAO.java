package kasirin.data.dao;

import kasirin.data.model.ProductVariation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MySqlProductVariationDAO implements ProductVariationDAO {
    @Override
    public int insertProductVariation(ProductVariation productVariation) {
        int result = -1;

        String query = "INSERT INTO products (store_id,name,category,base_price,description,image_url) VALUES (?,?,?,?,?,?)";
        // either use try-with-resources or finally block, so ur computer don't explode.
        try (Connection conn = MySqlDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, productVariation.getStoreID());
            stmt.setString(2, productVariation.getName());
            stmt.setString(3, productVariation.getCategory());
            stmt.setDouble(4, productVariation.getBasePrice());
            stmt.setString(5, productVariation.getDescription());
            stmt.setString(6, productVariation.getImageURL());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
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
    public ProductVariation findProductVariation(int id) {
        return null;
    }

    @Override
    public int updateProductVariation(int id, ProductVariation productVariation) {
        return 0;
    }

    @Override
    public int deleteProductVariation(int id) {
        return 0;
    }

    @Override
    public List<ProductVariation> findAllProductVariations() {
        return List.of();
    }
}
