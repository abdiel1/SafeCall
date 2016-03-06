package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import ContactManagement.*;
import java.util.List;

public class ContactListManagement extends AppCompatActivity {

    private static ContactListManager contactListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_management);

        contactListManager = ContactListManager.getInstance();
        List<Contact> contacts = contactListManager.getContactList();
        ListView listView = (ListView) findViewById(R.id.listView);

        MyArrayAdapter arrayAdapter = new MyArrayAdapter(this,contacts,R.layout.contactlistview);

        listView.setAdapter(arrayAdapter);
    }

    public void onClickAddContacts(View view){
        Intent intent = new Intent(this,AddContactsActivity.class);
        startActivity(intent);
    }

}
