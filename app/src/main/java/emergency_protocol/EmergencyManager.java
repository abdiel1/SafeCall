package emergency_protocol;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import twilio.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;
import twilio.TwilioCallService;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager{
    private static Context context;
    private static EmergencyManager emergencyManager;
    private boolean ackReceived;
    private volatile boolean callInProgress;
    public static final String ACTION_CALL_STATUS = EmergencyManager.class.getName() + "CallBroadcast";
    public static final String EXTRA_CONTACT_NAME = "Extra_Contact_Name";
    public static final String EXTRA_PHONE_NUMBER = "Extra_Phone_Number";

    private CallCaregiver caregiver;


    private EmergencyManager(Context cont) {
        context = cont;
        ackReceived = false;
//        MessageReceiver msgReceiver = new MessageReceiver(context);
        caregiver = CallCaregiver.getInstance(context);
    }

    public static EmergencyManager getInstance(Context cont) {
        if (emergencyManager == null) {
            emergencyManager = new EmergencyManager(cont);
        }
        return emergencyManager;
    }


    public boolean isCallInProgress() {
        return callInProgress;
    }

    public void setCallInProgress(boolean callInProgress) {
        this.callInProgress = callInProgress;
    }

    public void startEmergencyProtocol() {
        LocationManagement locationManagement = LocationManagement.getInstance(context);
        String location = locationManagement.getLocation();

        //Put send location to server here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        ContactListManager contactListManager = ContactListManager.getInstance();
        final List<Contact> contactList = contactListManager.getContactList(context);

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            int count = 0;
            @Override
            public void run() {

                if (!isCallInProgress()) {
                    Contact contact = contactList.get(count);
                    String phoneNumber = contact.getPhoneNumber();
                    String name = contact.getName();
//                caregiver.connect(phoneNumber);
                    callInProgress = true;
                    Intent intent = new Intent(ACTION_CALL_STATUS);
                    intent.putExtra(EXTRA_CONTACT_NAME,name);
                    intent.putExtra(EXTRA_PHONE_NUMBER,phoneNumber);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    Log.d("Service","Intent Sent");

                    if (count < contactList.size() - 1) {
                        count++;
                        //break;
                    } else {
                        count = 0;
                    }
                }
                if(!ackReceived){
                    handler.postDelayed(this,2000);
                } else{
                    //Send Text Message
                }
            }
        });

    }

}
