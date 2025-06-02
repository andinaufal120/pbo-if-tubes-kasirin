package kasirin.data.dao;

import kasirin.data.model.User;

import java.util.List;

public class MySqlUserDAO implements UserDAO {
    @Override
    public int insertUser(User user) {
        return 0;
    }

    @Override
    public User findUser(int id) {
        return null;
    }

    @Override
    public int updateUser(int id, User user) {
        return 0;
    }

    @Override
    public int deleteUser(int id) {
        return 0;
    }

    @Override
    public List<User> findAllUsers() {
        return List.of();
    }
}
