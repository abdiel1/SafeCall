package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Map;

import fall_detection.FallDetectionManagement;
import fall_detection.FallDetectionManager;
import contact_management.ContactListManagement;

public class Settings extends AppCompatActivity {

    private Map<String,Boolean> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SettingsManager settingsManager = SettingsManager.getInstance(this);
        settings = settingsManager.getSettings(this);

        //Set up wearable device fall detection switch
        final Switch wearableDeviceSwitch = (Switch) findViewById(R.id.switch1);
        if(settings.get(SettingsManager.WD_FALL_DETECTION) != null){
            wearableDeviceSwitch.setChecked(settings.get(SettingsManager.WD_FALL_DETECTION));
        } else{
            wearableDeviceSwitch.setChecked(true);
        }

        wearableDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.put(SettingsManager.WD_FALL_DETECTION,isChecked);
                settingsManager.saveSettings(Settings.this);
            }
        });

        //Set up on phone fall detection switch
        final Switch phoneSwitch = (Switch) findViewById(R.id.switch2);
        if(settings.get(SettingsManager.ON_PHONE_FALL_DETECTION) != null){
            phoneSwitch.setChecked(settings.get(SettingsManager.ON_PHONE_FALL_DETECTION));

        } else {
            phoneSwitch.setChecked(false);
        }

        phoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               settings.put(SettingsManager.ON_PHONE_FALL_DETECTION, isChecked);
               settingsManager.saveSettings(Settings.this);
                if(isChecked){
                    FallDetectionManagement.startListening();
                } else{
                    FallDetectionManagement.stopListening();
                }

            }
        });
    }

    public void onClickGoToContacts(View view){
        Intent intent = new Intent(this, ContactListManagement.class);
        startActivity(intent);
    }

}
