package kasirin.data.model;

/// Transfer Object used by "Store" DAO to send/receive data from client.
///
/// @author yamaym
public class Store {
    /* Instance fields */
    private int id; // primary key
    private String name;
    private String type;
    private String address;

    /* Constructor */
    public Store(String name, String type, String address) {
        this.setName(name);
        this.setType(type);
        this.setAddress(address);
    }

    /* Getters and setters */
    public int getId() {
        return id;
    }

    /// <strong>NOTE:</strong> Only used by DAO to return ID to client.
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
