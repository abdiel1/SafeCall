package emergency_protocol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.abdielrosado.safecall.R;

public class AckReceiverActivity extends AppCompatActivity {

    private BroadcastReceiver sendReceiver;
    private String TAG = "AckReceiverActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ack_receiver);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(sendReceiver);
    }

    @Override
    public void onPause(){
        super.onPause();
        this.unregisterReceiver(sendReceiver);
    }
}
