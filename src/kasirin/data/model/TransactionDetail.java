package kasirin.data.model;

/// Transfer Object used by "Transaction Detail" DAO to send/receive data from client.
///
/// @author yamaym
public class TransactionDetail {
    /* Instance fields */
    private int id;
    private int productID;
    private int variationID;
    private int quantity;
    private double pricePerUnit;

    /* Constructor */
    public TransactionDetail(int productID, int variationID, int quantity, double pricePerUnit) {
        this.setProductID(productID);
        this.setVariationID(variationID);
        this.setQuantity(quantity);
        this.setPricePerUnit(pricePerUnit);
    }

    /* Getters and setters */
    public int getId() {
        return id;
    }

    /// <strong>NOTE:</strong> Only used by DAO to return ID to client.
    public void setId(int id) {
        this.id = id;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getVariationID() {
        return variationID;
    }

    public void setVariationID(int variationID) {
        this.variationID = variationID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
