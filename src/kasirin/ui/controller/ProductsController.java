package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Products Management View
 * Handles CRUD operations for products and variations
 *
 * @author yamaym
 */
public class ProductsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button addProductBtn;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;

    @FXML private TableView<Object> productsTable;
    @FXML private TableColumn<Object, String> productIdCol;
    @FXML private TableColumn<Object, String> productNameCol;
    @FXML private TableColumn<Object, String> productCategoryCol;
    @FXML private TableColumn<Object, String> productBasePriceCol;
    @FXML private TableColumn<Object, String> productStockCol;
    @FXML private TableColumn<Object, String> productActionsCol;

    @FXML private VBox productDetailsContainer;
    @FXML private Label detailNameLabel;
    @FXML private Label detailCategoryLabel;
    @FXML private Label detailBasePriceLabel;
    @FXML private Label detailDescriptionLabel;

    @FXML private TableView<Object> variationsTable;
    @FXML private TableColumn<Object, String> variationTypeCol;
    @FXML private TableColumn<Object, String> variationValueCol;
    @FXML private TableColumn<Object, String> variationStockCol;
    @FXML private TableColumn<Object, String> variationPriceCol;
    @FXML private TableColumn<Object, String> variationActionsCol;

    @FXML private Button addVariationBtn;
    @FXML private Button editProductBtn;
    @FXML private Button deleteProductBtn;

    private User currentUser;
    private Store currentStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTables();
            setupFilters();

            System.out.println("ProductsController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing ProductsController: " + e.getMessage());
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

            loadProducts();

            System.out.println("Products loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing products with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        // Setup products table columns
        productIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("1"));
        productNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Sample Product"));
        productCategoryCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Food"));
        productBasePriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 25,000"));
        productStockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("50"));
        productActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Edit | Delete"));

        // Setup variations table columns
        variationTypeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Size"));
        variationValueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Large"));
        variationStockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("25"));
        variationPriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 5,000"));
        variationActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Edit"));
    }

    /**
     * Setup filters
     */
    private void setupFilters() {
        categoryFilter.getItems().addAll("All Categories", "Food", "Beverage", "Snacks", "Others");
        categoryFilter.setValue("All Categories");
    }

    /**
     * Load products from service
     */
    private void loadProducts() {
        try {
            // TODO: Replace with actual service call
            // productService.getProductsByStore(currentStore.getId());

            System.out.println("Products loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showAddProduct() {
        AlertUtil.showInfo("Add Product", "Add product dialog will be implemented soon!");
    }

    @FXML
    private void searchProducts() {
        String searchTerm = searchField.getText().trim();
        String category = categoryFilter.getValue();

        // TODO: Implement search functionality
        AlertUtil.showInfo("Search", "Searching for: " + searchTerm + " in category: " + category);
    }

    @FXML
    private void refreshProducts() {
        loadProducts();
        AlertUtil.showInfo("Refresh", "Products refreshed successfully!");
    }

    @FXML
    private void showAddVariation() {
        AlertUtil.showInfo("Add Variation", "Add variation dialog will be implemented soon!");
    }

    @FXML
    private void editProduct() {
        AlertUtil.showInfo("Edit Product", "Edit product dialog will be implemented soon!");
    }

    @FXML
    private void deleteProduct() {
        AlertUtil.showInfo("Delete Product", "Delete product functionality will be implemented soon!");
    }
}
