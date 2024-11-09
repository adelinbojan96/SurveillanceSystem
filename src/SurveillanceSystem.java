import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class SurveillanceSystem extends Application {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private VideoCapture capture;
    private WritableImage fxImage;
    private ImageView imageView;

    @Override
    public void start(Stage primaryStage) {
        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open the webcam.");
            return;
        }

        imageView = new ImageView();
        fxImage = new WritableImage(640, 480);
        imageView.setImage(fxImage);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, 640, 480);
        primaryStage.setTitle("JavaFX Webcam Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this::updateFrame).start();
    }

    private void updateFrame() {
        Mat frame = new Mat();

        while (capture.isOpened()) {
            if (capture.read(frame)) {
                BufferedImage image = matToBufferedImage(frame);
                if (image != null) {
                    fxImage = SwingFXUtils.toFXImage(image, fxImage);
                    imageView.setImage(fxImage);
                }
            }

            try {
                Thread.sleep(30); // ~ 33 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        capture.release();
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

    @Override
    public void stop() throws Exception {
        super.stop();
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
