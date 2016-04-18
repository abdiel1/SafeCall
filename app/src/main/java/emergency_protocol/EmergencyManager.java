package emergency_protocol;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.abdielrosado.safecall.R;

import java.util.List;

import twilio.CallCaregiver;
import contact_management.Contact;
import contact_management.ContactListManager;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class EmergencyManager extends AppCompatActivity{
    private static Context context;
    private static EmergencyManager emergencyManager;
    private static String TAG = "TWILIOMAINACTIVITY";



    private boolean ackReceived;
    private volatile boolean callInProgress;

    private CallCaregiver caregiver;

    public EmergencyManager() {

    }
    private EmergencyManager(Context cont) {
        context = cont;

//        MessageReceiver msgReceiver = new MessageReceiver(context);
        caregiver = CallCaregiver.getInstance(context);
    }

    public static EmergencyManager getInstance(Context cont){
        if(emergencyManager == null){
            emergencyManager = new EmergencyManager(cont);
        }
        return emergencyManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twilio_main);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);

        EmergencyManager emergencyManager = EmergencyManager.getInstance(getApplicationContext());
        emergencyManager.startEmergencyProtocol();

    }

    public boolean isCallInProgress() {
        return callInProgress;
    }

    public void setCallInProgress(boolean callInProgress) {
        this.callInProgress = callInProgress;
    }

    public void startEmergencyProtocol(){
        LocationManagement locationManagement = LocationManagement.getInstance(context);
        String location = locationManagement.getLocation();

        //Put send location to server here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        ContactListManager contactListManager = ContactListManager.getInstance();
        List<Contact> contactList = contactListManager.getContactList(context);

       // TextView status = (TextView) findViewById(R.id.call_status);

        int count = 0;
        while(!ackReceived){
            // Make the call
            if(!isCallInProgress()){
//                caregiver.connect(contactList.get(count).getPhoneNumber());
                callInProgress = true;
               // status.setText(contactList.get(count).getName());
                if(count < contactList.size() - 1){
                    count++;
                    //break;
                } else{
                    count = 0;
                }
            }

        }

    }

    public void onClickSetAck(View view){
        this.ackReceived = true;
    }

    public void onClickDone(View view){
        callInProgress = false;
    }

}
