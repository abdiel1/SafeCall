package emergency_protocol;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.abdielrosado.safecall.SettingsManager;
import com.twilio.client.Connection;
import com.twilio.client.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import twilio.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;
import twilio.TwilioCallService;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager{

    private static final String EMERGENCY_MESSAGE = "Necesito ayuda. Me encuentro en: ";
    private static Context context;
    private static EmergencyManager emergencyManager;
    private static AudioManager audioManager;
    private AtomicBoolean ackReceived;
    private AtomicBoolean callInProgress;
    public static final String ACTION_CALL_STATUS = EmergencyManager.class.getName() + "CallBroadcast";
    public static final String EXTRA_CONTACT_NAME = "Extra_Contact_Name";
    public static final String EXTRA_PHONE_NUMBER = "Extra_Phone_Number";

    private static SettingsManager settingsManager;
    private Map<String,String> profile;
    private CallCaregiver caregiver;


    private EmergencyManager(Context cont) {
        if (cont != null) {
            context = cont;
            ackReceived = new AtomicBoolean(false);
            callInProgress = new AtomicBoolean(false);
//        MessageReceiver msgReceiver = new MessageReceiver(context);
            caregiver = CallCaregiver.getInstance(context);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            settingsManager = SettingsManager.getInstance(cont);

        }
    }

    public static EmergencyManager getInstance(Context cont) {
        if (emergencyManager == null) {
            emergencyManager = new EmergencyManager(cont);
        }
        return emergencyManager;
    }


    public boolean isCallInProgress() {
        return callInProgress.get();
    }

    public void setCallInProgress(boolean callInProgress) {
        if(isCallInProgress()){
            caregiver.disconnect();
            this.callInProgress.set(callInProgress);
        }
    }

    public void startEmergencyProtocol() {
        LocationManagement locationManagement = LocationManagement.getInstance(context);
        final String location = locationManagement.getLocation();

        ContactListManager contactListManager = ContactListManager.getInstance();
        final List<Contact> contactList = contactListManager.getContactList(context);

        profile = settingsManager.getProfile(context);

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            Device device;
            int count = 0;
            @Override
            public void run() {
                Contact contact = contactList.get(count);
                if (!isCallInProgress()) {
                    String phoneNumber = contact.getPhoneNumber();
                    String contactName = contact.getName();
                    String username = "";
                    Map<String, String> parameters = new HashMap<String, String>();
                    profile = settingsManager.getProfile(context);

                    if(profile != null){
                        username = profile.get(SettingsManager.NAME);
                    }
                    if(!username.isEmpty()){
                        parameters.put("Username",username);
                    }
                    if(!phoneNumber.isEmpty()){
                        parameters.put("To", phoneNumber);
                    }


                    //Turn on speaker
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);

                    //Make call through Twilio
                    device = caregiver.connect(parameters);

                    callInProgress.set(true);
                    Intent intent = new Intent(ACTION_CALL_STATUS);
                    intent.putExtra(EXTRA_CONTACT_NAME,contactName);
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
                if(!isAckReceived()){
                    handler.postDelayed(this,2000);
                } else{
                    //Send Text Message
                    String message = EMERGENCY_MESSAGE + "http://maps.google.com/?q=" + location;
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact.getPhoneNumber(),null,message,null,null);

                    //Turn off speaker
                    audioManager.setMode(AudioManager.MODE_NORMAL);

                }
            }
        });

    }

    public boolean isAckReceived() {
        return ackReceived.get();
    }

    public void setAckReceived(boolean ackReceived) {
        this.ackReceived.set(ackReceived);
    }

}
