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

    private boolean snapshotChecked = false;
    private boolean emailChecked = false;
    private boolean dbChecked = false;
    private String emailAddress;
    private SharedData() {
    }

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

    public boolean isSnapshotChecked() {
        return snapshotChecked;
    }

    public boolean isEmailChecked() {
        return emailChecked;
    }

    public boolean isDbChecked() {
        return dbChecked;
    }
    public void setEmailChecked(boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

    public void setDbChecked(boolean dbChecked) {
        this.dbChecked = dbChecked;
    }

    public void setSnapshotChecked(boolean snapshotChecked) {
        this.snapshotChecked = snapshotChecked;
    }
    public void setEmailAddress(String text)
    {
        this.emailAddress = text;
    }
    public String getEmailAddress()
    {
        return this.emailAddress;
    }
}
