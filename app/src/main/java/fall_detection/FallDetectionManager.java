package fall_detection;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener {

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();

    private AtomicBoolean alarmOn;

    private AtomicBoolean freeFall;

    private long time;

    private Real multiplyFactor;

    private static FallDetectionManager instance;

    private FallDetectionManager() {
        alarmOn = new AtomicBoolean(false);
        freeFall = new AtomicBoolean(false);
        time = 0;
    }

    public static FallDetectionManager getInstance() {
        if (instance == null) {
            instance = new FallDetectionManager();
        }

        return instance;

    }

    @Override
    public void registerFallDetectionListener(FallDetectionListener fallDetectionListener) {
        if (!fallDetectionListeners.contains(fallDetectionListener)) {
            fallDetectionListeners.add(fallDetectionListener);
        }
    }

    @Override
    public boolean removeFallDetectionListener(FallDetectionListener fallDetectionListener) {

        return fallDetectionListeners.remove(fallDetectionListener);
    }

    @Override
    public void notifyFallDetectionListeners() {

        for (FallDetectionListener f : fallDetectionListeners) {
            f.onFallDetected();
        }

    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {


        if ((calculateMagnitude(event.values) - 96.2361) > 90 && !alarmOn.get() && freeFall.get()) {
            Log.d("Fall", "Fall was detected");
            //Log.d("FallMD",fallMahalanobisDistance.toString());
            //Log.d("NFallMD",nonFallMahalanobisDistance.toString());
            FallDetectionManagement.stopListening();
            alarmOn.set(true);
            notifyFallDetectionListeners();
        }


        if ((calculateMagnitude(event.values) - 96.2361) < -55) {
            freeFall.set(true);
            time = System.currentTimeMillis();

        } else if (System.currentTimeMillis() - time > 500) {
            freeFall.set(false);
        }



    }

    private float calculateMagnitude(float[] values) {
        return (values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void alarmOff() {
        alarmOn.set(false);
    }


}
