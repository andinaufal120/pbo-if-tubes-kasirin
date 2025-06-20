package kasirin.service;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlStoreDAO;
import kasirin.data.model.Store;
import java.util.List;

/**
 * Service layer for handling store-related business logic
 * Decoupled from UI components to maintain separation of concerns
 *
 * @author yamaym
 */
public class StoreService {
    private MySqlStoreDAO storeDAO;

    public StoreService() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.storeDAO = (MySqlStoreDAO) factory.getStoreDAO();
    }

    /**
     * Creates a new store and links it to a user
     *
     * @param store Store object to create
     * @param userId User ID to link the store to
     * @return Store ID if successful, -1 if validation fails, -2 if linking fails
     */
    public int createStore(Store store, int userId) {
        // Validate store data
        if (store == null || store.getName() == null || store.getName().trim().isEmpty()) {
            return -1;
        }

        // Insert store
        int storeId = storeDAO.insertStore(store);

        // Link user to store if store creation was successful
        if (storeId > 0) {
            boolean linked = storeDAO.linkUserToStore(userId, storeId);
            if (!linked) {
                // If linking fails, return error code
                return -2;
            }

            // Set the ID from the database
            store.setId(storeId);
        }

        return storeId;
    }

    /**
     * Gets all stores from the database
     *
     * @return List of all stores
     */
    public List<Store> getAllStores() {
        return storeDAO.getAllStores();
    }

    /**
     * Finds a store by its ID
     *
     * @param id Store ID to search for
     * @return Store object if found, null otherwise
     */
    public Store getStoreById(int id) {
        return storeDAO.findStore(id);
    }

    /**
     * Gets all stores accessible by a specific user
     *
     * @param userId User ID to get stores for
     * @return List of stores accessible by the user
     */
    public List<Store> getStoresByUserId(int userId) {
        return storeDAO.getStoresByUserId(userId);
    }

    /**
     * Updates an existing store
     *
     * @param id Store ID to update
     * @param store Updated Store object
     * @return true if update was successful, false otherwise
     */
    public boolean updateStore(int id, Store store) {
        if (store == null) {
            return false;
        }
        return storeDAO.updateStore(id, store) > 0;
    }

    /**
     * Deletes a store by its ID
     *
     * @param id Store ID to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteStore(int id) {
        return storeDAO.deleteStore(id) > 0;
    }

    /**
     * Links a user to a store (many-to-many relationship)
     *
     * @param userId User ID to link
     * @param storeId Store ID to link
     * @return true if linking was successful, false otherwise
     */
    public boolean linkUserToStore(int userId, int storeId) {
        return storeDAO.linkUserToStore(userId, storeId);
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        if (storeDAO != null) {
            storeDAO.closeConnection();
        }
    }
}
