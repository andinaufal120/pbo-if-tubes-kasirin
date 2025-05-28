package kasirin.data.model;

/// Transfer Object used by "Product Variation" DAO to send/receive data from client.
///
/// @author yamaym
public class ProductVariation {
    /* Instance fields */
    private int id; // primary key
    private int productId; // foreign key
    private String type;
    private String value;
    private int stocks;
    private double additionalPrice;

    /* Constructor */
    public ProductVariation(int productId, String type, String value, double additionalPrice) {
        this(productId, type, value, additionalPrice, 0);
    }

    public ProductVariation(int productId, String type, String value, double additionalPrice, int stocks) {
        this.setProductId(productId);
        this.setType(type);
        this.setValue(value);
        this.setStocks(stocks);
        this.setAdditionalPrice(additionalPrice);
    }

    /* Getters and setters */
    public int getId() {
        return id;
    }

    /// <strong>NOTE:</strong> Only used by DAO to return ID to client.
    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStocks() {
        return stocks;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public int incrementStocks(int amount) {
        this.stocks += amount;
        return this.stocks;
    }

    public int decrementStocks(int amount) {
        this.stocks -= amount;
        return this.stocks;
    }

    public double getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}
