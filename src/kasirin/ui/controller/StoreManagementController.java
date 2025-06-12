package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.util.NavigationUtil;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Store Management System
 * Handles navigation between different modules (Dashboard, Products, Transactions, Reports, Cashier)
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
     * Initialize with user and store data
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            // Update store information in sidebar
            storeNameLabel.setText(store.getName());
            storeTypeLabel.setText(store.getType() != null ? store.getType() : "General Store");

            // Load dashboard by default
            showDashboard();

            System.out.println("StoreManagementController initialized with store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show Dashboard view
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
     * Show Products management view
     */
    @FXML
    private void showProducts() {
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
     * Show Transactions/POS view
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
     * Show Reports view
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
     * Show Cashier management view
     */
    @FXML
    private void showCashier() {
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
     * Show Settings view
     */
    @FXML
    private void showSettings() {
        AlertUtil.showInfo("Settings", "Settings module will be available soon!");
    }

    /**
     * Go back to main dashboard
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

    /**
     * Get current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current store
     */
    public Store getCurrentStore() {
        return currentStore;
    }
}
