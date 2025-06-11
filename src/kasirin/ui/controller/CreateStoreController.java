package kasirin.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.StoreService;
import kasirin.ui.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

/// Controller for the Create Store View
/// Handles new store creation
/// @author yamaym
public class CreateStoreController implements Initializable {
    
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextArea addressArea;
    @FXML private Button createButton;
    @FXML private Button cancelButton;
    @FXML private Label titleLabel;
    
    private User currentUser;
    private MainController parentController;
    private StoreService storeService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storeService = new StoreService();
    }
    
    /// Initialize with user data and parent controller
    public void initializeWithData(User user, MainController parentController) {
        this.currentUser = user;
        this.parentController = parentController;
    }
    
    /// Handle create store button click
    @FXML
    private void handleCreateStore() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String address = addressArea.getText().trim();
        
        // Validate input
        if (name.isEmpty()) {
            AlertUtil.showError("Validasi Error", "Nama toko tidak boleh kosong!");
            nameField.requestFocus();
            return;
        }
        
        try {
            // Create store object
            Store newStore = new Store(name, type, address);
            
            // Save to database and link with user
            int storeId = storeService.createStore(newStore, currentUser.getId());
            
            if (storeId > 0) {
                AlertUtil.showInfo("Sukses", "Toko berhasil dibuat!");
                
                // Refresh parent controller's store list
                if (parentController != null) {
                    parentController.refreshStoreList();
                }
                
                // Close this window
                handleCancel();
                
            } else if (storeId == -2) {
                AlertUtil.showError("Error", "Toko berhasil dibuat tetapi gagal menghubungkan dengan user!");
            } else {
                AlertUtil.showError("Error", "Gagal membuat toko. Silakan coba lagi.");
            }
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    /// Handle cancel button click
    @FXML
    private void handleCancel() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
