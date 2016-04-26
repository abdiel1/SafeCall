package emergency_protocol;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by Kenneth on 4/24/2016.
 */
public class ActivateSpeaker {
    private static TelephonyManager manager;
    private static StatePhoneReceiver listener;
    private static ActivateSpeaker activateSpeaker;
    private static final String TAG = "EmergencyManager" ;
    private static Context context;

    private ActivateSpeaker(Context cont){
        context = cont;
        listener = new StatePhoneReceiver(context);

        manager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public ActivateSpeaker getInstance(Context cont){
        if(activateSpeaker == null){
            activateSpeaker = new ActivateSpeaker(cont);
        }
        return activateSpeaker;
    }

    public class StatePhoneReceiver extends PhoneStateListener{
        Context context;
        public StatePhoneReceiver(Context cont){
            context = cont;
        }

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            super.onCellInfoChanged(cellInfo);
        }
    }


}
