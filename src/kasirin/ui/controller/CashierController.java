package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import kasirin.data.model.Role;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.StoreService;
import kasirin.service.UserService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Manajemen Kasir
 * Menangani CRUD operasi untuk user dengan role staff (kasir)
 *
 * @author yamaym
 */
public class CashierController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button addCashierBtn;
    @FXML private Button clearFormBtn;

    @FXML private TableView<User> cashierTable;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, Void> actionsCol;

    @FXML private Label totalCashiersLabel;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;

    private User currentUser;
    private Store currentStore;
    private UserService userService;
    private StoreService storeService;
    private ObservableList<User> cashierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userService = new UserService();
            storeService = new StoreService();
            cashierList = FXCollections.observableArrayList();

            setupTable();
            setupValidation();

            System.out.println("CashierController berhasil diinisialisasi");
        } catch (Exception e) {
            System.err.println("Error inisialisasi CashierController: " + e.getMessage());
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

            loadCashiers();

            // TODO: Tim dapat menambahkan logika berdasarkan role user
            // Contoh: hanya admin/owner yang bisa menambah kasir
            if (!user.getRole().getValue().equals("admin") && !user.getRole().getValue().equals("owner")) {
                // Disable form untuk non-admin
                disableAddForm();
            }

            System.out.println("Manajemen kasir dimuat untuk toko: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error inisialisasi data kasir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup tabel kasir dan kolom-kolomnya
     */
    private void setupTable() {
        // Setup kolom tabel
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole().getValue().toUpperCase())
        );

        // Setup kolom aksi dengan tombol hapus
        actionsCol.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<User, Void>() {
                private final Button deleteBtn = new Button("🗑️");

                {
                    deleteBtn.getStyleClass().add("danger-button");
                    deleteBtn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        handleDeleteCashier(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteBtn);
                    }
                }
            };
            return cell;
        });

        cashierTable.setItems(cashierList);

        // TODO: Tim dapat menambahkan kolom tambahan jika diperlukan
        // Contoh: tanggal dibuat, status aktif, dll.
    }

    /**
     * Setup validasi form input
     */
    private void setupValidation() {
        // Validasi username: tidak boleh ada spasi
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                usernameField.setText(oldValue);
            }
        });

        // TODO: Tim dapat menambahkan validasi tambahan
        // Contoh:
        // - Username harus unik
        // - Password harus memenuhi kriteria keamanan
        // - Nama tidak boleh mengandung karakter khusus

        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Ketika field kehilangan fokus
                // TODO: Implementasi pengecekan username duplikat
                checkUsernameAvailability();
            }
        });
    }

    /**
     * Cek ketersediaan username
     * TODO: Tim implementasikan pengecekan ke database
     */
    private void checkUsernameAvailability() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            // TODO: Implementasi pengecekan username ke database
            // boolean isAvailable = userService.isUsernameAvailable(username);
            // if (!isAvailable) {
            //     // Tampilkan peringatan username sudah digunakan
            // }
        }
    }

    /**
     * Muat daftar kasir untuk toko saat ini
     */
    private void loadCashiers() {
        try {
            // TODO: Tim dapat mengoptimalkan query ini
            // Saat ini mengambil semua user lalu filter, bisa dibuat query khusus

            List<User> allUsers = userService.getAllUsers();
            cashierList.clear();

            for (User user : allUsers) {
                if (user.getRole() == Role.STAFF) {
                    // Cek apakah user memiliki akses ke toko saat ini
                    List<Store> userStores = storeService.getStoresByUserId(user.getId());
                    for (Store store : userStores) {
                        if (store.getId() == currentStore.getId()) {
                            cashierList.add(user);
                            break;
                        }
                    }
                }
            }

            totalCashiersLabel.setText(cashierList.size() + " kasir");

            // TODO: Tim dapat menambahkan statistik tambahan
            // Contoh: kasir aktif hari ini, total transaksi per kasir, dll.

            System.out.println("Dimuat " + cashierList.size() + " kasir untuk toko: " + currentStore.getName());
        } catch (Exception e) {
            System.err.println("Error memuat kasir: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal memuat daftar kasir: " + e.getMessage());
        }
    }

    /**
     * Handler untuk tombol tambah kasir
     */
    @FXML
    private void handleAddCashier() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // TODO: Tim dapat menambahkan validasi bisnis tambahan
            // Contoh:
            // - Cek limit maksimal kasir per toko
            // - Validasi format nama (tidak boleh angka, dll.)
            // - Enkripsi password dengan algoritma yang lebih kuat

            // Buat user kasir baru dengan role STAFF
            User newCashier = userService.registerUser(name, username, password, confirmPassword, Role.STAFF);

            // Link kasir ke toko saat ini melalui user_store_access
            boolean linked = storeService.linkUserToStore(newCashier.getId(), currentStore.getId());

            if (linked) {
                // TODO: Tim dapat menambahkan logika setelah berhasil membuat kasir
                // Contoh:
                // - Kirim email welcome ke kasir baru
                // - Log aktivitas pembuatan kasir
                // - Set default permissions untuk kasir

                AlertUtil.showInfo("Berhasil", "Kasir berhasil dibuat!");
                clearForm();
                loadCashiers();

                // TODO: Tim dapat menambahkan notifikasi real-time ke admin lain

            } else {
                AlertUtil.showError("Error", "Kasir berhasil dibuat tetapi gagal menghubungkan ke toko. Silakan coba lagi.");
            }

        } catch (IllegalArgumentException e) {
            AlertUtil.showError("Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            // TODO: Tim dapat menambahkan logging error yang lebih detail
            System.err.println("Error membuat kasir: " + e.getMessage());
            AlertUtil.showError("Error", "Gagal membuat kasir: " + e.getMessage());
        }
    }

    /**
     * Handler untuk hapus kasir
     *
     * @param cashier User kasir yang akan dihapus
     */
    private void handleDeleteCashier(User cashier) {
        // TODO: Tim dapat menambahkan validasi sebelum hapus
        // Contoh:
        // - Cek apakah kasir masih memiliki transaksi aktif
        // - Konfirmasi dengan password admin
        // - Transfer transaksi ke kasir lain

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Kasir");
        alert.setHeaderText("Apakah Anda yakin ingin menghapus kasir ini?");
        alert.setContentText("Kasir: " + cashier.getName() + " (" + cashier.getUsername() + ")");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // TODO: Tim perlu implementasi soft delete atau transfer data
                    // Sebelum menghapus user, pastikan:
                    // 1. Backup/transfer semua transaksi kasir
                    // 2. Update referensi di tabel lain
                    // 3. Log aktivitas penghapusan

                    boolean deleted = userService.deleteUser(cashier.getId());
                    if (deleted) {
                        AlertUtil.showInfo("Berhasil", "Kasir berhasil dihapus!");
                        loadCashiers();

                        // TODO: Tim dapat menambahkan cleanup tambahan
                        // Contoh: hapus session aktif kasir, notifikasi, dll.

                    } else {
                        AlertUtil.showError("Error", "Gagal menghapus kasir.");
                    }
                } catch (Exception e) {
                    AlertUtil.showError("Error", "Gagal menghapus kasir: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Validasi form input
     *
     * @return true jika semua input valid
     */
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Nama wajib diisi!");
            nameField.requestFocus();
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Username wajib diisi!");
            usernameField.requestFocus();
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            AlertUtil.showError("Validasi Gagal", "Password wajib diisi!");
            passwordField.requestFocus();
            return false;
        }

        if (passwordField.getText().length() < 6) {
            AlertUtil.showError("Validasi Gagal", "Password minimal 6 karakter!");
            passwordField.requestFocus();
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.showError("Validasi Gagal", "Password dan konfirmasi password tidak sama!");
            confirmPasswordField.requestFocus();
            return false;
        }

        // TODO: Tim dapat menambahkan validasi tambahan
        // Contoh:
        // - Password harus mengandung huruf besar, kecil, angka
        // - Username tidak boleh mengandung kata-kata terlarang
        // - Nama harus format yang benar

        return true;
    }

    /**
     * Bersihkan form input
     */
    @FXML
    private void clearForm() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.requestFocus();
    }

    /**
     * Handler untuk pencarian kasir
     */
    @FXML
    private void searchCashiers() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadCashiers();
            return;
        }

        try {
            // TODO: Tim dapat mengoptimalkan pencarian ini dengan query database
            // Saat ini filter di memori, bisa dibuat query LIKE di database

            List<User> allUsers = userService.getAllUsers();
            cashierList.clear();

            for (User user : allUsers) {
                if (user.getRole() == Role.STAFF) {
                    // Cek akses ke toko saat ini
                    List<Store> userStores = storeService.getStoresByUserId(user.getId());
                    boolean hasAccess = userStores.stream().anyMatch(store -> store.getId() == currentStore.getId());

                    // Filter berdasarkan nama atau username
                    if (hasAccess && (user.getName().toLowerCase().contains(searchTerm) ||
                            user.getUsername().toLowerCase().contains(searchTerm))) {
                        cashierList.add(user);
                    }
                }
            }

            totalCashiersLabel.setText(cashierList.size() + " kasir ditemukan");

            // TODO: Tim dapat menambahkan highlight hasil pencarian

        } catch (Exception e) {
            AlertUtil.showError("Error", "Pencarian gagal: " + e.getMessage());
        }
    }

    /**
     * Handler untuk refresh daftar kasir
     */
    @FXML
    private void refreshCashiers() {
        searchField.clear();
        loadCashiers();
        AlertUtil.showInfo("Refresh", "Daftar kasir berhasil diperbarui!");
    }

    /**
     * Disable form tambah kasir (untuk user non-admin)
     */
    private void disableAddForm() {
        nameField.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        addCashierBtn.setDisable(true);
        clearFormBtn.setDisable(true);

        // TODO: Tim dapat menambahkan pesan informasi mengapa form di-disable
    }
}
