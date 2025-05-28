package kasirin.data.model;

/// Transfer Object used by "User" DAO to send/receive data from client.
///
/// @author yamaym
public class User {
    /* Instance fields */
    private int id; // primary key
    private int storeID; // foreign key
    private String name;
    private String username;
    private String password;
    enum role {/* what roles do we have? */}

    public User(int storeID, String name, String username, String password) {
        this.setStoreID(storeID);
        this.setName(name);
        this.setUsername(username);
        this.setPassword(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
