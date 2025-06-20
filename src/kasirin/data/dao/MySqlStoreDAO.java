package kasirin.data.dao;

import kasirin.data.model.Store;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlStoreDAO implements StoreDAO {
    Connection conn;

    public MySqlStoreDAO() {
        conn = MySqlDAOFactory.getConnection();
    }

    @Override


    public int insertStore(Store store) {
        int result = -1;
        String query = "INSERT INTO Stores (name, type, address) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getType());
            pstmt.setString(3, store.getAddress());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting store: " + e.getMessage());
        }
        return result;
    }



    @Override
    public Store findStore(int id) {
        Store store = null;
        String query = "SELECT * FROM Stores WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                store = createStoreFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding store: " + e.getMessage());
        }
        return store;
    }

    @Override
    public int updateStore(int id, Store store) {
        int result = -1;
        String query = "UPDATE Stores SET name=?, type=?, address=? WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getType());
            pstmt.setString(3, store.getAddress());
            pstmt.setInt(4, id);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating store: " + e.getMessage());
        }
        return result;
    }

    @Override
    public int deleteStore(int id) {
        int result = -1;
        String query = "DELETE FROM Stores WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting store: " + e.getMessage());
        }
        return result;
    }

    public List<Store> getAllStores() {
        List<Store> stores = new ArrayList<>();
        String query = "SELECT * FROM Stores";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                stores.add(createStoreFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all stores: " + e.getMessage());
        }
        return stores;
    }

    public List<Store> getStoresByUserId(int userId) {
        List<Store> stores = new ArrayList<>();
        String query = "SELECT s.* FROM Stores s " +
                "JOIN user_store_access usa ON s.id = usa.store_id " +
                "WHERE usa.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                stores.add(createStoreFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting stores by user ID: " + e.getMessage());
        }
        return stores;
    }

    public boolean linkUserToStore(int userId, int storeId) {
        String query = "INSERT INTO user_store_access (user_id, store_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error linking user to store: " + e.getMessage());
            return false;
        }
    }

    private Store createStoreFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        String address = rs.getString("address");

        Store store = new Store(name, type, address);
        store.setId(id);
        return store;
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }


    @Override
    public List<Store> findAllStores() {
        return List.of();
    }
}
