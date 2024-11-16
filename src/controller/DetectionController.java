package controller;

import camera_share.SharedData;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;

public class DetectionController {
    private SharedData sharedData;

    @FXML
    private ImageView detectionWebcamView;

    private boolean stopCamera = false;

    @FXML
    public void initialize() {
        sharedData = SharedData.getInstance();

        if (detectionWebcamView == null) {
            System.out.println("Error: detectionWebcamView is null. Check FXML fx:id.");
            return;
        }

        detectionWebcamView.setPreserveRatio(true);

        VideoCapture capture = sharedData.getVideoCapture();
        if (capture == null || !capture.isOpened()) {
            System.out.println("Error: Camera not initialized.");
            return;
        }

        new Thread(() -> updateFrame(capture)).start();
    }

    private void updateFrame(VideoCapture capture) {
        Mat frame = new Mat();

        while (!stopCamera && capture.isOpened()) {
            if (capture.read(frame)) {
                BufferedImage image = sharedData.matToBufferedImage(frame);
                if (image != null) {
                    Platform.runLater(() -> {
                        if (!stopCamera) {
                            detectionWebcamView.setImage(SwingFXUtils.toFXImage(image, null));
                        }
                    });
                }
            }

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopCamera() {
        stopCamera = true;
    }

    public void navigateToPreview(ActionEvent event) {
        stopCamera();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Preview.fxml"));
            Parent root = loader.load();

            PreviewController previewController = loader.getController();
            previewController.setSharedData(sharedData);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene previewScene = new Scene(root);
            currentStage.setScene(previewScene);
            currentStage.setTitle("Preview Frame");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }
}
