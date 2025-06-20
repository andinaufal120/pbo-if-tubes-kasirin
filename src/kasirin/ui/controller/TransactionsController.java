package kasirin.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import kasirin.data.model.Product;
import kasirin.data.model.ProductVariation;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.ProductService;
import kasirin.service.TransactionService;
import kasirin.service.TransactionService.TransactionItem;
import kasirin.service.TransactionService.TransactionResult;
import kasirin.service.TransactionService.TransactionException;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Enhanced controller for the Transactions/POS View
 * Handles point of sale operations with improved error handling and validation
 *
 * @author yamaym
 */
public class TransactionsController implements Initializable {

    @FXML private Label cashierLabel;
    @FXML private Label dateTimeLabel;

    @FXML private TextField productSearchField;
    @FXML private Button searchProductBtn;
    @FXML private ComboBox<String> productCategoryFilter;
    @FXML private GridPane productsGrid;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartProductCol;
    @FXML private TableColumn<CartItem, String> cartVariationCol;
    @FXML private TableColumn<CartItem, Integer> cartQtyCol;
    @FXML private TableColumn<CartItem, String> cartPriceCol;
    @FXML private TableColumn<CartItem, String> cartTotalCol;
    @FXML private TableColumn<CartItem, Void> cartActionsCol;

    @FXML private Button clearCartBtn;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label grandTotalLabel;
    @FXML private TextField paymentAmountField;
    @FXML private Label changeLabel;
    @FXML private Button checkoutBtn;

    private User currentUser;
    private Store currentStore;
    private ProductService productService;
    private TransactionService transactionService;
    private ObservableList<CartItem> cartItems;
    private List<Product> availableProducts;
    private double subtotal = 0.0;
    private double taxRate = 0.10; // 10% tax

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            productService = new ProductService();
            transactionService = new TransactionService();
            cartItems = FXCollections.observableArrayList();

            setupDateTime();
            setupTables();
            setupFilters();
            setupPaymentCalculation();

            System.out.println("TransactionsController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing TransactionsController: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Initialization Error", "Failed to initialize POS system: " + e.getMessage());
        }
    }

    /**
     * Initialize with user and store data
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            System.out.println("Initializing with User: " + user.getName() + " (ID: " + user.getId() + ")");
            System.out.println("Initializing with Store: " + store.getName() + " (ID: " + store.getId() + ")");

            cashierLabel.setText("Cashier: " + user.getName());
            loadProducts();

            System.out.println("Transactions loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing transactions with data: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Data Loading Error", "Failed to load store data: " + e.getMessage());
        }
    }

    /**
     * Setup date and time display with auto-refresh
     */
    private void setupDateTime() {
        updateDateTime();

        // Update time every minute
        Timeline timeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.minutes(1), e -> updateDateTime())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - HH:mm");
        dateTimeLabel.setText(now.format(formatter));
    }

    /**
     * Setup tables with enhanced formatting
     */
    private void setupTables() {
        // Setup cart table columns
        cartProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartVariationCol.setCellValueFactory(new PropertyValueFactory<>("variationInfo"));
        cartQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        cartPriceCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("Rp %,.0f", cellData.getValue().getPricePerUnit())
                )
        );

        cartTotalCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("Rp %,.0f", cellData.getValue().getTotalPrice())
                )
        );

        // Setup actions column with edit and remove buttons
        cartActionsCol.setCellFactory(col -> {
            TableCell<CartItem, Void> cell = new TableCell<CartItem, Void>() {
                private final Button editBtn = new Button("✏️");
                private final Button removeBtn = new Button("🗑️");

                {
                    editBtn.getStyleClass().add("small-button");
                    removeBtn.getStyleClass().add("danger-button");

                    editBtn.setOnAction(event -> {
                        CartItem item = getTableView().getItems().get(getIndex());
                        editCartItem(item);
                    });

                    removeBtn.setOnAction(event -> {
                        CartItem item = getTableView().getItems().get(getIndex());
                        removeFromCart(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editBtn, removeBtn);
                        setGraphic(buttons);
                    }
                }
            };
            return cell;
        });

        cartTable.setItems(cartItems);

        // Add listener for cart changes
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) change -> {
            calculateTotals();
        });
    }

    /**
     * Setup filters with dynamic category loading
     */
    private void setupFilters() {
        productCategoryFilter.getItems().add("All Categories");
        productCategoryFilter.setValue("All Categories");

        // Categories will be populated when products are loaded
    }

    /**
     * Setup payment calculation with input validation
     */
    private void setupPaymentCalculation() {
        // Only allow numeric input
        paymentAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                paymentAmountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            calculateChange();
        });

        // Format input on focus lost
        paymentAmountField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Lost focus
                formatPaymentInput();
            }
        });
    }

    private void formatPaymentInput() {
        String text = paymentAmountField.getText().replaceAll("[^\\d]", "");
        if (!text.isEmpty()) {
            try {
                double amount = Double.parseDouble(text);
                paymentAmountField.setText(String.format("%.0f", amount));
            } catch (NumberFormatException e) {
                paymentAmountField.clear();
            }
        }
    }

    /**
     * Load products for POS with enhanced error handling
     */
    private void loadProducts() {
        try {
            List<Product> allProducts = productService.getAllProducts();
            availableProducts = allProducts.stream()
                    .filter(p -> p.getStoreID() == currentStore.getId())
                    .toList();

            // Update category filter
            updateCategoryFilter();

            displayProducts(availableProducts);

            System.out.println("Loaded " + availableProducts.size() + " products for POS");
        } catch (Exception e) {
            System.err.println("Error loading products for POS: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Product Loading Error", "Failed to load products: " + e.getMessage());
        }
    }

    /**
     * Update category filter with available categories
     */
    private void updateCategoryFilter() {
        List<String> categories = availableProducts.stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .toList();

        productCategoryFilter.getItems().clear();
        productCategoryFilter.getItems().add("All Categories");
        productCategoryFilter.getItems().addAll(categories);
        productCategoryFilter.setValue("All Categories");
    }

    /**
     * Display products in grid with improved layout
     */
    private void displayProducts(List<Product> products) {
        productsGrid.getChildren().clear();

        if (products.isEmpty()) {
            Label emptyLabel = new Label("No products available");
            emptyLabel.getStyleClass().add("empty-message");
            productsGrid.add(emptyLabel, 0, 0);
            return;
        }

        int col = 0;
        int row = 0;
        int maxCols = 4;

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productsGrid.add(productCard, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Create enhanced product card for display
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(150);
        card.setPrefHeight(120);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(130);

        Label categoryLabel = new Label(product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label priceLabel = new Label(String.format("Rp %,.0f", product.getBasePrice()));
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #059669;");

        card.getChildren().addAll(nameLabel, categoryLabel, priceLabel);

        // Add click handler with visual feedback
        card.setOnMouseClicked(event -> {
            card.setStyle("-fx-background-color: #e3f2fd;");
            showProductVariations(product);

            // Reset style after short delay
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> card.setStyle(""));
            pause.play();
        });

        return card;
    }

    /**
     * Show product variations dialog or add directly if no variations
     */
    private void showProductVariations(Product product) {
        try {
            // Show quantity input dialog
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Add to Cart");
            dialog.setHeaderText("Add " + product.getName() + " to cart");
            dialog.setContentText("Quantity:");

            dialog.showAndWait().ifPresent(result -> {
                try {
                    int quantity = Integer.parseInt(result);
                    if (quantity > 0) {
                        addToCart(product, null, quantity);
                    } else {
                        AlertUtil.showWarning("Invalid Quantity", "Quantity must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    AlertUtil.showError("Invalid Input", "Please enter a valid number");
                }
            });
        } catch (Exception e) {
            AlertUtil.showError("Product Selection Error", "Failed to add product to cart: " + e.getMessage());
        }
    }

    /**
     * Add item to cart with validation
     */
    private void addToCart(Product product, ProductVariation variation, int quantity) {
        try {
            System.out.println("Adding to cart: " + product.getName() + " x" + quantity);

            if (quantity <= 0) {
                AlertUtil.showWarning("Invalid Quantity", "Quantity must be greater than 0");
                return;
            }

            double pricePerUnit = product.getBasePrice();
            String variationInfo = "Default";
            int variationId = 0; // Default variation

            if (variation != null) {
                pricePerUnit += variation.getAdditionalPrice();
                variationInfo = variation.getType() + ": " + variation.getValue();
                variationId = variation.getId();
            }

            // Check if item already exists in cart
            CartItem existingItem = null;
            for (CartItem item : cartItems) {
                if (item.getProductId() == product.getId() && item.getVariationId() == variationId) {
                    existingItem = item;
                    break;
                }
            }

            if (existingItem != null) {
                // Update quantity of existing item
                int newQuantity = existingItem.getQuantity() + quantity;
                System.out.println("Updating existing cart item quantity: " + existingItem.getQuantity() + " -> " + newQuantity);
                existingItem.setQuantity(newQuantity);
            } else {
                // Add new item to cart
                CartItem newItem = new CartItem(
                        product.getId(),
                        variationId,
                        product.getName(),
                        variationInfo,
                        quantity,
                        pricePerUnit
                );
                cartItems.add(newItem);
                System.out.println("Added new cart item: " + newItem);
            }

            cartTable.refresh();
            System.out.println("Cart updated successfully. Total items: " + cartItems.size());

        } catch (Exception e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Cart Error", "Failed to add item to cart: " + e.getMessage());
        }
    }

    /**
     * Edit cart item quantity
     */
    private void editCartItem(CartItem item) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantity()));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Edit quantity for " + item.getProductName());
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                int newQuantity = Integer.parseInt(result);
                if (newQuantity <= 0) {
                    AlertUtil.showWarning("Invalid Quantity", "Quantity must be greater than 0");
                    return;
                }

                System.out.println("Updating cart item quantity: " + item.getQuantity() + " -> " + newQuantity);
                item.setQuantity(newQuantity);
                cartTable.refresh();

            } catch (NumberFormatException e) {
                AlertUtil.showError("Invalid Input", "Please enter a valid number");
            } catch (Exception e) {
                System.err.println("Error updating cart item: " + e.getMessage());
                AlertUtil.showError("Update Error", "Failed to update quantity: " + e.getMessage());
            }
        });
    }

    /**
     * Remove item from cart with confirmation
     */
    private void removeFromCart(CartItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Item");
        alert.setHeaderText("Remove item from cart?");
        alert.setContentText(item.getProductName() + " (" + item.getVariationInfo() + ") x" + item.getQuantity());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cartItems.remove(item);
                System.out.println("Removed from cart: " + item.getProductName());
            }
        });
    }

    /**
     * Calculate totals with proper formatting
     */
    private void calculateTotals() {
        subtotal = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        double tax = subtotal * taxRate;
        double grandTotal = subtotal + tax;

        subtotalLabel.setText(String.format("Rp %,.0f", subtotal));
        taxLabel.setText(String.format("Rp %,.0f", tax));
        grandTotalLabel.setText(String.format("Rp %,.0f", grandTotal));

        calculateChange();
    }

    /**
     * Calculate change amount with validation
     */
    private void calculateChange() {
        try {
            String paymentText = paymentAmountField.getText().replaceAll("[^0-9]", "");
            if (!paymentText.isEmpty()) {
                double paymentAmount = Double.parseDouble(paymentText);
                double tax = subtotal * taxRate;
                double grandTotal = subtotal + tax;
                double change = paymentAmount - grandTotal;

                if (change >= 0) {
                    changeLabel.setText(String.format("Change: Rp %,.0f", change));
                    changeLabel.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    checkoutBtn.setDisable(cartItems.isEmpty());
                } else {
                    changeLabel.setText(String.format("Short: Rp %,.0f", Math.abs(change)));
                    changeLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    checkoutBtn.setDisable(true);
                }
            } else {
                changeLabel.setText("Change: Rp 0");
                changeLabel.setStyle("-fx-text-fill: #666;");
                checkoutBtn.setDisable(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid amount");
            changeLabel.setStyle("-fx-text-fill: #ef4444;");
            checkoutBtn.setDisable(true);
        }
    }

    @FXML
    private void searchProducts() {
        String searchTerm = productSearchField.getText().trim().toLowerCase();
        String category = productCategoryFilter.getValue();

        List<Product> filteredProducts = availableProducts.stream()
                .filter(product -> {
                    boolean matchesSearch = searchTerm.isEmpty() ||
                            product.getName().toLowerCase().contains(searchTerm) ||
                            product.getCategory().toLowerCase().contains(searchTerm);

                    boolean matchesCategory = "All Categories".equals(category) ||
                            product.getCategory().equals(category);

                    return matchesSearch && matchesCategory;
                })
                .toList();

        displayProducts(filteredProducts);
    }

    @FXML
    private void clearCart() {
        if (cartItems.isEmpty()) {
            AlertUtil.showInfo("Empty Cart", "Cart is already empty.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Are you sure you want to clear the cart?");
        alert.setContentText("This will remove all " + cartItems.size() + " items from the cart.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cartItems.clear();
                paymentAmountField.clear();
                AlertUtil.showInfo("Cart Cleared", "Shopping cart has been cleared.");
            }
        });
    }

    @FXML
    private void processPayment() {
        System.out.println("=== Processing Payment ===");

        try {
            // Validation
            if (cartItems.isEmpty()) {
                AlertUtil.showWarning("Empty Cart", "Please add items to cart before checkout.");
                return;
            }

            String paymentText = paymentAmountField.getText().replaceAll("[^0-9]", "");
            if (paymentText.isEmpty()) {
                AlertUtil.showError("Invalid Payment", "Please enter payment amount.");
                paymentAmountField.requestFocus();
                return;
            }

            double paymentAmount = Double.parseDouble(paymentText);
            System.out.println("Payment amount: Rp " + String.format("%,.2f", paymentAmount));

            // Show processing indicator
            checkoutBtn.setText("Processing...");
            checkoutBtn.setDisable(true);

            // Convert cart items to transaction items with detailed logging
            List<TransactionItem> transactionItems = new ArrayList<>();
            System.out.println("Converting " + cartItems.size() + " cart items to transaction items:");

            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);
                System.out.println("Cart item " + (i + 1) + ": " + cartItem);

                TransactionItem transactionItem = new TransactionItem(
                        cartItem.getProductId(),
                        cartItem.getVariationId(),
                        cartItem.getQuantity(),
                        cartItem.getPricePerUnit()
                );
                transactionItems.add(transactionItem);
                System.out.println("Created transaction item: " + transactionItem);
            }

            // Validate user and store data
            if (currentUser == null || currentUser.getId() <= 0) {
                throw new RuntimeException("Invalid user data: " + currentUser);
            }

            if (currentStore == null || currentStore.getId() <= 0) {
                throw new RuntimeException("Invalid store data: " + currentStore);
            }

            System.out.println("Processing transaction with:");
            System.out.println("- User: " + currentUser.getName() + " (ID: " + currentUser.getId() + ")");
            System.out.println("- Store: " + currentStore.getName() + " (ID: " + currentStore.getId() + ")");
            System.out.println("- Items: " + transactionItems.size());
            System.out.println("- Payment: Rp " + String.format("%,.2f", paymentAmount));

            // Process transaction
            TransactionResult result = transactionService.processCompleteTransaction(
                    transactionItems, currentUser, currentStore, paymentAmount);

            // Show success message
            String successMessage = String.format(
                    "Transaction completed successfully!\n\n" +
                            "Transaction ID: %d\n" +
                            "Total: Rp %,.0f\n" +
                            "Payment: Rp %,.0f\n" +
                            "Change: Rp %,.0f\n" +
                            "Items: %d\n" +
                            "Time: %s",
                    result.getTransactionId(),
                    result.getTotalAmount(),
                    result.getPaymentAmount(),
                    result.getChangeAmount(),
                    result.getTransactionDetails().size(),
                    result.getTransactionTime().toString()
            );

            AlertUtil.showInfo("Payment Processed", successMessage);

            // Clear cart after successful payment
            cartItems.clear();
            paymentAmountField.clear();

            System.out.println("Transaction completed successfully: " + result);
            System.out.println("=== Payment Processing Complete ===");

        } catch (TransactionException e) {
            System.err.println("Transaction error: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Transaction Failed", e.getMessage());
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Payment", "Please enter a valid payment amount.");
            paymentAmountField.requestFocus();
        } catch (Exception e) {
            System.err.println("Unexpected error processing payment: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Payment Error", "An unexpected error occurred: " + e.getMessage());
        } finally {
            // Reset checkout button
            checkoutBtn.setText("💳 Process Payment");
            checkoutBtn.setDisable(false);
            calculateChange(); // Recalculate to set proper state
        }
    }

    /**
     * Enhanced CartItem class with validation
     */
    public static class CartItem {
        private int productId;
        private int variationId;
        private String productName;
        private String variationInfo;
        private int quantity;
        private double pricePerUnit;

        public CartItem(int productId, int variationId, String productName, String variationInfo,
                        int quantity, double pricePerUnit) {
            this.productId = productId;
            this.variationId = variationId;
            this.productName = productName;
            this.variationInfo = variationInfo;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
        }

        // Getters
        public int getProductId() { return productId; }
        public int getVariationId() { return variationId; }
        public String getProductName() { return productName; }
        public String getVariationInfo() { return variationInfo; }
        public int getQuantity() { return quantity; }
        public double getPricePerUnit() { return pricePerUnit; }

        // Setters with validation
        public void setQuantity(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0: " + quantity);
            }

            System.out.println("Updating quantity for " + productName + ": " + this.quantity + " -> " + quantity);
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return quantity * pricePerUnit;
        }

        @Override
        public String toString() {
            return String.format("CartItem{productId=%d, variationId=%d, name='%s', variation='%s', qty=%d, price=%.2f, total=%.2f}",
                    productId, variationId, productName, variationInfo, quantity, pricePerUnit, getTotalPrice());
        }
    }
}
