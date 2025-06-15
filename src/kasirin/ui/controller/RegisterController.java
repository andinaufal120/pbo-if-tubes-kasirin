package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kasirin.data.model.Role;
import kasirin.data.model.User;
import kasirin.service.UserService;
import kasirin.ui.util.NavigationUtil;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Register View
 * Handles new user registration
 *
 * @author yamaym
 */
public class RegisterController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Label titleLabel;

    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserService();
        setupRoleComboBox();
    }

    /**
     * Setup role combo box with available roles
     */
    private void setupRoleComboBox() {
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(Role.STAFF); // Default selection
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        Role role = roleComboBox.getValue();

        try {
            // Use service to perform registration with validation
            User newUser = userService.registerUser(name, username, password, confirmPassword, role);

            // Show success message
            AlertUtil.showInfo("Registrasi Berhasil",
                    "Akun berhasil dibuat! Silakan login dengan akun baru Anda.");

            // Clear form
            clearForm();

            // Navigate back to login
            handleBack();

        } catch (IllegalArgumentException e) {
            // Handle validation errors
            AlertUtil.showError("Registrasi Gagal", e.getMessage());
            handleRegistrationError(e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Handle specific registration errors by focusing appropriate fields
     */
    private void handleRegistrationError(String message) {
        if (message.contains("Username sudah digunakan")) {
            usernameField.requestFocus();
        } else if (message.contains("Password dan konfirmasi")) {
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
        } else if (message.contains("Password minimal")) {
            passwordField.requestFocus();
        }
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        try {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            NavigationUtil.navigateToLoginView(currentStage);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Gagal kembali ke halaman login: " + e.getMessage());
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(Role.STAFF);
    }
}
