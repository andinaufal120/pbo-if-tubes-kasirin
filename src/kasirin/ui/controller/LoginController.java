package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kasirin.data.model.User;
import kasirin.service.UserService;
import kasirin.ui.util.NavigationUtil;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Login View
 * Handles user authentication and navigation to main application
 *
 * @author yamaym
 */
public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userService = new UserService();
            setupEventHandlers();
            System.out.println("LoginController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing LoginController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup event handlers for UI components
     */
    private void setupEventHandlers() {
        // Handle Enter key press in password field
        passwordField.setOnAction(event -> handleLogin());

        // Handle Enter key press in username field
        usernameField.setOnAction(event -> passwordField.requestFocus());
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Login attempt for username: " + username);

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showError("Error", "Username dan password tidak boleh kosong!");
            return;
        }

        try {
            // Authenticate user using service layer
            User user = userService.authenticateUser(username, password);

            if (user == null) {
                System.out.println("Authentication failed for username: " + username);
                AlertUtil.showError("Login Gagal", "Username atau password salah!");
                passwordField.clear();
                return;
            }

            System.out.println("Authentication successful for user: " + user.getName());

            // Show success message
            AlertUtil.showInfo("Login Berhasil", "Selamat datang, " + user.getName() + "!");

            // Navigate to main view
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            System.out.println("Navigating to main view...");
            NavigationUtil.navigateToMainView(currentStage, user);

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Terjadi kesalahan saat login: " + e.getMessage());
        }
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        try {
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            NavigationUtil.navigateToRegisterView(currentStage);
        } catch (Exception e) {
            System.err.println("Error navigating to register: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuka form registrasi: " + e.getMessage());
        }
    }
}
