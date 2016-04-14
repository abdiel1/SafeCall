package EmergencyProtocol;

import android.content.Context;
import android.widget.ListView;

import java.security.acl.LastOwnerException;
import java.util.List;

import btcomm.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager {
    private static Context context;
    private static EmergencyManager emergencyManager;

    private boolean ackReceived;

    private EmergencyManager(Context cont) {
        context = cont;
    }

    public static EmergencyManager getInstance(Context cont){
        if(emergencyManager == null){
            emergencyManager = new EmergencyManager(cont);
        }
        return emergencyManager;
    }

    public void startEmergencyProtocol(){
        LocationManagement locationManagement = LocationManagement.getInstance(context);
        String location = locationManagement.getLocation();

        //Put send location to server here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        CallCaregiver caregiver = CallCaregiver.getInstance(context);

        ContactListManager contactListManager = ContactListManager.getInstance();
        List<Contact> contactList = contactListManager.getContactList(context);

        int count = 0;
        while(!ackReceived){
            // Make the call
            caregiver.connect(contactList.get(count).getPhoneNumber());
            if(count < contactList.size()){
                count++;
            } else{
                count = 0;
            }
        }


    }


}
