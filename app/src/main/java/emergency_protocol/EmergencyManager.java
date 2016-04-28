package emergency_protocol;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.abdielrosado.safecall.SettingsManager;
import com.twilio.client.Connection;
import com.twilio.client.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import gcm.MessageReceiver;
import twilio.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;
import twilio.TwilioCallService;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager {

    private static final String EMERGENCY_MESSAGE = "Necesito ayuda. Me encuentro en: ";
    private static Context context;
    private static EmergencyManager emergencyManager;
    private static AudioManager audioManager;
    private AtomicBoolean ackReceived;
    private AtomicBoolean callInProgress;
    private AtomicBoolean stop;
    private AtomicBoolean complete;
    public static final String ACTION_CALL_STATUS = EmergencyManager.class.getName() + "CallBroadcast";
    public static final String EXTRA_CONTACT_NAME = "Extra_Contact_Name";
    public static final String EXTRA_PHONE_NUMBER = "Extra_Phone_Number";

    private static SettingsManager settingsManager;
    private Map<String, String> profile;
    private CallCaregiver caregiver;


    private EmergencyManager(Context cont) {
        if (cont != null) {
            context = cont;
            ackReceived = new AtomicBoolean(false);
            callInProgress = new AtomicBoolean(false);
            stop = new AtomicBoolean(false);
            complete = new AtomicBoolean(false);
            MessageReceiver msgReceiver = new MessageReceiver(context);
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

    public void setComplete() {
        if (isCallInProgress()) {
            caregiver.disconnect();
            callInProgress.set(false);
        }
    }

    public void startEmergencyProtocol() {
        final LocationManagement locationManagement = LocationManagement.getInstance(context);


        ContactListManager contactListManager = ContactListManager.getInstance();
        final List<Contact> contactList = contactListManager.getContactList(context);

        profile = settingsManager.getProfile(context);

        final Handler handler = new Handler();

        stop.set(false);
        callInProgress.set(false);
        complete.set(false);
        ackReceived.set(false);

        handler.post(new Runnable() {
            Device device;
            int count = 0;

            @Override
            public void run() {
                Contact contact = contactList.get(count);
                if (stop.get()) {
                    Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ACTION_CALL_STATUS);
                    intent.putExtra(EXTRA_CONTACT_NAME, "Done");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else {
                    if (!isCallInProgress()) {
                        String phoneNumber = contact.getPhoneNumber();
                        String contactName = contact.getName();
                        String username = "";
                        Map<String, String> parameters = new HashMap<String, String>();
                        profile = settingsManager.getProfile(context);

                        if (profile != null) {
                            username = profile.get(SettingsManager.NAME);
                        }
                        if (!username.isEmpty()) {
                            parameters.put("Username", username);
                        }
                        if (!phoneNumber.isEmpty()) {
                            parameters.put("To", phoneNumber);
                        }


                        //Turn on speaker
//                        audioManager.setMode(AudioManager.MODE_IN_CALL);
//                        audioManager.setSpeakerphoneOn(true);

                        //Make call through Twilio
//                        device = caregiver.connect(parameters);

                        callInProgress.set(true);
                        Intent intent = new Intent(ACTION_CALL_STATUS);
                        intent.putExtra(EXTRA_CONTACT_NAME, contactName);
                        intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        Log.d("Service", "Intent Sent");

                        if (count < contactList.size() - 1) {
                            count++;
                            //break;
                        } else {
                            count = 0;
                        }
                    }
                    if (isAckReceived() && complete.get()) {

                        String location = locationManagement.getLocation();
                        //Send Text Message
                        if (location != null) {
                            String message = EMERGENCY_MESSAGE + "http://maps.google.com/?q=" + location;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(contact.getPhoneNumber(), null, message, null, null);
                        }


                        //Turn off speaker
//                        audioManager.setMode(AudioManager.MODE_NORMAL);
                        Toast.makeText(context, "Finished", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ACTION_CALL_STATUS);
                        intent.putExtra(EXTRA_CONTACT_NAME, "Done");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }else if ((isAckReceived() && !complete.get()) || (!isAckReceived() && !complete.get())) {
                        if (isAckReceived()) {
                            Intent intent = new Intent(ACTION_CALL_STATUS);
                            intent.putExtra(EXTRA_CONTACT_NAME, "Online");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }

                        handler.postDelayed(this, 2000);

                    }else if(!isAckReceived() && complete.get()){
                        callInProgress.set(false);
                        handler.postDelayed(this, 2000);
                    }
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

    public void stopEmergencyProtocol() {
        stop.set(true);
        callInProgress.set(false);
    }

}
