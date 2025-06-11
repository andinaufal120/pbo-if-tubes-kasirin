package kasirin.ui.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kasirin.data.model.Store;

import java.util.function.Consumer;

/// Utility class for creating store card components
/// @author yamaym
public class StoreCardUtil {
    
    /// Create a store card component
    public static VBox createStoreCard(Store store, Consumer<Store> onManageAction) {
        VBox card = new VBox(10);
        card.getStyleClass().add("store-card");
        card.setPadding(new Insets(20));
        
        // Store name
        Label nameLabel = new Label(store.getName());
        nameLabel.getStyleClass().add("store-name");
        
        // Store type
        String typeText = store.getType() != null && !store.getType().isEmpty() 
            ? store.getType() : "N/A";
        Label typeLabel = new Label("Tipe: " + typeText);
        typeLabel.getStyleClass().add("store-info");
        
        // Store address (truncated if too long)
        String addressText = store.getAddress();
        if (addressText != null && addressText.length() > 100) {
            addressText = addressText.substring(0, 97) + "...";
        }
        if (addressText == null || addressText.isEmpty()) {
            addressText = "N/A";
        }
        Label addressLabel = new Label("Alamat: " + addressText);
        addressLabel.getStyleClass().add("store-info");
        
        // Info container
        VBox infoContainer = new VBox(5);
        infoContainer.getChildren().addAll(nameLabel, typeLabel, addressLabel);
        
        // Manage button
        Button manageButton = new Button("Kelola");
        manageButton.getStyleClass().add("manage-button");
        manageButton.setOnAction(e -> onManageAction.accept(store));
        
        // Main container with info and button
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);
        mainContainer.getChildren().addAll(infoContainer, manageButton);
        
        card.getChildren().add(mainContainer);
        
        return card;
    }
}
