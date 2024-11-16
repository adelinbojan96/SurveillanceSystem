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
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.image.BufferedImage;

public class PreviewController {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private boolean stopCamera = false;
    private SharedData sharedData;

    @FXML
    private ImageView webcamView;

    public void initialize() {
        sharedData = SharedData.getInstance();

        webcamView.setPreserveRatio(true);

        VideoCapture capture = sharedData.getVideoCapture();
        if (capture == null || !capture.isOpened()) {
            capture = new VideoCapture(0, Videoio.CAP_DSHOW);
            if (!capture.isOpened()) {
                System.out.println("Error: Could not open the webcam.");
                return;
            }
            sharedData.setVideoCapture(capture);
        }

        VideoCapture finalCapture = capture;
        new Thread(() -> updateFrame(finalCapture)).start();
    }


    public void updateFrame(VideoCapture capture) {
        Mat frame = new Mat();

        while (!stopCamera && capture.isOpened()) {
            if (capture.read(frame)) {
                BufferedImage image = sharedData.matToBufferedImage(frame);
                if (image != null) {
                    WritableImage finalFxImage = SwingFXUtils.toFXImage(image, null);
                    Platform.runLater(() -> {
                        if (!stopCamera) {
                            webcamView.setImage(finalFxImage);
                            sharedData.setFxImage(finalFxImage);
                        }
                    });
                }
            } else {
                System.out.println("Frame not read from capture.");
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

    public void navigateToDetection(ActionEvent event) {
        try {
            stopCamera();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Detection.fxml"));
            Parent detectionRoot = loader.load();

            DetectionController detectionController = loader.getController();
            detectionController.setSharedData(sharedData);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene detectionScene = new Scene(detectionRoot);
            currentStage.setScene(detectionScene);
            currentStage.setTitle("Detection Frame");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }
}