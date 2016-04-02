package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import contact_management.ContactListManagement;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickGoToSettings(View view){
        Intent intent = new Intent(this,SettingsManager.class);
        startActivity(intent);
    }

}
