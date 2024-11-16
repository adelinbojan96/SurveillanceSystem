package camera_share;

import javafx.scene.image.WritableImage;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import org.opencv.core.Mat;

import java.awt.image.DataBufferByte;

public class SharedData {
    private static SharedData instance;

    private WritableImage fxImage;
    private VideoCapture videoCapture;

    private SharedData() {}

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public WritableImage getFxImage() {
        return fxImage;
    }

    public void setFxImage(WritableImage fxImage) {
        this.fxImage = fxImage;
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }

    public void setVideoCapture(VideoCapture videoCapture) {
        this.videoCapture = videoCapture;
    }
    public BufferedImage matToBufferedImage(Mat mat) {
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
}
