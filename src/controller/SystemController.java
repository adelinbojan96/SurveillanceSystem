package controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class SystemController {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private VideoCapture capture;
    private WritableImage fxImage;
    private boolean stopCamera = false;

    @FXML
    private ImageView webcamView;

    @FXML
    public void initialize() {
        webcamView.setPreserveRatio(true);

        capture = new VideoCapture(0, Videoio.CAP_DSHOW);

        if (!capture.isOpened()) {
            System.out.println("Error: Could not open the webcam.");
            return;
        }

        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 720);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 540);

        fxImage = new WritableImage(720, 540);
        webcamView.setImage(fxImage);

        new Thread(this::updateFrame).start();
    }

    public void updateFrame() {
        Mat frame = new Mat();

        while (!stopCamera && capture.isOpened()) {
            if (capture.read(frame)) {
                BufferedImage image = matToBufferedImage(frame);
                if (image != null) {
                    WritableImage finalFxImage = SwingFXUtils.toFXImage(image, null);
                    Platform.runLater(() -> {
                        if (!stopCamera) {
                            webcamView.setImage(finalFxImage);
                        }
                    });
                }
            } else {
                System.out.println("Frame not read from capture.");
            }

            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            return null;
        }
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public void stopCamera() {
        stopCamera = true;
        System.out.println("Stopping camera capture.");
        if (capture != null && capture.isOpened()) {
            capture.release();
            System.out.println("Camera capture released.");
        }
    }
}
