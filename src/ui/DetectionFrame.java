package ui;

import controller.PreviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DetectionFrame extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Detection.fxml"));

            Parent root = loader.load();
            PreviewController controller = loader.getController();

            Scene scene = new Scene(root);

            primaryStage.setTitle("Detection Frame");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(_ -> {
                if (controller != null) {
                    controller.stopCamera();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
