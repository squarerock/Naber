package squarerock.naber.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import squarerock.naber.Constants;
import squarerock.naber.PreferencesManager;
import squarerock.naber.R;
import squarerock.naber.asynctasks.UDPSendTask;
import squarerock.naber.interfaces.IWifiDetailsPresenter;
import squarerock.naber.interfaces.WifiConnectedCallback;
import squarerock.naber.presenters.WifiDetailsPresenter;
import squarerock.naber.receivers.WifiStateChangeReceiver;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class WiFiDetailsActivity extends AppCompatActivity implements WifiConnectedCallback, UDPSendTask.UDPSendCallback {

    @BindView(R.id.et_wifi_ssid) TextInputEditText et_wifi_ssid;
    @BindView(R.id.et_wifi_password) TextInputEditText et_wifi_password;
    @BindView(R.id.btnConfigure) Button btnConnect;

    private IWifiDetailsPresenter presenter;
    private int hubNetworkId;
    private int cameraNetworkId;
    private static final String TAG = "WiFiDetailsActivity";
    private WifiStateChangeReceiver wifiStateChangeReceiver;
    private String jsonString;
    private UDPSendTask task;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);
        ButterKnife.bind(this);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        presenter = new WifiDetailsPresenter(wifiManager);
        initReceiver();
    }

    @OnClick(R.id.btnConfigure)
    public void btnConfigure() {
        jsonString = presenter.getJsonString(
                PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_CAMERA),
                et_wifi_ssid.getText().toString(),
                et_wifi_password.getText().toString());

        Log.d(TAG, "getJsonString: "+jsonString);

        hubNetworkId = presenter.addWifiNetwork(
                PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_HUB),
                Constants.HUB_PASSWORD);
    }

    private void initReceiver(){
        wifiStateChangeReceiver = new WifiStateChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateChangeReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(wifiStateChangeReceiver != null) {
            unregisterReceiver(wifiStateChangeReceiver);
        }

        if(task != null) {
            task.cancel(true);
        }
    }

    @Override
    public void onConnectedToHub() {
        Log.d(TAG, "onConnectedToHub: Executing UDP task");
        task = new UDPSendTask(this, wifiManager);
        task.execute(jsonString);
        /*if(task!= null) {
        }
        else{
            Log.d(TAG, "onConnectedToHub: Already executed task");
        }*/
    }

    @Override
    public void onDataSent() {
        presenter.forgetWifiNetwork(hubNetworkId);
        wifiManager.reconnect();
        changeMe();
    }

    private void changeMe(){

    }
}
