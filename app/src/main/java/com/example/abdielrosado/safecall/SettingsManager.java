package com.example.abdielrosado.safecall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import contact_management.Contact;
import contact_management.ContactListManagement;
import contact_management.ContactListManager;

/**
 * Created by abdielrosado on 3/23/16.
 */
public class SettingsManager extends AppCompatActivity {

    private static final String SETTINGS_FILE =  "Settings.txt";

    public static final String WD_FALL_DETECTION = "WDFD";

    public static final String ON_PHONE_FALL_DETECTION = "OPFD";

    private Map<String,Boolean> settings;

    private static SettingsManager instance;

    public SettingsManager(){
        settings = new HashMap<String,Boolean>();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }
    public static SettingsManager getInstance(){
        if(instance == null){
            instance = new SettingsManager();
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

                }
            } catch (JSONException e){

            }
        } catch(IOException e){

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

                fileWriter.write(jsonObject.toString() + "\n");

            } catch (JSONException ex){

            } finally {
                fileWriter.close();
            }
        } catch (IOException e){

        }
    }

    public void onClickGoToContacts(View view){
        Intent intent = new Intent(this, ContactListManagement.class);
        startActivity(intent);

    }

}
