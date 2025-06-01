package kasirin.data.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import kasirin.data.model.Role;
import kasirin.data.model.User;

public class MySqlUserDAO implements UserDAO {

    Connection conn;

    public MySqlUserDAO() {
        conn = MySqlDAOFactory.getConnection();
    }

    @Override
    public int insertUser(User user) {
        int result = -1;
        String query = "INSERT INTO Users (name, username, password, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().getValue());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
        return result;
    }

    @Override
    public User findUser(int id) {
        User user = null;
        String query = "SELECT * FROM Users WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by ID: " + e.getMessage());
        }
        return user;
    }

    @Override
    public int updateUser(int id, User user) {
        int result = -1;
        String query = "UPDATE Users SET name=?, username=?, password=?, role=? WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().getValue());
            pstmt.setInt(6, id);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
        return result;
    }

    @Override
    public int deleteUser(int id) {
        int result = -1;
        String query = "DELETE FROM Users WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return result;
    }

    public User authenticateUser(String username, String password) {
        User user = null;
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
        }
        return user;
    }

    public User findUserByUsername(String username) {
        User user = null;
        String query = "SELECT * FROM Users WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by username: " + e.getMessage());
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("id");
        String name = rs.getString("name");
        String username = rs.getString("username");
        String password = rs.getString("password");
        Role role = Role.fromString(rs.getString("role"));

        User user = new User(name, username, password);
        user.setId(userId);
        user.setRole(role);
        return user;
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
}
