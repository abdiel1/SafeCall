package contact_management;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abdielrosado.safecall.R;

import java.util.ArrayList;
import java.util.List;

public class RemoveContactsActivity extends AppCompatActivity {

    private static List<Integer> selected = new ArrayList<Integer>();

    private final String NO_SELECTION = "No Contacts Have Been Selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove__contacts);

        selected.clear();

        ListView listView = (ListView) findViewById(R.id.listView3);
        ContactList contactList = ContactListManager.getInstance();
        MyArrayAdapter arrayAdapter = new MyArrayAdapter(this,contactList.getContactList(this),
                R.layout.select_contact,MyArrayAdapter.REMOVING_CONTACTS);

        listView.setAdapter(arrayAdapter);

    }

    public static void addSelection(int position){
        selected.add(position);
    }

    public static void removeSelection(int position){
        selected.remove((Integer) position);
    }

    public void onClickRemoveContacts(View view){
        if(selected.size() == 0){
            Toast.makeText(this,NO_SELECTION,Toast.LENGTH_SHORT).show();
        } else{
            ContactListManager contactListManager = ContactListManager.getInstance();
            List<Contact> contactList = contactListManager.getContactList(this);

            //Get all the selected contacts
            List<Contact> contacts = new ArrayList<Contact>();
            for(Integer i : selected){
                contacts.add(contactList.get(i));
            }

            // Remove all the selected contacts
            for(Contact c : contacts){
                contactList.remove(c);
            }
            contactListManager.saveContacts(this);

            // Go back to the contacts main menu
            Intent intent = new Intent(this, ContactListManagement.class);
            startActivity(intent);
        }
    }

}
