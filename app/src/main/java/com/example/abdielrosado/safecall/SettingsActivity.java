package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Map;

import btcomm.BluetoothCommActivity;
import contact_management.ContactListManagement;


/**
 * Created by abdielrosado on 3/23/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTINGSACTIVITY";
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //To toggle the switch for the Fall Detection on wearable device
        final Switch toggleOnWearableDetection = (Switch) findViewById(R.id.switch1);
        final Switch toggleOnPhoneDetection = (Switch) findViewById(R.id.switch2);

        settingsManager = SettingsManager.getInstance(this);
        final Map<String,Boolean> settings = settingsManager.getSettings(this);

        Boolean temp = settings.get(SettingsManager.ON_PHONE_FALL_DETECTION);
        toggleOnPhoneDetection.setChecked(temp == null ? false:temp);
        temp = settings.get(SettingsManager.WD_FALL_DETECTION);
        toggleOnWearableDetection.setChecked(temp == null ? false:temp);

        toggleOnWearableDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d(TAG, "The Bluetooth activity will be called.");
                    Intent intent = new Intent(getApplicationContext(), BluetoothCommActivity.class);
                    startActivity(intent);
                    toggleOnWearableDetection.setChecked(true);
                    settings.put(SettingsManager.WD_FALL_DETECTION, true);
                    settingsManager.saveSettings(SettingsActivity.this);
                } else {
                    // The toggle is disabled
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


                } else {
                    // The toggle is disabled
                    toggleOnPhoneDetection.setChecked(false);
                    settings.put(SettingsManager.ON_PHONE_FALL_DETECTION, false);
                    settingsManager.saveSettings(SettingsActivity.this);

                }
            }
        });

    }

    public void onClickGoToContacts(View view){
        Intent intent = new Intent(this, ContactListManagement.class);
        startActivity(intent);

    }

}
