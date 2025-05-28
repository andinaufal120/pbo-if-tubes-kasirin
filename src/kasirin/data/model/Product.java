package kasirin.data.model;

/// Transfer Object used by "Product" DAO to send/receive data from client.
///
/// @author yamaym
public class Product {
    /* Instance fields */
    private int id; // primary key
    private int storeID; // foreign key
    private String name;
    private String category;
    private double basePrice;
    private String description;
    private String imageURL;

    public Product(String name, int storeID, String category, double basePrice) {
        this.setName(name);
        this.setStoreID(storeID);
        this.setCategory(category);
        this.setBasePrice(basePrice);
    }

    public Product(String name, int storeID, String category, double basePrice, String description, String imageURL) {
        this.setName(name);
        this.setStoreID(storeID);
        this.setCategory(category);
        this.setBasePrice(basePrice);
        this.setDescription(description);
        this.setImageURL(imageURL);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
