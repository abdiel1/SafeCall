package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ContactManagement.*;
import java.util.ArrayList;
import java.util.List;

public class AddContactsActivity extends AppCompatActivity {

    private static List<Integer> selected = new ArrayList<Integer>();
    private List<Contact> contactList;

    private final String NO_SELECTION = "No Contacts Have Been Selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ListView listView = (ListView) findViewById(R.id.listView2);
        contactList = new ArrayList<Contact>();
        selected.clear();


        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name,number);
            contactList.add(contact);
        }

        MyArrayAdapter arrayAdapter = new MyArrayAdapter(this,contactList,R.layout.select_contact);
        listView.setAdapter(arrayAdapter);

    }

    public static void addSelection(int selection){
        selected.add(selection);
    }

    public void onAddContactsClicked(View view){
        if(selected.size() == 0){
            Toast.makeText(this,NO_SELECTION,Toast.LENGTH_SHORT);
        } else{
            ContactList contactListManager = ContactListManager.getInstance();
            for(Integer i : selected){
                contactListManager.addContact(contactList.get(i));
            }

            contactListManager.saveContacts(this);
            Intent intent = new Intent(this,ContactListManagement.class);
            startActivity(intent);
        }
    }


}
