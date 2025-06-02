package kasirin.service;

import kasirin.data.dao.StoreDAO;
import kasirin.data.dao.MySqlStoreDAO;
import kasirin.data.model.Store;

public class StoreService {
    private final StoreDAO storeDAO;

    public StoreService() {
        this.storeDAO = new MySqlStoreDAO();
    }

    public int addStore(Store store) {
        validateStore(store);
        return storeDAO.insertStore(store);
    }

    public Store getStoreById(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID store tidak valid.");
        return storeDAO.findStore(id);
    }

    public int updateStore(int id, Store updatedStore) {
        if (id <= 0) throw new IllegalArgumentException("ID store tidak valid.");
        validateStore(updatedStore);
        return storeDAO.updateStore(id, updatedStore);
    }

    public int deleteStore(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID store tidak valid.");
        return storeDAO.deleteStore(id);
    }

    private void validateStore(Store store) {
        if (store == null) throw new IllegalArgumentException("Objek store tidak boleh null.");
        if (store.getName() == null || store.getName().trim().isEmpty())
            throw new IllegalArgumentException("Nama store tidak boleh kosong.");
        if (store.getAddress() == null || store.getAddress().trim().isEmpty())
            throw new IllegalArgumentException("Alamat store tidak boleh kosong.");
    }
}
