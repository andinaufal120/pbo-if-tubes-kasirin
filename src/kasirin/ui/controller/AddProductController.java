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
 * Controller untuk Dialog Tambah Produk
 * Menangani pembuatan produk baru dalam sistem
 *
 * @author yamaym
 */
public class AddProductController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField basePriceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private Store currentStore;
    private ProductService productService;
    private ProductsController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productService = new ProductService();
        setupValidation();

        // TODO: Tim dapat menambahkan inisialisasi tambahan di sini
        // Contoh: setup auto-complete untuk kategori, validasi khusus, dll.
    }

    /**
     * Inisialisasi dengan data dari controller parent
     *
     * @param user User yang sedang login
     * @param store Toko yang sedang dikelola
     * @param parentController Controller parent untuk refresh data
     */
    public void initializeWithData(User user, Store store, ProductsController parentController) {
        this.currentUser = user;
        this.currentStore = store;
        this.parentController = parentController;

        // TODO: Tim dapat menambahkan logika inisialisasi khusus berdasarkan role user
        // Contoh: jika user adalah staff, batasi kategori yang bisa dipilih
        if (user.getRole().getValue().equals("staff")) {
            // Implementasi pembatasan untuk staff
        }
    }

    /**
     * Setup validasi untuk field input
     * Tim dapat menambahkan validasi tambahan sesuai kebutuhan bisnis
     */
    private void setupValidation() {
        // Validasi harga: hanya angka dan titik desimal
        basePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                basePriceField.setText(oldValue);
            }
        });

        // TODO: Tim dapat menambahkan validasi tambahan di sini
        // Contoh:
        // - Validasi nama produk tidak boleh duplikat dalam satu toko
        // - Validasi kategori harus sesuai dengan daftar yang diizinkan
        // - Validasi URL gambar harus format yang valid

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            // TODO: Implementasi validasi nama produk
            // Cek duplikasi nama dalam toko yang sama
        });
    }

    /**
     * Handler untuk tombol simpan
     * Memproses penyimpanan produk baru ke database
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

            // TODO: Tim dapat menambahkan validasi bisnis tambahan di sini
            // Contoh:
            // - Cek apakah kategori valid untuk toko ini
            // - Validasi harga minimum/maksimum
            // - Proses upload gambar jika diperlukan

            // Buat objek produk baru
            Product product = new Product(name, currentStore.getId(), category, basePrice, description, imageUrl);

            // TODO: Tim dapat menambahkan logika tambahan sebelum menyimpan
            // Contoh: set created_by, created_date, dll jika ada field tambahan

            // Simpan ke database melalui service
            int productId = productService.addProduct(product);

            if (productId > 0) {
                // TODO: Tim dapat menambahkan logika setelah berhasil menyimpan
                // Contoh:
                // - Log aktivitas user
                // - Kirim notifikasi ke admin
                // - Update cache produk

                AlertUtil.showInfo("Berhasil", "Produk berhasil ditambahkan!");

                // Refresh data di parent controller
                if (parentController != null) {
                    parentController.reloadProducts();
                }

                // Tutup dialog
                handleCancel();
            } else {
                AlertUtil.showError("Gagal", "Gagal menambahkan produk. Silakan coba lagi.");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Tidak Valid", "Silakan masukkan harga yang valid.");
            basePriceField.requestFocus();
        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            // TODO: Tim dapat menambahkan logging error yang lebih detail
            System.err.println("Error saat menyimpan produk: " + e.getMessage());
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    /**
     * Validasi input form
     * Tim dapat menambahkan aturan validasi sesuai kebutuhan bisnis
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

            // TODO: Tim dapat menambahkan validasi harga tambahan
            // Contoh: harga maksimum, harga minimum berdasarkan kategori

        } catch (NumberFormatException e) {
            AlertUtil.showError("Validasi Gagal", "Silakan masukkan harga yang valid!");
            basePriceField.requestFocus();
            return false;
        }

        // TODO: Tim dapat menambahkan validasi tambahan di sini
        // Contoh:
        // - Validasi format URL gambar
        // - Validasi panjang deskripsi
        // - Validasi kategori terhadap daftar yang diizinkan

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
