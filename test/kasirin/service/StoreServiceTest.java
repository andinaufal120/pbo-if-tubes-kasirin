package kasirin.service;

import kasirin.data.model.Role;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreServiceTest { // TODO: waiting for StoreService final implementation.
    private final static StoreService storeService = new StoreService();
    private static int generatedStoreId;
    private static int generatedUserId;

    /// Statements that get executed before starting the first test.
    @BeforeAll
    static void setUp() {
        // Creating temporary user for testing purpose
        UserService userService = new UserService();
        try {
            User createdUser = userService.performRegistration("StoreServiceTest", "junittest", "junittest", "junittest", Role.ADMIN);
            generatedUserId = createdUser.getId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /// Statements that used to clean up.
    @AfterAll
    static void tearDown() {
        // Deleting the temporary user
        UserService userService = new UserService();
        userService.deleteUser(generatedUserId);
    }

    /// Test if method createStore() rejects when the <code>store</code> parameter is <code>null</code>.
    @Test
    @Order(1)
    void createStoreRejectNullStore() {
        assertEquals(-1, storeService.createStore(null, generatedUserId));
    }

    /// Test if method createStore() rejects when the new store name is <code>null</code>.
    @Test
    @Order(2)
    void createStoreRejectNullName() {
        assertEquals(-1, storeService.createStore(new Store(null, "junittest", "junittest"), generatedUserId));
    }

    /// Test if method createStore() rejects when the new store name is an empty string.
    @Test
    @Order(3)
    void createStoreRejectEmptyName() {
        assertEquals(-1, storeService.createStore(new Store(" ", "junittest", "junittest"), generatedUserId));
    }

    /// Test if method createStore() successfully inserted a new store by returning the new store id.
    @Test
    @Order(4)
    void createStoreReturnStoreId() {
        generatedStoreId = storeService.createStore(new Store("StoreServiceTest", "junittest", "junittest"), generatedUserId);
        assertTrue(generatedStoreId > 0);
    }

    /// Test if method getAllStores() returns an ArrayList object.
    @Test
    @Order(5)
    void getAllStoresReturnArrayList() {
        assertEquals(ArrayList.class, storeService.getAllStores().getClass());
    }

    /// Test if method getAllStores() returns a non-empty ArrayList object.
    /// <p><strong>Note:</strong> since the 4-th test inserted a store, it can be assumed that at least 1 store is
    /// present in the ArrayList object.</p>
    @Test
    @Order(6)
    void getAllStoresNotEmpty() {
        assertFalse(storeService.getAllStores().isEmpty()); // assumed that the new store successfully created.
    }

    /// Test if method getStore() returns <code>null</code> when the store id is invalid.
    @Test
    @Order(7)
    void getStoreByIdReturnNull() {
        assertNull(storeService.getStoreById(-999999));
    }

    /// Test if method getStore() returns a Store object when a matching store id is found in MySQL table.
    @Test
    @Order(8)
    void getStoreByIdReturnStore() {
        assertEquals(Store.class, storeService.getStoreById(generatedStoreId).getClass());
    }

    /// Test if method updateStore() rejects a null store by returning <code>false</code>.
    @Test
    @Order(9)
    void updateStoreRejectNullStore() {
        assertFalse(storeService.updateStore(generatedStoreId, null));
    }

    /// Test if method updateStore() returns true if it's able to update an existing store.
    @Test
    @Order(10)
    void updateStoreReturnTrue() {
        assertTrue(storeService.updateStore(generatedStoreId, new Store("Updated Store Service", "updatedjunittest", "updatedjunittest")));
    }

    /// Test if method deleteStore() rejects invalid store id by returning <code>false</code>.
    @Test
    @Order(11)
    void deleteStoreRejectInvalidStoreId() {
        assertFalse(storeService.deleteStore(-999999));
    }

    /// Test if method deleteStore() is able to delete a store by returning <code>true</code>.
    @Test
    @Order(12)
    void deleteStoreReturnTrue() {
        assertTrue(storeService.deleteStore(generatedStoreId));
    }
}