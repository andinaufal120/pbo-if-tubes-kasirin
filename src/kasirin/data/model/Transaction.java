package kasirin.data.model;

import java.sql.Timestamp;

/// Transfer Object used by "Transaction" DAO to send/receive data from client.
///
/// @author yamaym
public class Transaction {
    /* Instance fields */
    private int id; // primary key
    private int storeID; // foreign key
    private String action;
    private String description;
    private double total;
    private Timestamp timestamp; // TODO: data type must be checked again.

    /* Constructor */
    public Transaction(int storeID, String action, String description, double total, Timestamp timestamp) {
        this.setStoreID(storeID);
        this.setAction(action);
        this.setDescription(description);
        this.setTotal(total);
        this.setTimestamp(timestamp);
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
