package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import kasirin.data.model.Product;
import kasirin.data.model.ProductVariation;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.ProductService;
import kasirin.service.ProductVariationService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Dialog Edit Produk dengan Variasi
 * Menangani pengeditan produk dan variasinya yang sudah ada
 *
 * @author yamaym
 */
public class EditProductController implements Initializable {

    // Product fields
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField basePriceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;

    // Variation fields
    @FXML private TextField variationTypeField;
    @FXML private TextField variationValueField;
    @FXML private TextField variationStockField;
    @FXML private TextField variationPriceField;
    @FXML private Button addVariationBtn;

    // Variations table
    @FXML private TableView<ProductVariation> variationsTable;
    @FXML private TableColumn<ProductVariation, String> typeColumn;
    @FXML private TableColumn<ProductVariation, String> valueColumn;
    @FXML private TableColumn<ProductVariation, Integer> stockColumn;
    @FXML private TableColumn<ProductVariation, Double> priceColumn;
    @FXML private TableColumn<ProductVariation, Void> actionsColumn;

    // Action buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private Store currentStore;
    private Product currentProduct;
    private ProductService productService;
    private ProductVariationService variationService;
    private ProductsController parentController;
    private ObservableList<ProductVariation> variations;
    private List<ProductVariation> originalVariations;
    private List<ProductVariation> variationsToDelete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        variationService = new ProductVariationService();
        variations = FXCollections.observableArrayList();
        originalVariations = new ArrayList<>();
        variationsToDelete = new ArrayList<>();

        setupVariationsTable();
        setupValidation();
    }

    /**
     * Inisialisasi dengan data dari controller parent
     */
    public void initializeWithData(User user, Store store, Product product, ProductsController parentController) {
        this.currentUser = user;
        this.currentStore = store;
        this.currentProduct = product;
        this.parentController = parentController;

        populateFields();
        loadExistingVariations();

        if (user.getRole().getValue().equals("staff")) {
            // Implementasi pembatasan edit untuk staff jika diperlukan
        }
    }

    /**
     * Mengisi field dengan data produk yang akan diedit
     */
    private void populateFields() {
        if (currentProduct != null) {
            nameField.setText(currentProduct.getName());
            categoryField.setText(currentProduct.getCategory());
            basePriceField.setText(String.valueOf(currentProduct.getBasePrice()));
            descriptionArea.setText(currentProduct.getDescription() != null ? currentProduct.getDescription() : "");
            imageUrlField.setText(currentProduct.getImageURL() != null ? currentProduct.getImageURL() : "");
        }
    }

    /**
     * Load variasi produk yang sudah ada
     */
    private void loadExistingVariations() {
        if (currentProduct != null) {
            try {
                List<ProductVariation> existingVariations = variationService.getVariationsByProductId(currentProduct.getId());
                originalVariations.addAll(existingVariations);
                variations.addAll(existingVariations);
            } catch (Exception e) {
                System.err.println("Error loading existing variations: " + e.getMessage());
                AlertUtil.showWarning("Peringatan", "Gagal memuat variasi produk yang ada.");
            }
        }
    }

    /**
     * Setup tabel variasi produk
     */
    private void setupVariationsTable() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stocks"));

        // Format kolom harga dengan mata uang Rupiah
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("additionalPrice"));
        priceColumn.setCellFactory(col -> new TableCell<ProductVariation, Double>() {
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

        // Setup kolom aksi dengan tombol edit dan hapus
        actionsColumn.setCellFactory(col -> {
            TableCell<ProductVariation, Void> cell = new TableCell<ProductVariation, Void>() {
                private final Button editBtn = new Button("✏️");
                private final Button deleteBtn = new Button("🗑️");

                {
                    editBtn.getStyleClass().add("small-button");
                    deleteBtn.getStyleClass().add("danger-button");

                    editBtn.setOnAction(event -> {
                        ProductVariation variation = getTableView().getItems().get(getIndex());
                        editVariation(variation);
                    });

                    deleteBtn.setOnAction(event -> {
                        ProductVariation variation = getTableView().getItems().get(getIndex());
                        removeVariation(variation);
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

        variationsTable.setItems(variations);
    }

    /**
     * Setup validasi untuk field input
     */
    private void setupValidation() {
        // Validasi harga produk: hanya angka dan titik desimal
        basePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                basePriceField.setText(oldValue);
            }
        });

        // Validasi harga tambahan variasi: hanya angka dan titik desimal
        variationPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                variationPriceField.setText(oldValue);
            }
        });

        // Validasi stok variasi: hanya angka
        variationStockField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                variationStockField.setText(oldValue);
            }
        });
    }

    /**
     * Handler untuk menambah variasi baru ke tabel
     */
    @FXML
    private void handleAddVariation() {
        if (!validateVariationInput()) {
            return;
        }

        try {
            String type = variationTypeField.getText().trim();
            String value = variationValueField.getText().trim();
            int stock = Integer.parseInt(variationStockField.getText().trim());
            double additionalPrice = variationPriceField.getText().trim().isEmpty() ?
                    0.0 : Double.parseDouble(variationPriceField.getText().trim());

            // Cek duplikasi variasi (type + value harus unik)
            boolean isDuplicate = variations.stream()
                    .anyMatch(v -> v.getType().equalsIgnoreCase(type) && v.getValue().equalsIgnoreCase(value));

            if (isDuplicate) {
                AlertUtil.showError("Variasi Duplikat",
                        "Variasi dengan tipe '" + type + "' dan nilai '" + value + "' sudah ada!");
                return;
            }

            // Buat variasi baru
            ProductVariation variation = new ProductVariation(currentProduct.getId(), type, value, additionalPrice, stock);
            variations.add(variation);

            // Bersihkan field input variasi
            clearVariationFields();

            AlertUtil.showInfo("Berhasil", "Variasi berhasil ditambahkan ke daftar!");

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan nilai numerik yang valid.");
        }
    }

    /**
     * Handler untuk edit variasi yang sudah ada
     */
    private void editVariation(ProductVariation variation) {
        // Isi field dengan data variasi yang akan diedit
        variationTypeField.setText(variation.getType());
        variationValueField.setText(variation.getValue());
        variationStockField.setText(String.valueOf(variation.getStocks()));
        variationPriceField.setText(String.valueOf(variation.getAdditionalPrice()));

        // Hapus variasi dari tabel sementara (akan ditambah kembali saat save)
        variations.remove(variation);

        // Jika variasi ini adalah variasi yang sudah ada di database, tandai untuk update
        if (variation.getId() > 0) {
            // Variasi akan diupdate saat save
        }
    }

    /**
     * Handler untuk menghapus variasi dari tabel
     */
    private void removeVariation(ProductVariation variation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Variasi");
        alert.setHeaderText("Apakah Anda yakin ingin menghapus variasi ini?");
        alert.setContentText("Tipe: " + variation.getType() + ", Nilai: " + variation.getValue());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                variations.remove(variation);

                // Jika variasi ini sudah ada di database, tandai untuk dihapus
                if (variation.getId() > 0) {
                    variationsToDelete.add(variation);
                }

                AlertUtil.showInfo("Berhasil", "Variasi berhasil dihapus dari daftar!");
            }
        });
    }

    /**
     * Validasi input variasi
     */
    private boolean validateVariationInput() {
        if (variationTypeField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Tipe variasi wajib diisi!");
            variationTypeField.requestFocus();
            return false;
        }

        if (variationValueField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Nilai variasi wajib diisi!");
            variationValueField.requestFocus();
            return false;
        }

        if (variationStockField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Stok variasi wajib diisi!");
            variationStockField.requestFocus();
            return false;
        }

        try {
            int stock = Integer.parseInt(variationStockField.getText().trim());
            if (stock < 0) {
                AlertUtil.showError("Validasi Gagal", "Stok tidak boleh negatif!");
                variationStockField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validasi Gagal", "Silakan masukkan stok yang valid!");
            variationStockField.requestFocus();
            return false;
        }

        if (!variationPriceField.getText().trim().isEmpty()) {
            try {
                double price = Double.parseDouble(variationPriceField.getText().trim());
                if (price < 0) {
                    AlertUtil.showError("Validasi Gagal", "Harga tambahan tidak boleh negatif!");
                    variationPriceField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Validasi Gagal", "Silakan masukkan harga tambahan yang valid!");
                variationPriceField.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Bersihkan field input variasi
     */
    private void clearVariationFields() {
        variationTypeField.clear();
        variationValueField.clear();
        variationStockField.clear();
        variationPriceField.clear();
    }

    /**
     * Handler untuk tombol simpan
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        // Validasi minimal harus ada satu variasi
        if (variations.isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Produk harus memiliki minimal satu variasi!");
            return;
        }

        try {
            // Update data produk
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double basePrice = Double.parseDouble(basePriceField.getText().trim());
            String description = descriptionArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            currentProduct.setName(name);
            currentProduct.setCategory(category);
            currentProduct.setBasePrice(basePrice);
            currentProduct.setDescription(description);
            currentProduct.setImageURL(imageUrl);

            // Update produk di database
            boolean productUpdated = productService.updateProduct(currentProduct.getId(), currentProduct);

            if (productUpdated) {
                // Hapus variasi yang ditandai untuk dihapus
                for (ProductVariation variation : variationsToDelete) {
                    try {
                        variationService.deleteProductVariation(variation.getId());
                    } catch (Exception e) {
                        System.err.println("Error deleting variation: " + e.getMessage());
                    }
                }

                // Simpan/update semua variasi
                boolean allVariationsProcessed = true;
                List<String> failedVariations = new ArrayList<>();

                for (ProductVariation variation : variations) {
                    try {
                        if (variation.getId() > 0) {
                            // Update variasi yang sudah ada
                            boolean updated = variationService.updateProductVariation(variation.getId(), variation);
                            if (!updated) {
                                allVariationsProcessed = false;
                                failedVariations.add(variation.getType() + " - " + variation.getValue() + " (update)");
                            }
                        } else {
                            // Tambah variasi baru
                            variation.setProductId(currentProduct.getId());
                            int variationId = variationService.addProductVariation(variation);
                            if (variationId <= 0) {
                                allVariationsProcessed = false;
                                failedVariations.add(variation.getType() + " - " + variation.getValue() + " (new)");
                            }
                        }
                    } catch (Exception e) {
                        allVariationsProcessed = false;
                        failedVariations.add(variation.getType() + " - " + variation.getValue() + " (error)");
                        System.err.println("Error processing variation: " + e.getMessage());
                    }
                }

                if (allVariationsProcessed) {
                    AlertUtil.showInfo("Berhasil",
                            "Produk dan semua variasi berhasil diperbarui!");

                    // Refresh data di parent controller
                    if (parentController != null) {
                        parentController.reloadProducts();
                    }

                    handleCancel();
                } else {
                    AlertUtil.showWarning("Sebagian Berhasil",
                            "Produk berhasil diperbarui, tetapi beberapa variasi gagal:\n" +
                                    String.join("\n", failedVariations));
                }
            } else {
                AlertUtil.showError("Gagal", "Gagal memperbarui produk. Silakan coba lagi.");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan harga yang valid.");
            basePriceField.requestFocus();
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saat update produk: " + e.getMessage());
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Validasi input form produk
     */
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Nama produk wajib diisi!");
            nameField.requestFocus();
            return false;
        }

        if (categoryField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Kategori wajib diisi!");
            categoryField.requestFocus();
            return false;
        }

        if (basePriceField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Harga dasar wajib diisi!");
            basePriceField.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(basePriceField.getText().trim());
            if (price <= 0) {
                AlertUtil.showError("Validasi Gagal", "Harga harus lebih besar dari 0!");
                basePriceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validasi Gagal", "Silakan masukkan harga yang valid!");
            basePriceField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Handler untuk tombol batal
     */
    @FXML
    private void handleCancel() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
