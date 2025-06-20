package kasirin.data.dao;

import kasirin.data.model.Store;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/// A unit test for MySqlStoreDAO that will attempt to mimic a CRUD lifecycle.
///
/// <p><strong>Note:</strong> it's required to run the unit test class as a whole.</p>
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySqlStoreDAOTest {
    private static final MySqlStoreDAO mySqlStoreDAO = new MySqlStoreDAO();
    private static int generatedStoreId;

    /// Test if method insertStore() is able to insert a new store to <code>store</code> table in MySQL by not
    /// returning a <code>-1</code>.
    @Test
    @Order(1)
    void insertStoreReturnStoreId() {
        generatedStoreId = mySqlStoreDAO.insertStore(new Store("MySqlStoreDAOTest", "Unit Test", "Unit Test"));
        assertNotEquals(-1, generatedStoreId);
    }

    /// Test if method findStore() is able to find the just inserted store using the generated store id.
    @Test
    @Order(2)
    void findStoreReturnNotNull() {
        assertNotNull(mySqlStoreDAO.findStore(generatedStoreId));
    }

    /// Test if method updateStore() is able to update a previously created store in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(3)
    void updateStoreAffectARow() {
        assertEquals(1, mySqlStoreDAO.updateStore(generatedStoreId,
                new Store("MySqlStoreDAOTest", "Updated Unit Test", "Updated Unit Test")));
    }

    /// Test if method deleteStore() is able to delete a previously created store in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(4)
    void deleteStoreAffectARow() {
        assertEquals(1, mySqlStoreDAO.deleteStore(generatedStoreId));
    }

//    /// Test if method findAllStores() returns an ArrayList object.
//    @Test
//    @Order(5)
//    void findAllStoresReturnArrayList() {
//        assertEquals(ArrayList.class, mySqlStoreDAO.findAllStores().getClass());
//    }
}