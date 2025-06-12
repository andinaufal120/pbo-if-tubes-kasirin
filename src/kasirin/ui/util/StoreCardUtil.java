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
        VBox card = new VBox(15);
        card.getStyleClass().add("store-card");
        card.setPadding(new Insets(20));

        // Store header with name and type badge
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Store name
        Label nameLabel = new Label(store.getName());
        nameLabel.getStyleClass().add("store-name");
        headerBox.getChildren().add(nameLabel);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Store type badge
        String typeText = store.getType() != null && !store.getType().isEmpty()
                ? store.getType() : "General";
        Label typeLabel = new Label(typeText);
        typeLabel.getStyleClass().add("store-badge");
        headerBox.getChildren().add(typeLabel);

        // Store address (truncated if too long)
        String addressText = store.getAddress();
        if (addressText != null && addressText.length() > 100) {
            addressText = addressText.substring(0, 97) + "...";
        }
        if (addressText == null || addressText.isEmpty()) {
            addressText = "No address provided";
        }
        Label addressLabel = new Label(addressText);
        addressLabel.getStyleClass().add("store-info");
        addressLabel.setWrapText(true);

        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button manageButton = new Button("Kelola Toko");
        manageButton.getStyleClass().add("manage-button");
        manageButton.setOnAction(e -> onManageAction.accept(store));

        actionBox.getChildren().add(manageButton);

        // Add all components to card
        card.getChildren().addAll(headerBox, addressLabel, actionBox);

        return card;
    }
}
