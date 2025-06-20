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
import java.util.ResourceBundle;

/**
 * Simplified Controller untuk Dialog Tambah Produk dengan Satu Variasi
 * Menangani pembuatan produk baru dengan satu variasi saja
 *
 * @author yamaym
 */
public class AddProductController implements Initializable {

    // Product fields
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField basePriceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;

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
    private ProductService productService;
    private ProductVariationService variationService;
    private ProductsController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        variationService = new ProductVariationService();

        setupValidation();
        setupDefaultValues();
    }

    /**
     * Inisialisasi dengan data dari controller parent
     */
    public void initializeWithData(User user, Store store, ProductsController parentController) {
        this.currentUser = user;
        this.currentStore = store;
        this.parentController = parentController;

        if (user.getRole().getValue().equals("staff")) {
            // Implementasi pembatasan untuk staff jika diperlukan
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
     * Setup nilai default untuk variasi
     */
    private void setupDefaultValues() {
        // Set default values untuk memudahkan input
        variationTypeField.setText("Default");
        variationValueField.setText("Standard");
        variationStockField.setText("0");
        variationPriceField.setText("0");
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
            // Ambil data produk dari form
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double basePrice = Double.parseDouble(basePriceField.getText().trim());
            String description = descriptionArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            // Buat objek produk baru
            Product product = new Product(name, currentStore.getId(), category, basePrice, description, imageUrl);

            // Simpan produk ke database
            int productId = productService.addProduct(product);

            if (productId > 0) {
                // Ambil data variasi dari form
                String type = variationTypeField.getText().trim();
                String value = variationValueField.getText().trim();
                int stock = Integer.parseInt(variationStockField.getText().trim());
                double additionalPrice = variationPriceField.getText().trim().isEmpty() ?
                        0.0 : Double.parseDouble(variationPriceField.getText().trim());

                // Buat variasi untuk produk
                ProductVariation variation = new ProductVariation(productId, type, value, additionalPrice, stock);
                int variationId = variationService.addProductVariation(variation);

                if (variationId > 0) {
                    AlertUtil.showInfo("Berhasil",
                            "Produk '" + name + "' berhasil ditambahkan dengan variasi '" + value + "'!");

                    // Refresh data di parent controller
                    if (parentController != null) {
                        parentController.reloadProducts();
                    }

                    handleCancel();
                } else {
                    // Jika variasi gagal, hapus produk yang sudah dibuat
                    productService.deleteProduct(productId);
                    AlertUtil.showError("Gagal", "Gagal menambahkan variasi produk. Produk dibatalkan.");
                }
            } else {
                AlertUtil.showError("Gagal", "Gagal menambahkan produk. Silakan coba lagi.");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan nilai numerik yang valid.");
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saat menyimpan produk: " + e.getMessage());
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
