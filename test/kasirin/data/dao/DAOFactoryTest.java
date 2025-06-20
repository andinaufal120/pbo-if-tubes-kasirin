package kasirin.data.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// A unit test for DAOFactory class.
///
/// @author yamaym
class DAOFactoryTest {

    /// Test if the static method getDAOFactory() is not returning <code>null</code>.
    @Test
    void getDAOFactoryNotNull() {
        assertNotNull(DAOFactory.getDAOFactory(DAOFactory.MYSQL));
        assertNotNull(DAOFactory.getDAOFactory(1));
    }

    /// Test if the static method getDAOFactory() throws an <code>IllegalArgumentException</code> when
    /// given an invalid factoryType in the parameter.
    ///
    /// <p><strong>Note:</strong> since there is only one option, the valid factoryType is only <code>1</code>.</p>
    @Test
    void getDAOFactoryThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DAOFactory.getDAOFactory(2));
    }
}