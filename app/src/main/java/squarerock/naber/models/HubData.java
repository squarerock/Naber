package squarerock.naber.models;

import com.google.gson.annotations.Expose;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class HubData {

    @Expose
    private String cameraId;
    @Expose
    private String wifiSSID;
    @Expose
    private String wifiPassword;

    public HubData(String cameraId, String wifiSSID, String wifiPassword) {
        this.cameraId = cameraId;
        this.wifiSSID = wifiSSID;
        this.wifiPassword = wifiPassword;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }
}
