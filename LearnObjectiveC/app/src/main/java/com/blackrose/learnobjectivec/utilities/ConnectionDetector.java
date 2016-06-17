package com.blackrose.learnobjectivec.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mrnoo on 19/03/2016.
 */
public class ConnectionDetector {

    private Context mContext;

    public ConnectionDetector(Context context){
        this.mContext = context;
    }

    public boolean isNetworkAvaiable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfos[] = connectivityManager.getAllNetworkInfo();
            if (networkInfos.length > 0){
                for (int i = 0; i <networkInfos.length; i++){
                    Log.d("LIST_NETWORK: ", networkInfos[i].getTypeName());
                    if (networkInfos[i] != null && networkInfos[i].isConnected())
                        return true;
                }
            }
        }
        return false;
    }

    private Map<String, String> getConnectionDetails() {
        Map<String, String> networkDetails = new HashMap<String, String>();
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetwork = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetwork != null && wifiNetwork.isConnected()) {

                networkDetails.put("Type", wifiNetwork.getTypeName());
                networkDetails.put("Sub type", wifiNetwork.getSubtypeName());
                networkDetails.put("State", wifiNetwork.getState().name());
            }

            NetworkInfo mobileNetwork = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnected()) {
                networkDetails.put("Type", mobileNetwork.getTypeName());
                networkDetails.put("Sub type", mobileNetwork.getSubtypeName());
                networkDetails.put("State", mobileNetwork.getState().name());
                if (mobileNetwork.isRoaming()) {
                    networkDetails.put("Roming", "YES");
                } else {
                    networkDetails.put("Roming", "NO");
                }
            }
        } catch (Exception e) {
            networkDetails.put("Status", e.getMessage());
        }
        return networkDetails;
    }

}
