package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlStoreDAO;
import kasirin.data.model.Store;
import java.util.List;

/// Service layer untuk menangani logika bisnis terkait Store
/// @author yamaym
public class StoreService {
    private MySqlStoreDAO storeDAO;

    public StoreService() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.storeDAO = (MySqlStoreDAO) factory.getStoreDAO();
    }

    /// Membuat toko baru dan menghubungkannya dengan user
    public int createStore(Store store, int userId) {
        if (store == null || store.getName() == null || store.getName().trim().isEmpty()) {
            return -1;
        }

        // Insert store
        int storeId = storeDAO.insertStore(store);

        // Link user to store if store creation was successful
        if (storeId > 0) {
            boolean linked = storeDAO.linkUserToStore(userId, storeId);
            if (!linked) {
                // If linking fails, we should ideally roll back the store creation
                // but for simplicity, we'll just return an error code
                return -2;
            }
        }

        return storeId;
    }

    /// Mendapatkan semua toko
    public List<Store> getAllStores() {
        return storeDAO.getAllStores();
    }

    /// Mendapatkan toko berdasarkan ID
    public Store getStoreById(int id) {
        return storeDAO.findStore(id);
    }

    /// Mendapatkan toko yang dimiliki/diakses oleh user tertentu
    public List<Store> getStoresByUserId(int userId) {
        return storeDAO.getStoresByUserId(userId);
    }

    /// Update toko
    public boolean updateStore(int id, Store store) {
        if (store == null) {
            return false;
        }
        return storeDAO.updateStore(id, store) > 0;
    }

    /// Hapus toko
    public boolean deleteStore(int id) {
        return storeDAO.deleteStore(id) > 0;
    }

    /// Menghubungkan user dengan toko (many-to-many)
    public boolean linkUserToStore(int userId, int storeId) {
        return storeDAO.linkUserToStore(userId, storeId);
    }

    /// Tutup koneksi database
    public void closeConnection() {
        if (storeDAO != null) {
            storeDAO.closeConnection();
        }
    }
}
