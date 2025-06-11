package kasirin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/// Main JavaFX Application entry point for Kasirin POS System
/// @author yamaym
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load the login view as the initial screen
            URL fxmlLocation = getClass().getResource("/kasirin/ui/fxml/LoginView.fxml");
            if (fxmlLocation == null) {
                System.err.println("Cannot find LoginView.fxml");
                System.err.println("Make sure the FXML file is in src/kasirin/ui/fxml/LoginView.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Add CSS styling
            URL cssLocation = getClass().getResource("/kasirin/ui/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            } else {
                System.out.println("Warning: styles.css not found");
            }

            primaryStage.setTitle("Kasirin - Point of Sale System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
