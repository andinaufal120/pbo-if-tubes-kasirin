package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kasirin.data.model.Product;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.ProductService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk Dialog Edit Produk
 * Menangani pengeditan produk yang sudah ada
 *
 * @author yamaym
 */
public class EditProductController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField basePriceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private Store currentStore;
    private Product currentProduct;
    private ProductService productService;
    private ProductsController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        setupValidation();

        // TODO: Tim dapat menambahkan inisialisasi tambahan untuk edit mode
    }

    /**
     * Inisialisasi dengan data dari controller parent
     *
     * @param user User yang sedang login
     * @param store Toko yang sedang dikelola
     * @param product Produk yang akan diedit
     * @param parentController Controller parent untuk refresh data
     */
    public void initializeWithData(User user, Store store, Product product, ProductsController parentController) {
        this.currentUser = user;
        this.currentStore = store;
        this.currentProduct = product;
        this.parentController = parentController;

        populateFields();

        // TODO: Tim dapat menambahkan logika berdasarkan role user
        // Contoh: staff hanya bisa edit produk yang mereka buat
        if (user.getRole().getValue().equals("staff")) {
            // Implementasi pembatasan edit untuk staff
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

            // TODO: Tim dapat menambahkan pengisian field tambahan jika ada
        }
    }

    /**
     * Setup validasi untuk field input
     */
    private void setupValidation() {
        // Validasi harga: hanya angka dan titik desimal
        basePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                basePriceField.setText(oldValue);
            }
        });

        // TODO: Tim dapat menambahkan validasi tambahan seperti di AddProductController
    }

    /**
     * Handler untuk tombol simpan
     * Memproses update produk ke database
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            // Ambil data dari form
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double basePrice = Double.parseDouble(basePriceField.getText().trim());
            String description = descriptionArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            // TODO: Tim dapat menambahkan validasi perubahan
            // Contoh: cek apakah ada perubahan sebelum update

            // Update objek produk
            currentProduct.setName(name);
            currentProduct.setCategory(category);
            currentProduct.setBasePrice(basePrice);
            currentProduct.setDescription(description);
            currentProduct.setImageURL(imageUrl);

            // TODO: Tim dapat menambahkan field tambahan untuk tracking
            // Contoh: updated_by, updated_date

            // Update di database melalui service
            boolean success = productService.updateProduct(currentProduct.getId(), currentProduct);

            if (success) {
                // TODO: Tim dapat menambahkan logika setelah berhasil update
                // Contoh:
                // - Log aktivitas perubahan
                // - Notifikasi ke admin jika ada perubahan signifikan
                // - Update cache

                AlertUtil.showInfo("Berhasil", "Produk berhasil diperbarui!");

                // Refresh data di parent controller
                if (parentController != null) {
                    parentController.reloadProducts();
                }

                // Tutup dialog
                handleCancel();
            } else {
                AlertUtil.showError("Gagal", "Gagal memperbarui produk. Silakan coba lagi.");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan harga yang valid.");
            basePriceField.requestFocus();
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            // TODO: Tim dapat menambahkan logging error yang lebih detail
            System.err.println("Error saat update produk: " + e.getMessage());
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Validasi input form (sama seperti AddProductController)
     *
     * @return true jika semua input valid
     */
    private boolean validateInput() {
        // Validasi nama produk
        if (nameField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Nama produk wajib diisi!");
            nameField.requestFocus();
            return false;
        }

        // Validasi kategori
        if (categoryField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Kategori wajib diisi!");
            categoryField.requestFocus();
            return false;
        }

        // Validasi harga
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

        // TODO: Tim dapat menambahkan validasi tambahan sesuai kebutuhan

        return true;
    }

    /**
     * Handler untuk tombol batal
     */
    @FXML
    private void handleCancel() {
        // TODO: Tim dapat menambahkan konfirmasi jika ada perubahan yang belum disimpan
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
