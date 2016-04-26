package emergency_protocol;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abdielrosado.safecall.MainActivity;
import com.twilio.client.Connection;
import com.twilio.client.Device;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

//import gcm.MessageReceiver;
import twilio.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;
import twilio.TwilioCallService;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager{
    private static final String TAG = "EmergencyManager" ;
    private static Context context;
    private static EmergencyManager emergencyManager;

    private boolean ackReceived;
    private volatile boolean callInProgress;
    public static final String ACTION_CALL_STATUS = EmergencyManager.class.getName() + "CallBroadcast";
    public static final String EXTRA_CONTACT_NAME = "Extra_Contact_Name";
    public static final String EXTRA_PHONE_NUMBER = "Extra_Phone_Number";

    private CallCaregiver caregiver;
//    private MessageReceiver msgReceiver;

    private static AudioManager audioManager;

    private EmergencyManager(Context cont) {
        context = cont;
        ackReceived = false;
//        msgReceiver = MessageReceiver.getInstance(context);
//        caregiver = CallCaregiver.getInstance(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

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
    public boolean isAckReceived() {
        return ackReceived;
    }

    public void setAckReceived(boolean ackReceived) {
        this.ackReceived = ackReceived;
    }
    public void startEmergencyProtocol() {
        LocationManagement locationManagement = LocationManagement.getInstance(context);
        String location = locationManagement.getLocation();

        //Put send location to server here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        ContactListManager contactListManager = ContactListManager.getInstance();
        final List<Contact> contactList = contactListManager.getContactList(context);

        final Handler handler = new Handler();


        handler.post(new Runnable() {
            Device device;
            int count = 0;
            @Override
            public void run() {

                if (!isCallInProgress()) {
                    Contact contact = contactList.get(count);
                    String phoneNumber = contact.getPhoneNumber();
                    String name = contact.getName();

                    //Turn on speaker
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                    HashMap<String, String> params = new HashMap<String, String>();

//                    device = caregiver.connect(phoneNumber);
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
                }else if(device != null && device.getState().equals(Device.State.READY)){
//                    callInProgress = false;
                }
                if(!isAckReceived()){
                    try{
                        Log.d("Device State",device.getState().toString());
                    }catch (NullPointerException e){
                        Log.d(TAG, "The device not yet created.");
                    }
                    handler.postDelayed(this,2000);
                } else{
                    Log.d(TAG, "Ack received. ");
                    //Send Text Message

                    //Turn off speaker
                    audioManager.setMode(AudioManager.MODE_NORMAL);

                    //Go back to main activity
//                    Intent intent = new Intent(context, MainActivity.class);
//                    context.startActivity(intent);
                }
            }
        });

    }

}
