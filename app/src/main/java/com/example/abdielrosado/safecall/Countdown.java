package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import EmergencyProtocol.EmergencyManager;
import fall_detection.FallDetectionManager;


public class Countdown extends AppCompatActivity {

    private volatile boolean stop;
    private static final String COUNT = "15.00";
    private static final int SLEEP_TIME = 100;
    private static final double INTERVAL = 0.1;
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        stop = false;

        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this,alarm);
        ringtone.play();

        runTimer();

    }

    public void onStopClicked(View view){
        stop = true;
        if(ringtone.isPlaying()){
            ringtone.stop();
        }
    }

    private void runTimer(){
        final TextView counter = (TextView) findViewById(R.id.counter);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.countdownLayout);
        counter.setText(COUNT);

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            double count =  15.00;
            @Override
            public void run() {

                String countString;

                count = count - INTERVAL;
                countString = String.format("%.1f", count);
                counter.setText(countString);

                if((int) count%2 == 0){
                    layout.setBackgroundColor(Color.RED);
                } else if((int) count%1 == 0){
                    layout.setBackgroundColor(Color.WHITE);
                }

                if(count <= 0){
                    ringtone.stop();
                    counter.setText("0");
                    Log.d("False Positive", "NO");
                    EmergencyManager emergencyManager = EmergencyManager.getInstance(Countdown.this);
                    emergencyManager.startEmergencyProtocol();
                } else if(stop){
                    counter.setText("STOPPED");
                    FallDetectionManager.getInstance().alarmOff();
                    Log.d("False Positive", "YES");
                }
                else{
                    handler.postDelayed(this,SLEEP_TIME);
                }


            }
        });
    }
}
