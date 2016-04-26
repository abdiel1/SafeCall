package twilio;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import emergency_protocol.EmergencyManager;


public class TwilioCallService extends Service {

    private static final String TAG = "TwilioCallService";
    private final IBinder binder = new LocalBinder();
    private EmergencyManager emergencyManager;
    private Handler handler = new Handler();
    private BroadcastReceiver sendReceiver;

    private Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            emergencyManager.startEmergencyProtocol();
            stopSelf();
        }
    };


    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        emergencyManager = EmergencyManager.getInstance(getApplicationContext());
        handler.postDelayed(serviceRunnable,1000);
    }



    public class LocalBinder extends Binder{
        TwilioCallService getService(){
            return TwilioCallService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void stopCall(){
        emergencyManager.setCallInProgress(false);
    }

    public void ackReceived(){
        emergencyManager.setAckReceived(true);
    }


}
