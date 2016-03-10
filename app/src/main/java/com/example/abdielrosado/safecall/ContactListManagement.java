package com.example.abdielrosado.safecall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ContactManagement.*;
import java.util.List;

public class ContactListManagement extends AppCompatActivity implements
        MoveContactsDialogFragment.MoveContactsDialogListener{

    private static ContactListManager contactListManager;

    private MyArrayAdapter arrayAdapter;

    private List<Contact> contacts;

    private int currentClickedElement = 0;

    private MoveContactsDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_management);

        contactListManager = ContactListManager.getInstance();
        contacts = contactListManager.getContactList(this);
        ListView listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new MyArrayAdapter(this,contacts,R.layout.contactlistview,MyArrayAdapter.NONE);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentClickedElement = position;

                dialogFragment = new MoveContactsDialogFragment();
                dialogFragment.show(getFragmentManager(),"MoveContactsDialogFragment");
            }
        });
    }

    public void onClickAddContacts(View view){
        Intent intent = new Intent(this,AddContactsActivity.class);
        startActivity(intent);
    }

    public void onClickGoToRemoveContacts(View view){
        Intent intent = new Intent(this,RemoveContactsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(int position) {
        //Get the selected contact
        Contact contact = contacts.get(currentClickedElement);

        // Reorder the contact list
        contactListManager.reorderContacts(contact,position - 1);
        contactListManager.saveContacts(this);

        //Update list view
        arrayAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDialogNegativeClick() {
        dialogFragment.dismiss();

    }
}
