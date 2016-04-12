package fall_detection;

/**
 * Created by abdielrosado on 3/2/16.
 */
public interface FallDetector {

    /**
     * Register FallDetectionListener objects that want to know when a fall is detected.
     * @param fallDetectionListener - FallDetectionListener object that wants to register.
     */
    public void registerFallDetectionListener(FallDetectionListener fallDetectionListener);


    /**
     * Remove FallDetectionListener objects that no longer want to know when a fall is detected.
     * @param fallDetectionListener - FallDetectionListener object that wants to be removed.
     * @return - Whether the FallDetectionListener object was removed.
     */
    public boolean removeFallDetectionListener(FallDetectionListener fallDetectionListener);

    /**
     * Notify all registered FallDetection listeners that a fall has been detected.
     */
    public void notifyFallDetectionListeners();


}
