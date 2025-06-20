package kasirin.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.controller.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import kasirin.data.model.Product;

import java.io.IOException;
import java.net.URL;

/**
 * Enhanced Navigation Utility with comprehensive view navigation
 * @author yamaym
 */
public class NavigationUtil {

    /**
     * Navigate to login view
     */
    public static void navigateToLoginView(Stage currentStage) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/LoginView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find LoginView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Login");
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to register view
     */
    public static void navigateToRegisterView(Stage currentStage) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/RegisterView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find RegisterView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Registrasi");
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to main view with user data
     */
    public static void navigateToMainView(Stage currentStage, User user) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/MainView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find MainView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with user data
        MainController controller = loader.getController();
        controller.initializeWithUser(user);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Dashboard");
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to main view from Window
     */
    public static void navigateToMainView(Window window, User user) throws Exception {
        Stage stage = (Stage) window;
        navigateToMainView(stage, user);
    }

    /**
     * Navigate to store management view
     */
    public static void navigateToStoreManagementView(Stage currentStage, User user, Store store) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/StoreManagementView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find StoreManagementView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        StoreManagementController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - " + store.getName() + " Management");
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to dashboard view
     */
    public static void navigateToDashboardView(Window window, User user, Store store) throws Exception {
        Stage currentStage = (Stage) window;
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/DashboardView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find DashboardView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        DashboardController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Dashboard - " + store.getName());
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to products view
     */
    public static void navigateToProductsView(Window window, User user, Store store) throws Exception {
        Stage currentStage = (Stage) window;
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/ProductsView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find ProductsView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        ProductsController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Manajemen Produk - " + store.getName());
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to transactions view
     */
    public static void navigateToTransactionsView(Window window, User user, Store store) throws Exception {
        Stage currentStage = (Stage) window;
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/TransactionsView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find TransactionsView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        TransactionsController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Kasir - " + store.getName());
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to reports view
     */
    public static void navigateToReportsView(Window window, User user, Store store) throws Exception {
        Stage currentStage = (Stage) window;
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/ReportsView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find ReportsView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        ReportsController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Laporan - " + store.getName());
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Navigate to cashier management view
     */
    public static void navigateToCashierView(Window window, User user, Store store) throws Exception {
        Stage currentStage = (Stage) window;
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/CashierView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find CashierView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        CashierController controller = loader.getController();
        controller.initializeWithData(user, store);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");
        addStylesheet(scene, "/kasirin/ui/css/store-management-styles.css");

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Manajemen Kasir - " + store.getName());
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /**
     * Open create store view as modal dialog
     */
    public static void navigateToCreateStoreView(Stage parentStage, User user, MainController parentController) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/CreateStoreView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find CreateStoreView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        CreateStoreController controller = loader.getController();
        controller.initializeWithData(user, parentController);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Buat Toko Baru");
        modalStage.setResizable(false);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        modalStage.setScene(scene);
        modalStage.centerOnScreen();
        modalStage.showAndWait();
    }

    /**
     * Open add product view as modal dialog
     */
    public static void openAddProductModal(Stage parentStage, User user, Store store, ProductsController parentController) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/AddProductView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find AddProductView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        AddProductController controller = loader.getController();
        controller.initializeWithData(user, store, parentController);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Tambah Produk Baru");
        modalStage.setResizable(false);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        modalStage.setScene(scene);
        modalStage.centerOnScreen();
        modalStage.showAndWait();
    }

    /**
     * Open edit product view as modal dialog
     */
    public static void openEditProductModal(Stage parentStage, User user, Store store, Product product, ProductsController parentController) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/EditProductView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find EditProductView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        EditProductController controller = loader.getController();
        controller.initializeWithData(user, store, product, parentController);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Edit Produk");
        modalStage.setResizable(false);

        Scene scene = new Scene(root);
        addStylesheet(scene, "/kasirin/ui/css/styles.css");

        modalStage.setScene(scene);
        modalStage.centerOnScreen();
        modalStage.showAndWait();
    }

    /**
     * Helper method to add stylesheet to scene
     */
    private static void addStylesheet(Scene scene, String cssPath) {
        URL cssLocation = NavigationUtil.class.getResource(cssPath);
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        } else {
            System.err.println("Warning: Could not find CSS file: " + cssPath);
        }
    }

    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Show error dialog
     */
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information dialog
     */
    public static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show warning dialog
     */
    public static void showWarningDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
