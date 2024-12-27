package motion_detector;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class Detector {

    private Mat previousFrame;

    public Detector() {
        previousFrame = null;
    }
    //detection of motion between current frame and previous.
    public boolean detectMotion(Mat currentFrame) {
        if (previousFrame == null) {
            previousFrame = preprocessFrame(currentFrame);
            return false;
        }

        Mat processedFrame = preprocessFrame(currentFrame);

        Mat diffFrame = new Mat();
        Core.absdiff(processedFrame, previousFrame, diffFrame);

        Mat threshFrame = new Mat();
        Imgproc.threshold(diffFrame, threshFrame, 25, 255, Imgproc.THRESH_BINARY);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(threshFrame, threshFrame, kernel, new Point(-1, -1), 2);

        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(threshFrame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        boolean motionDetected = false;
        for (MatOfPoint contour : contours) {
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > 5000) {
                motionDetected = true;
                break;
            }
        }

        previousFrame = processedFrame;

        return motionDetected;
    }

    //frame is preprocessed, converting it to grayscale and applying Gaussian blur.
    private Mat preprocessFrame(Mat frame) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(21, 21), 0);
        return grayFrame;
    }

    public void reset() {
        previousFrame = null;
    }
}
