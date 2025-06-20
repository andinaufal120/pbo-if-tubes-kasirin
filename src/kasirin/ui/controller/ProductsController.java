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
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.ProductService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Manajemen Produk
 * Menangani CRUD operasi untuk produk dan variasinya
 *
 * @author yamaym
 */
public class ProductsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
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
    private ProductService productService;
    private ObservableList<Product> productList;
    private Product selectedProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            productService = new ProductService();
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
     * Inisialisasi dengan data user dan store
     *
     * @param user User yang sedang login
     * @param store Store yang sedang dikelola
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            loadProducts();

            // TODO: Tim dapat menambahkan logika berdasarkan role user
            // Contoh: staff hanya bisa melihat produk yang mereka buat
            if (user.getRole().getValue().equals("staff")) {
                // Implementasi pembatasan untuk staff
                setupStaffPermissions();
            }

            System.out.println("Produk dimuat untuk toko: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error inisialisasi produk dengan data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup permissions khusus untuk staff
     * TODO: Tim implementasikan pembatasan akses untuk staff
     */
    private void setupStaffPermissions() {
        // TODO: Implementasi pembatasan untuk staff
        // Contoh:
        // - Hanya bisa edit produk yang mereka buat
        // - Tidak bisa hapus produk
        // - Batasi kategori yang bisa dibuat
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

        // Kolom stok (placeholder - TODO: Tim implementasikan dari ProductVariations)
        productStockCol.setCellValueFactory(cellData -> {
            // TODO: Tim implementasikan perhitungan total stok dari variasi produk
            // Query: SELECT SUM(stocks) FROM ProductsVariations WHERE product_id = ?
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Setup kolom aksi dengan tombol edit dan hapus
        productActionsCol.setCellFactory(col -> {
            TableCell<Product, Void> cell = new TableCell<Product, Void>() {
                private final Button editBtn = new Button("✏️");
                private final Button deleteBtn = new Button("🗑️");

                {
                    editBtn.getStyleClass().add("small-button");
                    deleteBtn.getStyleClass().add("danger-button");

                    editBtn.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        openEditProductDialog(product);
                    });

                    deleteBtn.setOnAction(event -> {
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
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editBtn, deleteBtn);
                        setGraphic(buttons);
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
                editProductBtn.setDisable(false);
                deleteProductBtn.setDisable(false);
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
     * TODO: Tim implementasikan loading data variasi dari database
     */
    private void setupVariationsTable() {
        // TODO: Tim implementasikan kolom variasi dengan data real dari ProductsVariations
        variationTypeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Size"));
        variationValueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Large"));
        variationStockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("25"));
        variationPriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 5,000"));
        variationActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Edit"));

        // TODO: Tim implementasikan loading variasi produk
        // Query: SELECT * FROM ProductsVariations WHERE product_id = ?
    }

    /**
     * Load variasi untuk produk yang dipilih
     * TODO: Tim implementasikan loading dari database
     *
     * @param product Produk yang dipilih
     */
    private void loadProductVariations(Product product) {
        // TODO: Tim implementasikan loading variasi produk dari database
        // Gunakan ProductVariationService untuk mengambil data
        // Query: SELECT * FROM ProductsVariations WHERE product_id = ?
    }

    /**
     * Setup filter dan dropdown
     */
    private void setupFilters() {
        // TODO: Tim dapat mengambil kategori dari database untuk dropdown
        // Query: SELECT DISTINCT category FROM Products WHERE store_id = ?
        categoryFilter.getItems().addAll("Semua Kategori", "Makanan", "Minuman", "Snack", "Lainnya");
        categoryFilter.setValue("Semua Kategori");
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

            // TODO: Tim dapat menambahkan sorting default
            // Contoh: sort berdasarkan nama, tanggal dibuat, dll.

            System.out.println("Dimuat " + productList.size() + " produk untuk toko: " + currentStore.getName());
        } catch (Exception e) {
            System.err.println("Error memuat produk: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat produk: " + e.getMessage());
        }
    }

    /**
     * Tampilkan detail produk di panel detail
     *
     * @param product Produk yang dipilih
     */
    private void showProductDetails(Product product) {
        detailNameLabel.setText(product.getName());
        detailCategoryLabel.setText(product.getCategory());
        detailBasePriceLabel.setText(String.format("Rp %,.0f", product.getBasePrice()));
        detailDescriptionLabel.setText(product.getDescription() != null ? product.getDescription() : "Tidak ada deskripsi");

        // TODO: Tim dapat menambahkan detail tambahan
        // Contoh: gambar produk, total stok, tanggal dibuat, dll.
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
     * Buka dialog tambah produk
     */
    @FXML
    private void showAddProduct() {
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
     * Buka dialog edit produk
     *
     * @param product Produk yang akan diedit
     */
    private void openEditProductDialog(Product product) {
        try {
            // TODO: Tim dapat menambahkan validasi permission di sini
            // Contoh: staff hanya bisa edit produk yang mereka buat

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
     * Handler untuk hapus produk
     *
     * @param product Produk yang akan dihapus
     */
    private void handleDeleteProduct(Product product) {
        // TODO: Tim dapat menambahkan validasi sebelum hapus
        // Contoh:
        // - Cek apakah produk masih memiliki stok
        // - Cek apakah produk pernah ada dalam transaksi
        // - Validasi permission user

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Produk");
        alert.setHeaderText("Apakah Anda yakin ingin menghapus produk ini?");
        alert.setContentText("Produk: " + product.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // TODO: Tim perlu implementasi soft delete atau backup data
                    // Sebelum menghapus:
                    // 1. Backup data produk dan variasinya
                    // 2. Cek referensi di tabel TransactionDetails
                    // 3. Update atau hapus variasi produk terlebih dahulu

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
        String category = categoryFilter.getValue();

        try {
            // TODO: Tim dapat mengoptimalkan dengan query database langsung
            // Saat ini filter di memori, bisa dibuat query LIKE di database

            List<Product> allProducts = productService.getAllProducts();
            productList.clear();

            for (Product product : allProducts) {
                if (product.getStoreID() == currentStore.getId()) {
                    boolean matchesSearch = searchTerm.isEmpty() ||
                            product.getName().toLowerCase().contains(searchTerm) ||
                            product.getCategory().toLowerCase().contains(searchTerm);

                    boolean matchesCategory = "Semua Kategori".equals(category) ||
                            product.getCategory().equals(category);

                    if (matchesSearch && matchesCategory) {
                        productList.add(product);
                    }
                }
            }

            // TODO: Tim dapat menambahkan highlight hasil pencarian

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
        categoryFilter.setValue("Semua Kategori");
        loadProducts();
        clearProductDetails();
        selectedProduct = null;
        editProductBtn.setDisable(true);
        deleteProductBtn.setDisable(true);
    }

    /**
     * Handler untuk tambah variasi produk
     */
    @FXML
    private void showAddVariation() {
        if (selectedProduct == null) {
            AlertUtil.showWarning("Tidak Ada Produk", "Silakan pilih produk terlebih dahulu untuk menambah variasi.");
            return;
        }

        // TODO: Tim implementasikan dialog tambah variasi produk
        // Buat AddVariationController dan AddVariationView.fxml
        AlertUtil.showInfo("Tambah Variasi", "Fitur tambah variasi akan segera tersedia!");
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
