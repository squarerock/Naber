package squarerock.naber.presenters;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.google.gson.Gson;

import squarerock.naber.interfaces.IWifiDetailsPresenter;
import squarerock.naber.models.HubData;
import squarerock.naber.models.HubDataBuilder;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class WifiDetailsPresenter implements IWifiDetailsPresenter {

    private WifiManager wifiManager;

    public WifiDetailsPresenter(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    @Override
    public int addWifiNetwork(String SSID, String password) {
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + SSID + "\"";
        wc.preSharedKey = "\"" + password + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int netId = wifiManager.addNetwork(wc);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
        wifiManager.reconnect();

        return netId;
    }

    @Override
    public boolean forgetWifiNetwork(int networkId) {
        return wifiManager.disableNetwork(networkId);
    }

    @Override
    public String getJsonString(String cameraId, String wifiSSid, String wifiPassword){
        Gson gson = new Gson();
        HubData data = new HubDataBuilder()
                .setCameraId(cameraId)
                .setWifiSSID(wifiSSid)
                .setWifiPassword(wifiPassword)
                .createHubData();
        String hubJsonData = gson.toJson(data);
        return hubJsonData;
    }
}
