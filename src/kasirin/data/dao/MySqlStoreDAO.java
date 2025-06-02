package kasirin.data.dao;

import kasirin.data.model.Store;

import java.util.List;

public class MySqlStoreDAO implements StoreDAO {
    @Override
    public int insertStore(Store store) {
        return 0;
    }

    @Override
    public Store findStore(int id) {
        return null;
    }

    @Override
    public int updateStore(int id, Store store) {
        return 0;
    }

    @Override
    public int deleteStore(int id) {
        return 0;
    }

    @Override
    public List<Store> findAllStores() {
        return List.of();
    }
}
