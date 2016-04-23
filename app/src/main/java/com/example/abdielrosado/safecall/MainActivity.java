package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import emergency_protocol.EmergencyManager;
import emergency_protocol.LocationManagement;
import fall_detection.FallDetectionManagement;
import gcm.MessageReceiverActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, FallDetectionManagement.class);
        //startService(intent);


        //startLocation();
        EmergencyManager emergencyManager = EmergencyManager.getInstance(getApplicationContext());



    }

    public void onClickGoToSettings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void onCountdownClicked(View view){
        Intent intent = new Intent(this,Countdown.class);
        startActivity(intent);
    }

    public void onProfileClicked(View view){
        Intent intent = new Intent(this, MessageReceiverActivity.class);
        startActivity(intent);
    }

    public void startLocation(){
        final LocationManagement locationManagement = LocationManagement.getInstance(this);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManagement.requestLocation();
                handler.postDelayed(this, LocationManagement.GPS_TIME_INTERVAL);
            }
        });
    }

}
