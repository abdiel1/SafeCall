package contact_management;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abdielrosado.safecall.MainActivity;
import com.example.abdielrosado.safecall.R;

import java.util.ArrayList;
import java.util.List;

public class AddContactsActivity extends AppCompatActivity {

    /**
     * List of the positions of the elements selected in the listView
     */
    private static List<Integer> selected = new ArrayList<Integer>();

    /**
     * List of the user's contacts
     */
    private List<Contact> contactList;

    /**
     * Message for when the user tries to add contacts and hasn't selected one.
     */
    private final String NO_SELECTION = "No Contacts Have Been Selected";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Get list view and initialize class' lists
        ListView listView = (ListView) findViewById(R.id.listView2);
        contactList = new ArrayList<Contact>();
        selected.clear();

        // Get the phone's contacts in ascending order
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        // Create contact objects from each of the phone's contacts
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name,number);
            contactList.add(contact);
        }

        // Set the list view adapter
        MyArrayAdapter arrayAdapter = new MyArrayAdapter(this,contactList,R.layout.select_contact,
                MyArrayAdapter.ADDING_CONTACTS);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
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

    public static void addSelection(int selection){
        selected.add(selection);
    }

    public static void removeSelection(int selection){
        selected.remove((Integer) selection);
    }

    /**
     * Adds the selected contacts to the contact list
     * @param view
     */
    public void onAddContactsClicked(View view){
        if(selected.size() == 0){
            Toast.makeText(this,NO_SELECTION,Toast.LENGTH_SHORT).show();
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
