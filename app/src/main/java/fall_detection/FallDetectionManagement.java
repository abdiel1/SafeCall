package fall_detection;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.example.abdielrosado.safecall.Countdown;
import com.example.abdielrosado.safecall.SettingsActivity;
import com.example.abdielrosado.safecall.SettingsManager;

/**
 * Created by abdielrosado on 4/3/16.
 */
public class FallDetectionManagement extends Service implements FallDetectionListener{

    private static SensorManager sensorManager;

    private static Sensor accelerometer;

    private SettingsManager settingsManager;

    private static FallDetectionManager fallDetectionManager;

    private PowerManager.WakeLock wakeLock;

    public void onCreate(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        settingsManager = SettingsManager.getInstance(this);
        Boolean active =  settingsManager.getSettings(this).get(SettingsManager.ON_PHONE_FALL_DETECTION);
        fallDetectionManager = FallDetectionManager.getInstance();
        fallDetectionManager.registerFallDetectionListener(this);

        sensorManager.registerListener(fallDetectionManager,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakeLockTag");
        wakeLock.acquire();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFallDetected() {
        Intent intent = new Intent(this, Countdown.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        fallDetectionManager.removeFallDetectionListener(this);
        sensorManager.unregisterListener(fallDetectionManager);
        wakeLock.release();

    }

    public static void stopListening(){
        sensorManager.unregisterListener(fallDetectionManager);
    }

    public static void startListening(){
        sensorManager.registerListener(fallDetectionManager,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
