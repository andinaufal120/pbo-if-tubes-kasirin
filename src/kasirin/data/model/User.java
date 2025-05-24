package kasirin.data.model;

/// Transfer Object used by "User" DAO to send/receive data from client.
///
/// @author yamaym
public class User {
    /* Instance fields */
    private int id;
    private int storeID;
    private String username;
    private String password;
    enum role {/* what roles do we have? */}

    /* Constructor */
    public User(int storeID, String username, String password) {
        this.setStoreID(storeID);
        this.setUsername(username);
        this.setPassword(password);
    }

    /* Getters and setters */
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
