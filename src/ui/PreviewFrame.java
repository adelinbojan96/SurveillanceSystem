package ui;

import controller.SystemController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PreviewFrame extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Preview.fxml"));

            Parent root = loader.load();
            SystemController controller = loader.getController();

            Scene scene = new Scene(root);

            primaryStage.setTitle("Preview Frame");
            primaryStage.setScene(scene);
            primaryStage.show();

            if (controller != null) {
                controller.initialize();
            } else {
                System.out.println("Controller is null. Check if FXML is correctly loaded.");
            }

            primaryStage.setOnCloseRequest(_ -> {
                if (controller != null) {
                    controller.stopCamera();
                }
                System.out.println("Application is closing.");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
