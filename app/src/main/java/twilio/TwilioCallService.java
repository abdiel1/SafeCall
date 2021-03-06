package twilio;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import emergency_protocol.EmergencyManager;


public class TwilioCallService extends Service {

    private final IBinder binder = new LocalBinder();
    private EmergencyManager emergencyManager;
    private Handler handler = new Handler();

    private Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            emergencyManager.startEmergencyProtocol();
        }
    };


    @Override
    public void onCreate(){
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



    public void setComplete(){
        emergencyManager.setComplete();
    }

    public void acknowledgementReceived(){
        emergencyManager.setAckReceived(true);
    }

    public void stopEmergencyProtocol(){
        emergencyManager.stopEmergencyProtocol();
    }






}
