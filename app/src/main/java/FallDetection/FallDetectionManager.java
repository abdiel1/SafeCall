package FallDetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.abdielrosado.safecall.SettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener{

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();

    private float[][] previousValues = new float[3][2];



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
        if(active){
            if(previousValues[0][0] != 0){
                float[] slopes = calculateSlope(event.values);
                if(slopes[0] > 50){

                }
            }

            long currentTime = System.currentTimeMillis();
            previousValues[0][0] = event.values[0];
            previousValues[0][1] = currentTime;
            previousValues[1][0] = event.values[1];
            previousValues[1][1] = currentTime;
            previousValues[2][0] = event.values[2];
            previousValues[2][1] = currentTime;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] calculateSlope(float[] values){
        float[] slopes = new float[3];
        long currentTime = System.currentTimeMillis();
        slopes[0] = Math.abs((values[0] - previousValues[0][0])/
                (currentTime - previousValues[0][1]));

        slopes[1] = Math.abs((values[1] - previousValues[1][0])/
                (currentTime - previousValues[1][1]));

        slopes[2] = Math.abs((values[2] - previousValues[2][0])/
                (currentTime - previousValues[2][1]));

        return slopes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value){
        active = value;
    }
}
