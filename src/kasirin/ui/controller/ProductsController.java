package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kasirin.data.model.Product;
import kasirin.data.model.ProductVariation;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.data.model.Role;
import kasirin.service.ProductService;
import kasirin.service.ProductVariationService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Enhanced Products Controller with Role-based Access Control
 *
 * @author yamaym
 */
public class ProductsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button addProductBtn;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, String> productCategoryCol;
    @FXML private TableColumn<Product, Double> productBasePriceCol;
    @FXML private TableColumn<Product, String> productStockCol;
    @FXML private TableColumn<Product, Void> productActionsCol;

    @FXML private VBox productDetailsContainer;
    @FXML private Label detailNameLabel;
    @FXML private Label detailCategoryLabel;
    @FXML private Label detailBasePriceLabel;
    @FXML private Label detailDescriptionLabel;

    @FXML private TableView<ProductVariation> variationsTable;
    @FXML private TableColumn<ProductVariation, String> variationTypeCol;
    @FXML private TableColumn<ProductVariation, String> variationValueCol;
    @FXML private TableColumn<ProductVariation, String> variationStockCol;
    @FXML private TableColumn<ProductVariation, String> variationPriceCol;
    @FXML private TableColumn<ProductVariation, String> variationActionsCol;

    @FXML private Button editProductBtn;
    @FXML private Button deleteProductBtn;

    private User currentUser;
    private Store currentStore;
    private ProductService productService;
    private ProductVariationService variationService;
    private ObservableList<Product> productList;
    private Product selectedProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            productService = new ProductService();
            variationService = new ProductVariationService();
            productList = FXCollections.observableArrayList();

            setupTables();
            setupFilters();

            System.out.println("ProductsController berhasil diinisialisasi");
        } catch (Exception e) {
            System.err.println("Error inisialisasi ProductsController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inisialisasi dengan data user dan store, dengan role-based access control
     *
     * @param user User yang sedang login
     * @param store Store yang sedang dikelola
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            // Check role-based access first
            if (!hasProductAccess()) {
                showAccessDenied();
                return;
            }

            loadProducts();
            setupRoleBasedPermissions();

            System.out.println("Produk dimuat untuk toko: " + store.getName() +
                    " oleh user: " + user.getName() + " (Role: " + user.getRole().getValue() + ")");
        } catch (Exception e) {
            System.err.println("Error inisialisasi produk dengan data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if current user has access to product management
     */
    private boolean hasProductAccess() {
        if (currentUser == null) return false;
        Role userRole = currentUser.getRole();
        return userRole == Role.ADMIN || userRole == Role.OWNER;
    }

    /**
     * Show access denied message for staff users
     */
    private void showAccessDenied() {
        // Disable all interactive elements
        addProductBtn.setDisable(true);
        searchBtn.setDisable(true);
        refreshBtn.setDisable(true);
        productsTable.setDisable(true);
        editProductBtn.setDisable(true);
        deleteProductBtn.setDisable(true);

        System.out.println("Access denied for user: " + currentUser.getName() +
                " (Role: " + currentUser.getRole().getValue() + ")");
    }

    /**
     * Setup role-based permissions for different user roles
     */
    private void setupRoleBasedPermissions() {
        Role userRole = currentUser.getRole();

        switch (userRole) {
            case STAFF:
                // Staff should not reach here due to hasProductAccess() check
                showAccessDenied();
                break;
            case ADMIN:
                setupAdminPermissions();
                break;
            case OWNER:
                setupOwnerPermissions();
                break;
            default:
                showAccessDenied();
                break;
        }
    }

    /**
     * Setup permissions for Admin role
     */
    private void setupAdminPermissions() {
        // Admin has full access to product management
        addProductBtn.setDisable(false);
        editProductBtn.setDisable(false);
        deleteProductBtn.setDisable(false);

        System.out.println("Admin permissions configured for product management");
    }

    /**
     * Setup permissions for Owner role
     */
    private void setupOwnerPermissions() {
        // Owner has complete access to everything
        addProductBtn.setDisable(false);
        editProductBtn.setDisable(false);
        deleteProductBtn.setDisable(false);

        System.out.println("Owner permissions configured for product management");
    }

    /**
     * Setup tabel produk dan variasi
     */
    private void setupTables() {
        // Setup kolom tabel produk
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        productBasePriceCol.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        // Format kolom harga dengan mata uang Rupiah
        productBasePriceCol.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", price));
                }
            }
        });

        // Kolom stok - hitung dari variasi produk
        productStockCol.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            int totalStock = variationService.getTotalStockForProduct(product.getId());
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(totalStock));
        });

        // Setup kolom aksi dengan role-based access
        productActionsCol.setCellFactory(col -> {
            TableCell<Product, Void> cell = new TableCell<Product, Void>() {
                private final Button editBtn = new Button("✏️");
                private final Button deleteBtn = new Button("🗑️");

                {
                    editBtn.getStyleClass().add("small-button");
                    deleteBtn.getStyleClass().add("danger-button");

                    editBtn.setOnAction(event -> {
                        if (!hasProductAccess()) {
                            AlertUtil.showWarning("Akses Ditolak", "Anda tidak memiliki izin untuk mengedit produk.");
                            return;
                        }
                        Product product = getTableView().getItems().get(getIndex());
                        openEditProductDialog(product);
                    });

                    deleteBtn.setOnAction(event -> {
                        if (!hasProductAccess()) {
                            AlertUtil.showWarning("Akses Ditolak", "Anda tidak memiliki izin untuk menghapus produk.");
                            return;
                        }
                        Product product = getTableView().getItems().get(getIndex());
                        handleDeleteProduct(product);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        // Only show action buttons if user has access
                        if (hasProductAccess()) {
                            javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                            buttons.getChildren().addAll(editBtn, deleteBtn);
                            setGraphic(buttons);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
            return cell;
        });

        productsTable.setItems(productList);

        // Handler untuk seleksi produk
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                showProductDetails(newSelection);
                loadProductVariations(newSelection);

                // Enable/disable buttons based on role
                if (hasProductAccess()) {
                    editProductBtn.setDisable(false);
                    deleteProductBtn.setDisable(false);
                }
            } else {
                selectedProduct = null;
                clearProductDetails();
                editProductBtn.setDisable(true);
                deleteProductBtn.setDisable(true);
            }
        });

        // Setup tabel variasi produk
        setupVariationsTable();
    }

    /**
     * Setup tabel variasi produk
     */
    private void setupVariationsTable() {
        variationTypeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Size"));
        variationValueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Large"));
        variationStockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("25"));
        variationPriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 5,000"));
        variationActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Edit"));
    }

    /**
     * Load variasi untuk produk yang dipilih
     */
    private void loadProductVariations(Product product) {
        try {
            List<ProductVariation> productVariations = variationService.getVariationsByProductId(product.getId());

            // Clear existing data
            variationsTable.getItems().clear();

            // Convert to observable list and add to table
            ObservableList<ProductVariation> variationsList = FXCollections.observableArrayList(productVariations);
            variationsTable.setItems(variationsList);

        } catch (Exception e) {
            System.err.println("Error loading product variations: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat variasi produk: " + e.getMessage());
        }
    }

    /**
     * Setup filter dan dropdown
     */
    private void setupFilters() {
        // Method ini dikosongkan karena categoryFilter tidak ada di FXML
    }

    /**
     * Load produk dari database
     */
    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();

            // Filter produk berdasarkan toko saat ini
            productList.clear();
            for (Product product : products) {
                if (product.getStoreID() == currentStore.getId()) {
                    productList.add(product);
                }
            }

            System.out.println("Dimuat " + productList.size() + " produk untuk toko: " + currentStore.getName());
        } catch (Exception e) {
            System.err.println("Error memuat produk: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat produk: " + e.getMessage());
        }
    }

    /**
     * Tampilkan detail produk di panel detail
     */
    private void showProductDetails(Product product) {
        detailNameLabel.setText(product.getName());
        detailCategoryLabel.setText(product.getCategory());
        detailBasePriceLabel.setText(String.format("Rp %,.0f", product.getBasePrice()));
        detailDescriptionLabel.setText(product.getDescription() != null ? product.getDescription() : "Tidak ada deskripsi");
    }

    /**
     * Bersihkan panel detail produk
     */
    private void clearProductDetails() {
        detailNameLabel.setText("-");
        detailCategoryLabel.setText("-");
        detailBasePriceLabel.setText("-");
        detailDescriptionLabel.setText("-");
    }

    /**
     * Buka dialog tambah produk - dengan role check
     */
    @FXML
    private void showAddProduct() {
        if (!hasProductAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk menambah produk.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/kasirin/ui/fxml/AddProductView.fxml");
            if (fxmlLocation == null) {
                AlertUtil.showError("Error", "File AddProductView.fxml tidak ditemukan");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            AddProductController controller = loader.getController();
            controller.initializeWithData(currentUser, currentStore, this);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addProductBtn.getScene().getWindow());
            stage.setTitle("Tambah Produk Baru");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            URL cssLocation = getClass().getResource("/kasirin/ui/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error membuka dialog tambah produk: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuka dialog tambah produk: " + e.getMessage());
        }
    }

    /**
     * Buka dialog edit produk - dengan role check
     */
    private void openEditProductDialog(Product product) {
        if (!hasProductAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk mengedit produk.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/kasirin/ui/fxml/EditProductView.fxml");
            if (fxmlLocation == null) {
                AlertUtil.showError("Error", "File EditProductView.fxml tidak ditemukan");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            EditProductController controller = loader.getController();
            controller.initializeWithData(currentUser, currentStore, product, this);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(editProductBtn.getScene().getWindow());
            stage.setTitle("Edit Produk");
            stage.setResizable(false);

            Scene scene = new Scene(root);
            URL cssLocation = getClass().getResource("/kasirin/ui/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error membuka dialog edit produk: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuka dialog edit produk: " + e.getMessage());
        }
    }

    /**
     * Handler untuk hapus produk - dengan role check
     */
    private void handleDeleteProduct(Product product) {
        if (!hasProductAccess()) {
            AlertUtil.showWarning("Akses Ditolak",
                    "Anda tidak memiliki izin untuk menghapus produk.\n" +
                            "Fitur ini hanya tersedia untuk Admin dan Owner.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Produk");
        alert.setHeaderText("Apakah Anda yakin ingin menghapus produk ini?");
        alert.setContentText("Produk: " + product.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = productService.deleteProduct(product.getId());
                    if (deleted) {
                        AlertUtil.showInfo("Berhasil", "Produk berhasil dihapus!");
                        refreshProducts();
                    } else {
                        AlertUtil.showError("Error", "Gagal menghapus produk.");
                    }
                } catch (Exception e) {
                    AlertUtil.showError("Error", "Gagal menghapus produk: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handler untuk pencarian produk
     */
    @FXML
    private void searchProducts() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        try {
            List<Product> allProducts = productService.getAllProducts();
            productList.clear();

            for (Product product : allProducts) {
                if (product.getStoreID() == currentStore.getId()) {
                    boolean matchesSearch = searchTerm.isEmpty() ||
                            product.getName().toLowerCase().contains(searchTerm) ||
                            product.getCategory().toLowerCase().contains(searchTerm);

                    if (matchesSearch) {
                        productList.add(product);
                    }
                }
            }

        } catch (Exception e) {
            AlertUtil.showError("Error", "Pencarian gagal: " + e.getMessage());
        }
    }

    /**
     * Handler untuk refresh produk
     */
    @FXML
    private void refreshProducts() {
        searchField.clear();
        loadProducts();
        clearProductDetails();
        selectedProduct = null;
        editProductBtn.setDisable(true);
        deleteProductBtn.setDisable(true);
    }

    /**
     * Handler untuk edit produk (dari tombol di panel detail)
     */
    @FXML
    private void editProduct() {
        if (selectedProduct != null) {
            openEditProductDialog(selectedProduct);
        }
    }

    /**
     * Handler untuk hapus produk (dari tombol di panel detail)
     */
    @FXML
    private void deleteProduct() {
        if (selectedProduct != null) {
            handleDeleteProduct(selectedProduct);
        }
    }

    /**
     * Refresh daftar produk (dipanggil dari dialog controller)
     */
    public void reloadProducts() {
        loadProducts();
    }
}
