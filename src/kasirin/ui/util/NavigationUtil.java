package kasirin.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kasirin.data.model.User;
import kasirin.ui.controller.CreateStoreController;
import kasirin.ui.controller.MainController;

import java.io.IOException;
import java.net.URL;

/// Utility class for handling navigation between views
/// @author yamaym
public class NavigationUtil {

    /// Navigate to login view
    public static void navigateToLoginView(Stage currentStage) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/LoginView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find LoginView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        URL cssLocation = NavigationUtil.class.getResource("/kasirin/ui/css/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Login");
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
    }

    /// Navigate to register view
    public static void navigateToRegisterView(Stage currentStage) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/RegisterView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find RegisterView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        URL cssLocation = NavigationUtil.class.getResource("/kasirin/ui/css/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Registrasi");
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
    }

    /// Navigate to main view with user data
    public static void navigateToMainView(Stage currentStage, User user) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/MainView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find MainView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with user data
        MainController controller = loader.getController();
        controller.initializeWithUser(user);

        Scene scene = new Scene(root);

        URL cssLocation = NavigationUtil.class.getResource("/kasirin/ui/css/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Kasirin - Dashboard");
        currentStage.setMaximized(true);
        currentStage.centerOnScreen();
    }

    /// Open create store view as modal dialog
    public static void navigateToCreateStoreView(Stage parentStage, User user, MainController parentController) throws Exception {
        URL fxmlLocation = NavigationUtil.class.getResource("/kasirin/ui/fxml/CreateStoreView.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Cannot find CreateStoreView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Get controller and initialize with data
        CreateStoreController controller = loader.getController();
        controller.initializeWithData(user, parentController);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Buat Toko Baru");
        modalStage.setResizable(false);

        Scene scene = new Scene(root);

        URL cssLocation = NavigationUtil.class.getResource("/kasirin/ui/css/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        modalStage.setScene(scene);
        modalStage.centerOnScreen();
        modalStage.showAndWait();
    }
}
