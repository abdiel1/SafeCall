package contact_management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.abdielrosado.safecall.MainActivity;
import com.example.abdielrosado.safecall.R;

import java.util.List;

/**
 * This class manages the main menu of the contact list.
 */
public class ContactListManagement extends AppCompatActivity implements
        MoveContactsDialogFragment.MoveContactsDialogListener {

    private static ContactListManager contactListManager;

    private MyArrayAdapter arrayAdapter;

    private List<Contact> contacts;

    /**
     * Is set to the position of the currently selescted element of the viewList
     */
    private int currentClickedElement = 0;

    private MoveContactsDialogFragment dialogFragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_management);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    /**
     * Open the activity for adding contacts
     * @param view
     */
    public void onClickAddContacts(View view){
        Intent intent = new Intent(this,AddContactsActivity.class);
        startActivity(intent);
    }

    /**
     * Open the activity for removing contacts
     * @param view
     */
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
