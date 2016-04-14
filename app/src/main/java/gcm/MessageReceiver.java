package gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.abdielrosado.safecall.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Kenneth on 4/14/2016.
 */
public class MessageReceiver{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MESSAGERECEIVER";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private Context context;

    private boolean isReceiverRegistered;

    public MessageReceiver(Context cont) {

        context = cont;

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
////                    mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
////                    mInformationTextView.setText(getString(R.string.token_error_message));
//                }
//            }
//        };
        // Registering BroadcastReceiver
        registerReceiver();
//        registerReceiver(broadcastReceiver, new IntentFilter(MyGcmListenerService.BROADCAST_ACTION));

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(context, RegistrationIntentService.class);
            context.startService(intent);
        }
    }


    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "Error in apiAvailability.");

            } else {
                Log.i(TAG, "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }

}
