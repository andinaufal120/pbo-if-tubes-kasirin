package kasirin.service;

import java.util.List;
import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlUserDAO;
import kasirin.data.model.Role;
import kasirin.data.model.User;

/// Service layer untuk menangani logika bisnis terkait User
/// @author yamaym
public class UserService {

    private MySqlUserDAO userDAO;

    public UserService() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.userDAO = (MySqlUserDAO) factory.getUserDAO();
    }

    /// Autentikasi user dengan username dan password
    public User authenticateUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticateUser(username.trim(), password);
    }

    /// Registrasi user baru dengan validasi
    public boolean registerUser(String name, String username, String password, Role role) {
        // Validasi input
        if (name == null || username == null || password == null || role == null) {
            return false;
        }

        if (name.trim().isEmpty() || username.trim().isEmpty() || password.length() < 6) {
            return false;
        }

        // Cek apakah username sudah ada
        if (userDAO.isUsernameExists(username.trim())) {
            return false;
        }

        // Buat user baru
        User newUser = new User(name.trim(), username.trim(), password);
        newUser.setRole(role);

        return userDAO.insertUser(newUser) > 0;
    }

    /// Mendapatkan semua user
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /// Mencari user berdasarkan ID
    public User getUserById(int id) {
        return userDAO.findUser(id);
    }

    /// Mencari user berdasarkan username
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDAO.findUserByUsername(username.trim());
    }

    /// Update user
    public boolean updateUser(int id, User user) {
        if (user == null) {
            return false;
        }
        return userDAO.updateUser(id, user) > 0;
    }

    /// Hapus user
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id) > 0;
    }

    /// Cek apakah username sudah ada
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userDAO.isUsernameExists(username.trim());
    }

    /// Tutup koneksi database
    public void closeConnection() {
        if (userDAO != null) {
            userDAO.closeConnection();
        }
    }
}
