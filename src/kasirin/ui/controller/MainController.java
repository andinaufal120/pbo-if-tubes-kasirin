package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kasirin.data.model.Role;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.StoreService;
import kasirin.ui.util.NavigationUtil;
import kasirin.ui.util.AlertUtil;
import kasirin.ui.util.StoreCardUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Main View (Dashboard)
 * Displays user dashboard with menu options and store management
 *
 * @author yamaym
 */
public class MainController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;
    @FXML private Button logoutButton;
    @FXML private Button createStoreButton;
    @FXML private VBox storeListContainer;

    // Menu buttons
    @FXML private Button productButton;
    @FXML private Button transactionButton;
    @FXML private Button reportButton;
    @FXML private VBox userManagementButton;  // Changed to VBox
    @FXML private VBox storeManagementButton; // Changed to VBox
    @FXML private Button settingsButton;

    private User currentUser;
    private StoreService storeService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            storeService = new StoreService();
            System.out.println("MainController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing MainController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize the view with user data
     */
    public void initializeWithUser(User user) {
        try {
            this.currentUser = user;
            setupUserInterface();
            setupMenuVisibility();
            loadStores();
            System.out.println("MainController initialized with user: " + user.getName());
        } catch (Exception e) {
            System.err.println("Error initializing MainController with user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup user interface with user information
     */
    private void setupUserInterface() {
        welcomeLabel.setText("Selamat Datang di Kasirin!");

        String userInfo = String.format("Masuk sebagai: %s (%s) - Role: %s",
                currentUser.getName(),
                currentUser.getUsername(),
                currentUser.getRole().getValue().toUpperCase()
        );
        userInfoLabel.setText(userInfo);
    }

    /**
     * Setup menu visibility based on user role
     */
    private void setupMenuVisibility() {
        // Hide admin/owner only features for staff
        boolean isAdminOrOwner = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER;
        userManagementButton.setVisible(isAdminOrOwner);
        userManagementButton.setManaged(isAdminOrOwner);
        storeManagementButton.setVisible(isAdminOrOwner);
        storeManagementButton.setManaged(isAdminOrOwner);
    }

    /**
     * Load and display user's stores
     */
    private void loadStores() {
        try {
            List<Store> stores = storeService.getStoresByUserId(currentUser.getId());

            // Clear existing store cards
            storeListContainer.getChildren().clear();

            if (stores.isEmpty()) {
                Label emptyLabel = new Label("Anda belum memiliki toko. Klik tombol 'Buat Toko Baru' untuk membuat toko.");
                emptyLabel.getStyleClass().add("empty-message");
                storeListContainer.getChildren().add(emptyLabel);
            } else {
                // Add store cards
                for (Store store : stores) {
                    storeListContainer.getChildren().add(
                            StoreCardUtil.createStoreCard(store, this::handleManageStore)
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading stores: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat daftar toko: " + e.getMessage());
        }
    }

    /**
     * Handle create store button click
     */
    @FXML
    private void handleCreateStore() {
        try {
            Stage currentStage = (Stage) createStoreButton.getScene().getWindow();
            NavigationUtil.navigateToCreateStoreView(currentStage, currentUser, this);
        } catch (Exception e) {
            System.err.println("Error opening create store view: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuka form buat toko: " + e.getMessage());
        }
    }

    /**
     * Handle logout button click
     */
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText("Apakah Anda yakin ingin keluar?");
        alert.setContentText("Anda akan kembali ke halaman login.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                    NavigationUtil.navigateToLoginView(currentStage);
                } catch (Exception e) {
                    System.err.println("Error during logout: " + e.getMessage());
                    e.printStackTrace();
                    AlertUtil.showError("Error", "Gagal logout: " + e.getMessage());
                }
            }
        });
    }

    // Menu button handlers
    @FXML
    private void handleProductManagement() {
        AlertUtil.showInfo("Modul Produk", "Fitur manajemen produk akan segera tersedia!");
    }

    @FXML
    private void handleTransactionManagement() {
        AlertUtil.showInfo("Modul Transaksi", "Fitur kasir dan transaksi akan segera tersedia!");
    }

    @FXML
    private void handleReports() {
        AlertUtil.showInfo("Modul Laporan", "Fitur laporan penjualan akan segera tersedia!");
    }

    @FXML
    private void handleUserManagement() {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER) {
            AlertUtil.showInfo("Manajemen Pengguna", "Fitur manajemen pengguna akan segera tersedia!");
        } else {
            AlertUtil.showError("Akses Ditolak", "Anda tidak memiliki izin untuk mengakses fitur ini.");
        }
    }

    @FXML
    private void handleStoreManagement() {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER) {
            AlertUtil.showInfo("Manajemen Toko", "Fitur manajemen toko akan segera tersedia!");
        } else {
            AlertUtil.showError("Akses Ditolak", "Anda tidak memiliki izin untuk mengakses fitur ini.");
        }
    }

    @FXML
    private void handleSettings() {
        AlertUtil.showInfo("Pengaturan", "Fitur pengaturan akan segera tersedia!");
    }

    /**
     * Handle manage store action from store card
     */
    private void handleManageStore(Store store) {
        AlertUtil.showInfo("Kelola Toko: " + store.getName(), "Fitur kelola toko akan segera tersedia!");
    }

    /**
     * Refresh store list (called after creating new store)
     */
    public void refreshStoreList() {
        loadStores();
    }

    /**
     * Get current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
