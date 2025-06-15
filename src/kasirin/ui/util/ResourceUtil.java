package kasirin.ui.util;

import java.net.URL;

/// Utility class for handling resource loading
/// @author yamaym
public class ResourceUtil {

    /// Get FXML resource URL
    public static URL getFXMLResource(String fxmlPath) {
        URL resource = ResourceUtil.class.getResource(fxmlPath);
        if (resource == null) {
            System.err.println("FXML resource not found: " + fxmlPath);
            // Try alternative paths
            String[] alternativePaths = {
                    "/kasirin/ui/fxml/" + fxmlPath,
                    "/ui/fxml/" + fxmlPath,
                    fxmlPath
            };

            for (String altPath : alternativePaths) {
                resource = ResourceUtil.class.getResource(altPath);
                if (resource != null) {
                    System.out.println("Found FXML at alternative path: " + altPath);
                    break;
                }
            }
        }
        return resource;
    }

    /// Get CSS resource URL
    public static URL getCSSResource(String cssPath) {
        URL resource = ResourceUtil.class.getResource(cssPath);
        if (resource == null) {
            System.err.println("CSS resource not found: " + cssPath);
            // Try alternative paths
            String[] alternativePaths = {
                    "/kasirin/ui/css/" + cssPath,
                    "/ui/css/" + cssPath,
                    cssPath
            };

            for (String altPath : alternativePaths) {
                resource = ResourceUtil.class.getResource(altPath);
                if (resource != null) {
                    System.out.println("Found CSS at alternative path: " + altPath);
                    break;
                }
            }
        }
        return resource;
    }

    /// Debug method to list available resources
    public static void debugResources() {
        System.out.println("=== Resource Debug Information ===");

        String[] resourcePaths = {
                "/kasirin/ui/fxml/LoginView.fxml",
                "/kasirin/ui/fxml/RegisterView.fxml",
                "/kasirin/ui/fxml/MainView.fxml",
                "/kasirin/ui/fxml/CreateStoreView.fxml",
                "/kasirin/ui/css/styles.css"
        };

        for (String path : resourcePaths) {
            URL resource = ResourceUtil.class.getResource(path);
            System.out.println(path + " -> " + (resource != null ? "FOUND" : "NOT FOUND"));
        }

        System.out.println("=== End Resource Debug ===");
    }
}
