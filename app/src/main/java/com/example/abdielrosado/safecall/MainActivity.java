package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import emergency_protocol.EmergencyManager;
import emergency_protocol.LocationManagement;
import fall_detection.FallDetectionManagement;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

//        startLocation();
        EmergencyManager emergencyManager = EmergencyManager.getInstance(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickGoToSettings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void onCountdownClicked(View view){
        Intent intent = new Intent(this,Countdown.class);
        startActivity(intent);
    }

    public void onClickGoToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
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

    @Override
    public void onBackPressed(){
        return;
    }

}
