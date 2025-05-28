package kasirin.data.model;

import java.sql.Timestamp;

/// Transfer Object used by "Transaction" DAO to send/receive data from client.
///
/// @author yamaym
public class Transaction {
    /* Instance fields */
    private int id; // primary key
    private int storeID; // foreign key
    private int userID; // foreign key
    private Timestamp timestamp;
    private double total;

    public Transaction(int storeID, int userID, Timestamp timestamp, double total) {
        this.setStoreID(storeID);
        this.setUserID(userID);
        this.setTimestamp(timestamp);
        this.setTotal(total);
    }

    public int getId() {
        return id;
    }

    /// <strong>NOTE:</strong> Only used by DAO to return ID to client.
    public void setId(int id) {
        this.id = id;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
