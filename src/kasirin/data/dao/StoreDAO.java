package kasirin.data.dao;

import kasirin.data.model.Store;

/// Provides CRUD methods for "Store" entity in datasource.
///
/// @author yamaym
public interface StoreDAO {
    public int insertStore(Store store);
    public int findStore(int id);
    public int updateStore(int id, Store store);
    public int deleteStore(int id);
}
