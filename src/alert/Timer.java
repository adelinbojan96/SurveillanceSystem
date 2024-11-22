package alert;

public class Timer {
    private long lastActionTime;
    private final int interval;

    public Timer(int intervalInSeconds) {
        this.interval = intervalInSeconds * 1000;
        this.lastActionTime = 0;
    }

    public boolean hasIntervalPassed() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActionTime) >= interval;
    }

    public void reset() {
        lastActionTime = System.currentTimeMillis();
    }

}
