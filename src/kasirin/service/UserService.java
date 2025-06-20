package kasirin.service;

import java.util.List;
import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlUserDAO;
import kasirin.data.model.Role;
import kasirin.data.model.User;

/**
 * Service layer for handling user-related business logic
 * Decoupled from UI components to maintain separation of concerns
 *
 * @author yamaym
 */
public class UserService {

    private MySqlUserDAO userDAO;

    public UserService() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.userDAO = (MySqlUserDAO) factory.getUserDAO();
    }

    /**
     * Authenticates a user with the provided credentials
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return User object if authentication is successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        // Input validation
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        // Authenticate using DAO
        return userDAO.authenticateUser(username.trim(), password);
    }

    /**
     * Registers a new user with validation
     *
     * @param name User's full name
     * @param username Desired username
     * @param password Password
     * @param confirmPassword Password confirmation
     * @param role User role
     * @return The newly created User object
     * @throws IllegalArgumentException If validation fails
     * @throws RuntimeException If user creation fails
     */
    public User registerUser(String name, String username, String password,
                             String confirmPassword, Role role) throws Exception {
        // Validate input
        if (name == null || username == null || password == null ||
                confirmPassword == null || role == null) {
            throw new IllegalArgumentException("Semua field harus diisi!");
        }

        name = name.trim();
        username = username.trim();

        // Validate password match
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password dan konfirmasi password tidak sama!");
        }

        // Validate password length
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter!");
        }

        // Check if username already exists
        if (userDAO.isUsernameExists(username)) {
            throw new IllegalArgumentException("Username sudah digunakan! Pilih username lain.");
        }

        // Create new user
        User newUser = new User(name, username, password);
        newUser.setRole(role);

        // Save to database
        int userId = userDAO.insertUser(newUser);

        if (userId <= 0) {
            throw new RuntimeException("Gagal membuat akun. Silakan coba lagi.");
        }

        // Set the ID from the database
        newUser.setId(userId);

        return newUser;
    }

    /**
     * Gets all users from the database
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Finds a user by their ID
     *
     * @param id User ID to search for
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        return userDAO.findUser(id);
    }

    /**
     * Finds a user by their username
     *
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDAO.findUserByUsername(username.trim());
    }

    /**
     * Updates an existing user
     *
     * @param id User ID to update
     * @param user Updated User object
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(int id, User user) {
        if (user == null) {
            return false;
        }
        return userDAO.updateUser(id, user) > 0;
    }

    /**
     * Deletes a user by their ID
     *
     * @param id User ID to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id) > 0;
    }

    /**
     * Checks if a username already exists in the database
     *
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userDAO.isUsernameExists(username.trim());
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        if (userDAO != null) {
            userDAO.closeConnection();
        }
    }
}
