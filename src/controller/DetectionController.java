package controller;

import alert.Timer;
import alert.AlertManager;
import camera_share.SharedData;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import motion_detector.Detector;

import java.awt.image.BufferedImage;
import java.net.URL;

public class DetectionController implements InteractionWithPreview {
    private SharedData sharedData;

    @FXML
    private ImageView detectionWebcamView;

    private boolean stopCamera = false;
    private Detector motionDetector;
    private AlertManager alertManager;
    private Timer actionTimer;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        sharedData = SharedData.getInstance();
        motionDetector = new Detector();
        alertManager = new AlertManager(sharedData.isEmailChecked(), sharedData.isSnapshotChecked(), sharedData.isDbChecked());
        actionTimer = new Timer(10);

        try {
            URL soundURL = getClass().getResource("/resources/alert.mp3");
            if (soundURL != null) {
                Media sound = new Media(soundURL.toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setVolume(0.5);

            } else {
                System.out.println("Error: Sound file not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (detectionWebcamView == null) {
            System.out.println("Error: detectionWebcamView is null.");
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
                    boolean motionDetected = motionDetector.detectMotion(frame);

                    if (motionDetected) {
                        if (actionTimer.hasIntervalPassed()) {
                            if (mediaPlayer != null) {
                                mediaPlayer.stop();
                                mediaPlayer.play();
                            }

                            alertManager.saveSnapshot(image, true);
                            alertManager.saveDB(image, true);
                            alertManager.sendMail(sharedData.getEmailAddress(), true);

                            actionTimer.reset();
                        } else {
                            System.out.println("Motion detected, but waiting for timer.");
                        }
                    }

                    Platform.runLater(() -> {
                        if (!stopCamera) {
                            detectionWebcamView.setImage(SwingFXUtils.toFXImage(image, null));
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
        if (motionDetector != null) {
            motionDetector.reset();
        }
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }

    public void navigateToPreview(javafx.event.ActionEvent actionEvent) {
        try {
            stopCamera();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Preview.fxml"));
            Parent previewRoot = loader.load();

            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Scene previewScene = new Scene(previewRoot);
            currentStage.setScene(previewScene);
            currentStage.setTitle("Preview Frame");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
