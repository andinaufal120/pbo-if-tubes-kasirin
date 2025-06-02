package kasirin.data.dao;

import kasirin.data.model.Store;

import java.util.List;

/// Provides CRUD methods for "Store" entity in datasource.
///
/// @author yamaym
public interface StoreDAO {
    /// Inserts a new store to datasource.
    ///
    /// @param store "Store" transfer object
    /// @return newly created store ID or a {@code -1} on error
    public int insertStore(Store store);

    /// Finds a store based on criteria.
    ///
    /// @param id store ID to search
    /// @return "Store" transfer object
    public Store findStore(int id);

    /// Updates an existing store in datasource.
    ///
    /// @param id    store ID to update
    /// @param store "Store" transfer object with updated fields
    /// @return number of affected rows or a {@code -1} on error
    public int updateStore(int id, Store store);

    /// Deletes an existing store in datasource.
    ///
    /// @param id store ID to be deleted
    /// @return number of affected rows or a {@code -1} on error
    public int deleteStore(int id);

    /// Gets a list of all stores available in datasource.
    ///
    /// @return a list of "Store" transfer objects
    public List<Store> findAllStores();
}
