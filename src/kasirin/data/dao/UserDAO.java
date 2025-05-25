package kasirin.data.dao;

import kasirin.data.model.User;

/// Provides CRUD methods for "User" entity in datasource.
///
/// @author yamaym
public interface UserDAO {
    /// Inserts a new user to datasource.
    ///
    /// @param user "User" transfer object
    /// @return newly created user ID or a {@code -1} on error
    public int insertUser(User user);

    /// Finds a user based on criteria.
    ///
    /// @param id user ID to search
    /// @return "Transaction" transfer object
    public int findUser(int id);

    /// Updates an existing user in datasource.
    ///
    /// @param id   user ID to update
    /// @param user "User" transfer object with updated fields
    /// @return {@code true} on success, {@code false} on error
    public int updateUser(int id, User user);

    /// Deletes an existing user in datasource.
    ///
    /// @param id user ID to be deleted
    /// @return {@code true} on success, {@code false} on error
    public int deleteUser(int id);
}
