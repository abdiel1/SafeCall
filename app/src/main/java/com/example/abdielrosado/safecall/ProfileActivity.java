package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private SettingsManager settingsManager;
    private static Button updateBT;
    private static EditText nameEditText;
    private String name;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateBT = (Button) findViewById(R.id.profileButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        settingsManager = SettingsManager.getInstance(getApplicationContext());
        if(savedInstanceState != null){
            updateBT.setText(savedInstanceState.getString("buttonState"));
            nameEditText.setText(savedInstanceState.getString("nameText"));
        }

        try{
            setContentView(R.layout.activity_profile);
            toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (NullPointerException e){
            Log.e(TAG, e.toString());
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("buttonState",updateBT.getText().toString());
        outState.putString("nameText", nameEditText.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        updateBT = (Button) findViewById(R.id.profileButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        Map<String,String> profile = settingsManager.getProfile(getApplicationContext());
        this.name = profile.get(SettingsManager.NAME);
        if(this.name != null){
            nameEditText.setEnabled(false);
            nameEditText.setText(this.name);
//            updateBT.setText(R.string.edit);
        }else {
            updateBT.setText(R.string.save);
            nameEditText.setEnabled(true);
        }
    }

    public void updateProfile(View view){
        if(updateBT.getText().equals(getString(R.string.update))){
            nameEditText.setEnabled(true);
            updateBT.setText(R.string.save);
        }else{
            updateBT.setText(R.string.update);
            nameEditText.setEnabled(false);
            this.name = nameEditText.getText().toString();
            final Map<String, String> profile = settingsManager.getProfile(getApplicationContext());
            profile.put(SettingsManager.NAME, this.name);
            settingsManager.saveSettings(getApplicationContext());

        }
    }

    @Override
    public void onBackPressed(){ return; }

}
