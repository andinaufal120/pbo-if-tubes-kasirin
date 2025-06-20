package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kasirin.data.model.Product;
import kasirin.data.model.ProductVariation;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.ProductService;
import kasirin.service.ProductVariationService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Simplified Controller untuk Dialog Edit Produk dengan Satu Variasi
 * Menangani pengeditan produk dan variasi tunggalnya
 *
 * @author yamaym
 */
public class EditProductController implements Initializable {

    // Product fields
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField basePriceField;
    @FXML private TextArea descriptionArea;


    // Single variation fields
    @FXML private TextField variationTypeField;
    @FXML private TextField variationValueField;
    @FXML private TextField variationStockField;
    @FXML private TextField variationPriceField;

    // Action buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private Store currentStore;
    private Product currentProduct;
    private ProductVariation currentVariation;
    private ProductService productService;
    private ProductVariationService variationService;
    private ProductsController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        variationService = new ProductVariationService();

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
        loadProductVariation();

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
        }
    }

    /**
     * Load variasi produk yang sudah ada (hanya satu)
     */
    private void loadProductVariation() {
        if (currentProduct != null) {
            try {
                List<ProductVariation> variations = variationService.getVariationsByProductId(currentProduct.getId());

                if (!variations.isEmpty()) {
                    // Ambil variasi pertama (dan seharusnya satu-satunya)
                    currentVariation = variations.get(0);

                    variationTypeField.setText(currentVariation.getType());
                    variationValueField.setText(currentVariation.getValue());
                    variationStockField.setText(String.valueOf(currentVariation.getStocks()));
                    variationPriceField.setText(String.valueOf(currentVariation.getAdditionalPrice()));
                } else {
                    // Jika tidak ada variasi, buat default
                    variationTypeField.setText("Default");
                    variationValueField.setText("Standard");
                    variationStockField.setText("0");
                    variationPriceField.setText("0");
                }
            } catch (Exception e) {
                System.err.println("Error loading product variation: " + e.getMessage());
                AlertUtil.showWarning("Peringatan", "Gagal memuat variasi produk yang ada.");

                // Set default values
                variationTypeField.setText("Default");
                variationValueField.setText("Standard");
                variationStockField.setText("0");
                variationPriceField.setText("0");
            }
        }
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
     * Handler untuk tombol simpan
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            // Update data produk
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double basePrice = Double.parseDouble(basePriceField.getText().trim());
            String description = descriptionArea.getText().trim();

            currentProduct.setName(name);
            currentProduct.setCategory(category);
            currentProduct.setBasePrice(basePrice);
            currentProduct.setDescription(description);

            // Update produk di database
            boolean productUpdated = productService.updateProduct(currentProduct.getId(), currentProduct);

            if (productUpdated) {
                // Update atau buat variasi
                String type = variationTypeField.getText().trim();
                String value = variationValueField.getText().trim();
                int stock = Integer.parseInt(variationStockField.getText().trim());
                double additionalPrice = variationPriceField.getText().trim().isEmpty() ?
                        0.0 : Double.parseDouble(variationPriceField.getText().trim());

                boolean variationUpdated = false;

                if (currentVariation != null) {
                    // Update variasi yang sudah ada
                    currentVariation.setType(type);
                    currentVariation.setValue(value);
                    currentVariation.setStocks(stock);
                    currentVariation.setAdditionalPrice(additionalPrice);

                    variationUpdated = variationService.updateProductVariation(currentVariation.getId(), currentVariation);
                } else {
                    // Buat variasi baru
                    ProductVariation newVariation = new ProductVariation(currentProduct.getId(), type, value, additionalPrice, stock);
                    int variationId = variationService.addProductVariation(newVariation);
                    variationUpdated = variationId > 0;
                }

                if (variationUpdated) {
                    AlertUtil.showInfo("Berhasil",
                            "Produk '" + name + "' berhasil diperbarui!");

                    // Refresh data di parent controller
                    if (parentController != null) {
                        parentController.reloadProducts();
                    }

                    handleCancel();
                } else {
                    AlertUtil.showWarning("Sebagian Berhasil",
                            "Produk berhasil diperbarui, tetapi gagal memperbarui variasi.");
                }
            } else {
                AlertUtil.showError("Gagal", "Gagal memperbarui produk. Silakan coba lagi.");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan nilai numerik yang valid.");
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saat update produk: " + e.getMessage());
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Validasi input form
     */
    private boolean validateInput() {
        // Validasi produk
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

        // Validasi variasi
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
            AlertUtil.showError("Validasi Gagal", "Stok wajib diisi!");
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
     * Handler untuk tombol batal
     */
    @FXML
    private void handleCancel() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
