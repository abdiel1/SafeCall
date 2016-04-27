package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.util.Map;

import btcomm.BluetoothCommActivity;
import btcomm.SendAndGetData;
import contact_management.ContactListManagement;
import fall_detection.FallDetectionManagement;


/**
 * Created by abdielrosado on 3/23/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTINGSACTIVITY";
    private SettingsManager settingsManager;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        //To toggle the switch for the Fall Detection on wearable device
        final Switch toggleOnWearableDetection = (Switch) findViewById(R.id.switch1);
        final Switch toggleOnPhoneDetection = (Switch) findViewById(R.id.switch2);
        final ToggleButton btConnect = (ToggleButton) findViewById(R.id.BT_toggle);
        final SendAndGetData sendAndGetData = SendAndGetData.getInstance(SettingsActivity.this);

        settingsManager = SettingsManager.getInstance(this);
        final Map<String,Boolean> settings = settingsManager.getSettings(this);
        Boolean temp = settings.get(SettingsManager.ON_PHONE_FALL_DETECTION);
        toggleOnPhoneDetection.setChecked(temp == null ? false:temp);
        temp = settings.get(SettingsManager.WD_FALL_DETECTION);


        if(sendAndGetData.isConnected()){
            btConnect.setChecked(true);
            toggleOnWearableDetection.setChecked(temp == null ? false:temp);
            if(toggleOnWearableDetection.isChecked()){
                sendAndGetData.sendDataToBT("E");
            }
        }else{
            btConnect.setChecked(false);
            toggleOnWearableDetection.setChecked(false);
            toggleOnWearableDetection.setClickable(false);
        }

        toggleOnWearableDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if(sendAndGetData.isConnected()){
                        sendAndGetData.sendDataToBT("E");
                    }
                    toggleOnWearableDetection.setChecked(true);
                    settings.put(SettingsManager.WD_FALL_DETECTION, true);
                    settingsManager.saveSettings(SettingsActivity.this);
                } else {
                    // The toggle is disabled
                    if(sendAndGetData.isConnected()){
                        sendAndGetData.sendDataToBT("D");
                    }
                    toggleOnWearableDetection.setChecked(false);
                    settings.put(SettingsManager.WD_FALL_DETECTION, false);
                    settingsManager.saveSettings(SettingsActivity.this);
                }
            }
        });

        //To toggle the switch for the Fall Detection on wearable device
        toggleOnPhoneDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    toggleOnPhoneDetection.setChecked(true);
                    settings.put(SettingsManager.ON_PHONE_FALL_DETECTION, true);
                    settingsManager.saveSettings(SettingsActivity.this);
                    Intent intent = new Intent(SettingsActivity.this, FallDetectionManagement.class);
                    startService(intent);


                } else {
                    // The toggle is disabled
                    toggleOnPhoneDetection.setChecked(false);
                    settings.put(SettingsManager.ON_PHONE_FALL_DETECTION, false);
                    settingsManager.saveSettings(SettingsActivity.this);
                    stopService(new Intent(SettingsActivity.this, FallDetectionManagement.class));

                }
            }
        });

        btConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked & !sendAndGetData.isConnected()){
                    toggleOnWearableDetection.setClickable(true);
                    Intent intent = new Intent(getApplicationContext(), BluetoothCommActivity.class);
                    startActivity(intent);
                }else if(!isChecked & sendAndGetData.isConnected()){
                    toggleOnWearableDetection.setClickable(false);
                    settings.put(SettingsManager.WD_FALL_DETECTION, false);
                    settingsManager.saveSettings(SettingsActivity.this);
                    sendAndGetData.closeConnectionFromBT();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickGoToContacts(View view){
        Intent intent = new Intent(this, ContactListManagement.class);
        startActivity(intent);

    }

    @Override
    public void onDestroy(){
        if(SendAndGetData.getInstance(this).isConnected()){
            SendAndGetData.getInstance(this).closeConnectionFromBT();
        }
        super.onDestroy();
    }

}
