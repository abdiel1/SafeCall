package FallDetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener{

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();



    private boolean active;

    private static FallDetectionManager instance;

    private FallDetectionManager(){
        active = true;
    }

    public static FallDetectionManager getInstance(){
        if(instance == null){
            instance = new FallDetectionManager();
        }

        return instance;

    }

    @Override
    public void registerFallDetectionListener(FallDetectionListener fallDetectionListener) {
        if(!fallDetectionListeners.contains(fallDetectionListener)){
            fallDetectionListeners.add(fallDetectionListener);
        }
    }

    @Override
    public boolean removeFallDetectionListener(FallDetectionListener fallDetectionListener) {

        return fallDetectionListeners.remove(fallDetectionListener);
    }

    @Override
    public void notifyFallDetectionListeners() {

        for(FallDetectionListener f : fallDetectionListeners){
            f.onFallDetected();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value){
        active = value;
    }
}
