package com.example.abdielrosado.safecall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import btcomm.BluetoothCommActivity;
import contact_management.ContactListManagement;

/**
 * Created by Kenneth on 4/20/2016.
 */
public class SettingsManager {

    private static final String SETTINGS_FILE =  "Settings.txt";
    public static final String WD_FALL_DETECTION = "WDFD";
    public static final String ON_PHONE_FALL_DETECTION = "OPFD";
    public static final String NAME = "NAME";

    private static final String TAG = "SETTINGSMANAGER";

    private Map<String,Boolean> settings;
    private Map<String,String> profile;

    private static SettingsManager instance;

    public SettingsManager(Context context){
        settings = new HashMap<String,Boolean>();
        profile = new HashMap<String , String>();
        loadSettings(context);
    }

    public static SettingsManager getInstance(Context context){
        if(instance == null){
            instance = new SettingsManager(context);
        }

        return instance;
    }

    public Map<String,Boolean> getSettings(Context context){
        if(settings.isEmpty()){
            loadSettings(context);
        }

        return settings;
    }

    private void loadSettings(Context context){
        File file = new File(context.getFilesDir(),SETTINGS_FILE);
        settings.clear();
        profile.clear();
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try{
                JSONTokener tokener;
                JSONObject jsonObject;
                String line;

                while ((line = in.readLine()) != null){
                    tokener = new JSONTokener(line);
                    jsonObject = (JSONObject) tokener.nextValue();
                    settings.put(WD_FALL_DETECTION, (Boolean) jsonObject.get(WD_FALL_DETECTION));
                    settings.put(ON_PHONE_FALL_DETECTION, (Boolean) jsonObject.get(ON_PHONE_FALL_DETECTION));
                    profile.put(NAME, (String) jsonObject.get(NAME));

                }
            } catch (JSONException e){
                Log.e(TAG, e.toString());
            }finally {
                in.close();
            }
        } catch(IOException e){
            Log.e(TAG, e.toString());
        }
    }

    public void saveSettings(Context context){

        JSONObject jsonObject;
        File file = new File(context.getFilesDir(),SETTINGS_FILE);

        try{
            FileWriter fileWriter = new FileWriter(file);

            try {
                jsonObject = new JSONObject();
                jsonObject.put(WD_FALL_DETECTION,settings.get(WD_FALL_DETECTION));
                jsonObject.put(ON_PHONE_FALL_DETECTION,settings.get(ON_PHONE_FALL_DETECTION));
                jsonObject.put(NAME, profile.get(NAME));

                fileWriter.write(jsonObject.toString() + "\n");

            } catch (JSONException ex){
                Log.e(TAG, ex.toString());
            } finally {
                fileWriter.close();
            }
        } catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    public Map<String, String> getProfile(Context context){
        if(profile.isEmpty()){
            loadSettings(context);
        }
        return profile;
    }

}

