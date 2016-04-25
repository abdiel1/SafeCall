package twilio;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.abdielrosado.safecall.R;

import emergency_protocol.EmergencyManager;

/**
 * Created by abdielrosado on 4/18/16.
 */
public class TwilioCallActivity extends AppCompatActivity{

    private static final String TAG = "TwilioCallActivity";
    private TextView status;
    private TwilioCallService twilioCallService;
    private boolean isBound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twilio_main);
        status = (TextView) findViewById(R.id.call_status);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"Intent Received");
                status.setText(intent.getStringExtra(EmergencyManager.EXTRA_CONTACT_NAME));
            }
        },new IntentFilter(EmergencyManager.ACTION_CALL_STATUS));

        //Get Intent
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action)&& type != null){
            if("text/plain".equals(type)){
                Intent intentUpdate = new Intent(EmergencyManager.ACTION_CALL_STATUS);
                intentUpdate.putExtra(EmergencyManager.EXTRA_CONTACT_NAME,intent.getStringExtra(Intent.EXTRA_TEXT));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentUpdate);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            doUnbindService();
        }
    }

    public void onClickSetAck(View view) {
        if(isBound){
            twilioCallService.ackReceived();
        }
    }

    public void onClickDone(View view) {
        if (isBound) {
            twilioCallService.stopCall();
        }

    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            twilioCallService = ((TwilioCallService.LocalBinder) service).getService();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            twilioCallService = null;
        }
    };

    private void doBindService() {
        Intent intent = new Intent(this, TwilioCallService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
