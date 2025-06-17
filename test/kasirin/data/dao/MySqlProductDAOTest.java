package kasirin.data.dao;

import kasirin.data.model.Product;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/// A unit test for MySqlProductDAO that will attempt to mimic a CRUD lifecycle.
///
/// <p><strong>Note:</strong> it's required to run the unit test class as a whole.</p>
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySqlProductDAOTest {
    private static final MySqlProductDAO mySqlProductDAO = new MySqlProductDAO();
    private static int generatedProductId;

    /// Test if method insertProduct() is able to insert a new product to <code>product</code> table in MySQL by not
    /// returning a <code>-1</code>.
    @Test
    @Order(1)
    void insertProductReturnProductId() {
        generatedProductId = mySqlProductDAO.insertProduct(new Product("MySqlProductDAOTest", 1, "Unit Test", 999999));
        assertNotEquals(-1, generatedProductId);
    }

    /// Test if method findProduct() is able to find the just inserted product using the generated product id.
    @Test
    @Order(2)
    void findProductReturnNotNull() {
        assertNotNull(mySqlProductDAO.findProduct(generatedProductId));
    }

    /// Test if method updateProduct() is able to update a previously created product in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(3)
    void updateProductAffectARow() {
        assertEquals(1, mySqlProductDAO.updateProduct(generatedProductId,
                new Product("MySqlProductDAOTest", 1, "Unit Test", 888888)));
    }

    /// Test if method deleteProduct() is able to delete a previously created product in the MySQL table by reporting
    /// that it has affected exactly 1 row.
    @Test
    @Order(4)
    void deleteProductAffectARow() {
        assertEquals(1, mySqlProductDAO.deleteProduct(generatedProductId));
    }

    /// Test if method findAllProducts() returns an ArrayList object.
    @Test
    @Order(5)
    void findAllProductsReturnArrayList() {
        assertEquals(ArrayList.class, mySqlProductDAO.findAllProducts().getClass());
    }
}