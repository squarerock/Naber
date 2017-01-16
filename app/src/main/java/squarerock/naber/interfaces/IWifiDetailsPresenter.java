package squarerock.naber.interfaces;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public interface IWifiDetailsPresenter {
    int addWifiNetwork(String SSID, String password);
    boolean forgetWifiNetwork(int networkId);
    String getJsonString(String cameraId, String wifiSSID, String wifiPassword);
}
