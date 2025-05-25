package kasirin.data.dao;

import kasirin.data.model.User;

/// Provides CRUD methods for "User" entity in datasource.
///
/// @author yamaym
public interface UserDAO {
    public int insertUser(User user);
    public int findUser(int id);
    public int updateUser(int id, User user);
    public int deleteUser(int id);
}
