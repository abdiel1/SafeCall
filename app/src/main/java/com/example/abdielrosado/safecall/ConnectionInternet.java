package com.example.abdielrosado.safecall;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by Kenneth on 4/23/2016.
 */
public class ConnectionInternet {

    private Context context;

    public ConnectionInternet(Context context){
        this.context = context;
    }

    //Verify is there is internet connection
    public boolean isInternetConnection(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null){
            NetworkInfo information = connectivity.getActiveNetworkInfo();
            if(information != null){
                if(information.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
