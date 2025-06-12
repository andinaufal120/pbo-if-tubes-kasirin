package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Transactions/POS View
 * Handles point of sale operations and transaction processing
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

    @FXML private TableView<Object> cartTable;
    @FXML private TableColumn<Object, String> cartProductCol;
    @FXML private TableColumn<Object, String> cartVariationCol;
    @FXML private TableColumn<Object, String> cartQtyCol;
    @FXML private TableColumn<Object, String> cartPriceCol;
    @FXML private TableColumn<Object, String> cartTotalCol;
    @FXML private TableColumn<Object, String> cartActionsCol;

    @FXML private Button clearCartBtn;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label grandTotalLabel;
    @FXML private TextField paymentAmountField;
    @FXML private Label changeLabel;
    @FXML private Button checkoutBtn;

    private User currentUser;
    private Store currentStore;
    private double subtotal = 0.0;
    private double taxRate = 0.10; // 10% tax

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupDateTime();
            setupTables();
            setupFilters();
            setupPaymentCalculation();

            System.out.println("TransactionsController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing TransactionsController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with user and store data
     */
    public void initializeWithData(User user, Store store) {
        try {
            this.currentUser = user;
            this.currentStore = store;

            cashierLabel.setText("Cashier: " + user.getName());
            loadProducts();

            System.out.println("Transactions loaded for store: " + store.getName());
        } catch (Exception e) {
            System.err.println("Error initializing transactions with data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup date and time display
     */
    private void setupDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - HH:mm");
        dateTimeLabel.setText(now.format(formatter));
    }

    /**
     * Setup tables
     */
    private void setupTables() {
        // Setup cart table columns
        cartProductCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Sample Product"));
        cartVariationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Large"));
        cartQtyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("2"));
        cartPriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 25,000"));
        cartTotalCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Rp 50,000"));
        cartActionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("🗑️"));
    }

    /**
     * Setup filters
     */
    private void setupFilters() {
        productCategoryFilter.getItems().addAll("All Categories", "Food", "Beverage", "Snacks", "Others");
        productCategoryFilter.setValue("All Categories");
    }

    /**
     * Setup payment calculation
     */
    private void setupPaymentCalculation() {
        paymentAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateChange();
        });
    }

    /**
     * Load products for POS
     */
    private void loadProducts() {
        try {
            // TODO: Replace with actual service call
            // productService.getProductsByStore(currentStore.getId());

            System.out.println("Products loaded for POS");
        } catch (Exception e) {
            System.err.println("Error loading products for POS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calculate totals
     */
    private void calculateTotals() {
        double tax = subtotal * taxRate;
        double grandTotal = subtotal + tax;

        subtotalLabel.setText(String.format("Rp %,.0f", subtotal));
        taxLabel.setText(String.format("Rp %,.0f", tax));
        grandTotalLabel.setText(String.format("Rp %,.0f", grandTotal));

        calculateChange();
    }

    /**
     * Calculate change amount
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
                    changeLabel.setStyle("-fx-text-fill: #059669;");
                    checkoutBtn.setDisable(false);
                } else {
                    changeLabel.setText("Insufficient payment");
                    changeLabel.setStyle("-fx-text-fill: #ef4444;");
                    checkoutBtn.setDisable(true);
                }
            } else {
                changeLabel.setText("Change: Rp 0");
                checkoutBtn.setDisable(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid amount");
            checkoutBtn.setDisable(true);
        }
    }

    @FXML
    private void searchProducts() {
        String searchTerm = productSearchField.getText().trim();
        String category = productCategoryFilter.getValue();

        // TODO: Implement product search
        AlertUtil.showInfo("Search", "Searching for: " + searchTerm + " in category: " + category);
    }

    @FXML
    private void clearCart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Are you sure you want to clear the cart?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cartTable.getItems().clear();
                subtotal = 0.0;
                calculateTotals();
                AlertUtil.showInfo("Cart Cleared", "Shopping cart has been cleared.");
            }
        });
    }

    @FXML
    private void processPayment() {
        try {
            // TODO: Implement payment processing
            // 1. Validate cart items
            // 2. Create transaction record
            // 3. Update product stocks
            // 4. Print receipt

            AlertUtil.showInfo("Payment Processed", "Transaction completed successfully!");

            // Clear cart after successful payment
            cartTable.getItems().clear();
            subtotal = 0.0;
            calculateTotals();
            paymentAmountField.clear();

        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Payment Error", "Failed to process payment: " + e.getMessage());
        }
    }
}
