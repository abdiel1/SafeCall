package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import EmergencyProtocol.LocationManagement;
import fall_detection.FallDetectionManagement;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, FallDetectionManagement.class);
        //startService(intent);

        startLocation();



    }

    public void onClickGoToSettings(View view){
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }

    public void onCountdownClicked(View view){
        Intent intent = new Intent(this,Countdown.class);
        startActivity(intent);
    }

    public void startLocation(){
        final LocationManagement locationManagement = new LocationManagement(this);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManagement.requestLocation();
                handler.postDelayed(this,LocationManagement.GPS_TIME_INTERVAL);
            }
        });
    }

}
