package ContactManagement;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdielrosado on 3/3/16.
 * */
public class ContactListManager implements ContactList{

    private List<Contact> contactList;

    private final static String contactsFile = "EmergencyContacts.txt";

    private static ContactListManager instance;

    private ContactListManager(){
        contactList = new ArrayList<Contact>();
    }

    public static ContactListManager getInstance(){
        if(instance == null){
            instance = new ContactListManager();
        }
        return instance;
    }


    @Override
    public List<Contact> getContactList() {
        if(contactList.isEmpty()){
            loadContacts();
        }
        return contactList;
    }

    @Override
    public void addContact(Contact contact) {
        if(!contactList.contains(contact)){
            contactList.add(contact);
        }
    }

    @Override
    public boolean removeContact(Contact contact) {

        return contactList.remove(contact);
    }

    public void reorderContact(Contact contact, int position){
        if(position < contactList.size() && position >= 0){
            if(contactList.contains(contact)){
                contactList.remove(contact);
                contactList.add(position,contact);
            }
        } else{
            throw new IndexOutOfBoundsException();
        }
    }

    private void loadContacts(){

        contactList.clear();
        File file = new File(contactsFile);
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try{
                JSONTokener tokener;
                JSONObject jsonObject;
                String line;
                Contact contact;

                while ((line = in.readLine()) != null){
                    tokener = new JSONTokener(line);
                    jsonObject = (JSONObject) tokener.nextValue();
                    contact = new Contact(jsonObject.get("Name").toString(),
                            jsonObject.get("Number").toString());
                    contactList.add(contact);
                }
            } catch (JSONException e){

            }
        } catch(IOException e){

        }
    }

    @Override
    public void saveContacts(Context context){

        JSONObject jsonObject;
        File file = new File(context.getFilesDir(),contactsFile);
        try {
            FileWriter fileWriter = new FileWriter(file);
            try {
                for (Contact c : contactList) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Name", c.getName());
                    jsonObject.put("Number", c.getPhoneNumber());

                    fileWriter.write(jsonObject.toString() + "\n");
                }
            } catch (JSONException | IOException e) {
                System.out.println(e.getLocalizedMessage());
            } finally {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
        } catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }
}
