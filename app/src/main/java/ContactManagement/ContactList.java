package ContactManagement;

import android.content.Context;

import java.util.List;

/**
 * Created by abdielrosado on 3/3/16.
 */
public interface ContactList {

    public List<Contact> getContactList();

    public void addContact(Contact contact);

    public boolean removeContact(Contact contact);

}
