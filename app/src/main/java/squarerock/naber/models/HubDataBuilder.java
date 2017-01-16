package squarerock.naber.models;

public class HubDataBuilder {
    private String cameraId;
    private String wifiSSID;
    private String wifiPassword;

    public HubDataBuilder setCameraId(String cameraId) {
        this.cameraId = cameraId;
        return this;
    }

    public HubDataBuilder setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
        return this;
    }

    public HubDataBuilder setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
        return this;
    }

    public HubData createHubData() {
        return new HubData(cameraId, wifiSSID, wifiPassword);
    }
}