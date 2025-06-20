package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kasirin.data.model.Role;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.util.NavigationUtil;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Enhanced Store Management Controller with Role-based Access Control
 * Fixed method names to match FXML onAction references
 *
 * @author yamaym
 */
public class StoreManagementController implements Initializable {

    @FXML private Label storeNameLabel;
    @FXML private Label storeTypeLabel;
    @FXML private StackPane contentArea;

    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button productsBtn;
    @FXML private Button transactionsBtn;
    @FXML private Button reportsBtn;
    @FXML private Button cashierBtn;
    @FXML private Button settingsBtn;
    @FXML private Button backBtn;

    private User currentUser;
    private Store currentStore;
    private Button activeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Set dashboard as default active button
            activeButton = dashboardBtn;
            updateActiveButton(dashboardBtn);

            System.out.println("StoreManagementController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing StoreManagementController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with user and store data, applying role-based access control
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            setupStoreInformation();
            setupRoleBasedAccess();

            // Load dashboard by default
            showDashboard();

            System.out.println("Store management initialized for user: " + user.getName() +
                    " (Role: " + user.getRole().getValue() + ") at store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing store management with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup store information display
     */
    private void setupStoreInformation() {
        storeNameLabel.setText(currentStore.getName());
        storeTypeLabel.setText(currentStore.getType() != null ? currentStore.getType() : "General Store");
    }

    /**
     * Setup role-based access control for menu items
     */
    private void setupRoleBasedAccess() {
        Role userRole = currentUser.getRole();

        System.out.println("Setting up role-based access for role: " + userRole.getValue());

        switch (userRole) {
            case STAFF:
                setupStaffAccess();
                break;
            case ADMIN:
                setupAdminAccess();
                break;
            case OWNER:
                setupOwnerAccess();
                break;
            default:
                setupDefaultAccess();
                break;
        }
    }

    /**
     * Setup access for STAFF role - Limited access
     */
    private void setupStaffAccess() {
        System.out.println("Configuring STAFF access - Limited permissions");

        // Staff can access
        dashboardBtn.setDisable(false);
        transactionsBtn.setDisable(false);
        reportsBtn.setDisable(false);

        // Staff CANNOT access
        productsBtn.setDisable(true);
        cashierBtn.setDisable(true);
        settingsBtn.setDisable(true);

        // Update button tooltips for disabled features
        productsBtn.setTooltip(new Tooltip("Akses ditolak - Hanya Admin/Owner yang dapat mengelola produk"));
        cashierBtn.setTooltip(new Tooltip("Akses ditolak - Hanya Admin/Owner yang dapat mengelola kasir"));
        settingsBtn.setTooltip(new Tooltip("Akses ditolak - Hanya Admin/Owner yang dapat mengakses pengaturan"));

        // Update button text to show restrictions
        productsBtn.setText("🔒 Produk");
        cashierBtn.setText("🔒 Manajemen Kasir");
        settingsBtn.setText("🔒 Pengaturan");

        System.out.println("STAFF access configured - Products and Cashier management disabled");
    }

    /**
     * Setup access for ADMIN role - Full access except owner-specific features
     */
    private void setupAdminAccess() {
        System.out.println("Configuring ADMIN access - Full permissions");

        // Admin can access everything
        dashboardBtn.setDisable(false);
        productsBtn.setDisable(false);
        transactionsBtn.setDisable(false);
        reportsBtn.setDisable(false);
        cashierBtn.setDisable(false);
        settingsBtn.setDisable(false);

        System.out.println("ADMIN access configured - Full access granted");
    }

    /**
     * Setup access for OWNER role - Complete access
     */
    private void setupOwnerAccess() {
        System.out.println("Configuring OWNER access - Complete permissions");

        // Owner can access everything
        dashboardBtn.setDisable(false);
        productsBtn.setDisable(false);
        transactionsBtn.setDisable(false);
        reportsBtn.setDisable(false);
        cashierBtn.setDisable(false);
        settingsBtn.setDisable(false);

        System.out.println("OWNER access configured - Complete access granted");
    }

    /**
     * Setup default access (fallback)
     */
    private void setupDefaultAccess() {
        System.out.println("Configuring DEFAULT access - Minimal permissions");

        // Default minimal access
        dashboardBtn.setDisable(false);
        transactionsBtn.setDisable(false);

        // Disable other features
        productsBtn.setDisable(true);
        reportsBtn.setDisable(true);
        cashierBtn.setDisable(true);
        settingsBtn.setDisable(true);
    }

    /**
     * Show Dashboard view - Method name matches FXML onAction
     */
    @FXML
    private void showDashboard() {
        try {
            loadView("/kasirin/ui/fxml/DashboardView.fxml", "Dashboard");
            updateActiveButton(dashboardBtn);
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    /**
     * Show Products management view - Method name matches FXML onAction
     */
    @FXML
    private void showProducts() {
        if (!hasProductAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk mengakses manajemen produk.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        try {
            loadView("/kasirin/ui/fxml/ProductsView.fxml", "Products");
            updateActiveButton(productsBtn);
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load products: " + e.getMessage());
        }
    }

    /**
     * Show Transactions view - Method name matches FXML onAction
     */
    @FXML
    private void showTransactions() {
        try {
            loadView("/kasirin/ui/fxml/TransactionsView.fxml", "Transactions");
            updateActiveButton(transactionsBtn);
        } catch (Exception e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load transactions: " + e.getMessage());
        }
    }

    /**
     * Show Reports view - Method name matches FXML onAction
     */
    @FXML
    private void showReports() {
        try {
            loadView("/kasirin/ui/fxml/ReportsView.fxml", "Reports");
            updateActiveButton(reportsBtn);
        } catch (Exception e) {
            System.err.println("Error loading reports: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load reports: " + e.getMessage());
        }
    }

    /**
     * Show Cashier management view - Method name matches FXML onAction
     */
    @FXML
    private void showCashier() {
        if (!hasCashierManagementAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk mengakses manajemen kasir.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        try {
            loadView("/kasirin/ui/fxml/CashierView.fxml", "Cashier");
            updateActiveButton(cashierBtn);
        } catch (Exception e) {
            System.err.println("Error loading cashier: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to load cashier management: " + e.getMessage());
        }
    }

    /**
     * Show Settings view - Method name matches FXML onAction
     */
    @FXML
    private void showSettings() {
        if (!hasSettingsAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk mengakses pengaturan.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        AlertUtil.showInfo("Settings", "Settings module will be available soon!");
    }

    /**
     * Go back to main dashboard - Method name matches FXML onAction
     */
    @FXML
    private void backToMain() {
        try {
            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            NavigationUtil.navigateToMainView(currentStage, currentUser);
        } catch (Exception e) {
            System.err.println("Error navigating back to main: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Failed to navigate back: " + e.getMessage());
        }
    }

    /**
     * Load a view into the content area
     */
    private void loadView(String fxmlPath, String viewName) throws Exception {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            throw new Exception("Cannot find FXML file: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent view = loader.load();

        // Initialize the controller with current data if it has the method
        Object controller = loader.getController();
        if (controller instanceof DashboardController) {
            ((DashboardController) controller).initializeWithData(currentUser, currentStore);
        } else if (controller instanceof ProductsController) {
            ((ProductsController) controller).initializeWithData(currentUser, currentStore);
        } else if (controller instanceof TransactionsController) {
            ((TransactionsController) controller).initializeWithData(currentUser, currentStore);
        } else if (controller instanceof ReportsController) {
            ((ReportsController) controller).initializeWithData(currentUser, currentStore);
        } else if (controller instanceof CashierController) {
            ((CashierController) controller).initializeWithData(currentUser, currentStore);
        }

        // Clear and add the new view
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);

        System.out.println("Loaded view: " + viewName);
    }

    /**
     * Update the active button styling
     */
    private void updateActiveButton(Button newActiveButton) {
        // Remove active class from previous button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        // Add active class to new button
        newActiveButton.getStyleClass().add("active");
        activeButton = newActiveButton;
    }

    // Role checking methods

    /**
     * Check if user has access to product management
     */
    private boolean hasProductAccess() {
        Role userRole = currentUser.getRole();
        return userRole == Role.ADMIN || userRole == Role.OWNER;
    }

    /**
     * Check if user has access to cashier management
     */
    private boolean hasCashierManagementAccess() {
        Role userRole = currentUser.getRole();
        return userRole == Role.ADMIN || userRole == Role.OWNER;
    }

    /**
     * Check if user has access to settings
     */
    private boolean hasSettingsAccess() {
        Role userRole = currentUser.getRole();
        return userRole == Role.ADMIN || userRole == Role.OWNER;
    }

    /**
     * Check if user has access to reports
     */
    private boolean hasReportsAccess() {
        // All roles can access reports, but with different levels of detail
        return true;
    }

    /**
     * Get current user for other controllers
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current store for other controllers
     */
    public Store getCurrentStore() {
        return currentStore;
    }
}
