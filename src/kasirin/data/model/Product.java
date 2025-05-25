package kasirin.data.model;

/// Transfer Object used by "Product" DAO to send/receive data from client.
///
/// @author yamaym
public class Product {
    /* Instance fields */
    private int id; // primary key
    private String category; // foreign key
    private int storeID; // foreign key
    private String name;
    private double basePrice;
    private String description;
    private String imageURL;

    /* Constructors */
    public Product(String category, int storeID, String name, double basePrice) {
        this.setCategory(category);
        this.setStoreID(storeID);
        this.setName(name);
        this.setBasePrice(basePrice);
    }

    public Product(String category, int storeID, String name, double basePrice, String description, String imageURL) {
        this.setCategory(category);
        this.setStoreID(storeID);
        this.setName(name);
        this.setBasePrice(basePrice);
        this.setDescription(description);
        this.setImageURL(imageURL);
    }

    /* Getters and setters */
    public int getId() {
        return id;
    }

    /// NOTE: Attempt to change Product ID should not be applied to datasource.
    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
