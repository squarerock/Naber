package squarerock.naber.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import squarerock.naber.Constants;
import squarerock.naber.interfaces.WifiConnectedCallback;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class WifiStateChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiStateChangeReceiver";
    private WifiConnectedCallback callback;

    public WifiStateChangeReceiver(WifiConnectedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: "+action);
        if(!isInitialStickyBroadcast()) {
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (networkInfo != null && wifiInfo != null && networkInfo.isConnected()) {
                    if (wifiInfo.getSSID().toUpperCase().startsWith(Constants.ID_HUB)) {
                        Log.d(TAG, "onReceive: connected to hub");
                        callback.onConnectedToHub();
                    } else {
                        Log.d(TAG, "onReceive: connected to: " + wifiInfo.getSSID());
                    }
                }
            }
        }
    }
}
