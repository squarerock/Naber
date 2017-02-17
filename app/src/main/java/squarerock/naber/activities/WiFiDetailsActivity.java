package squarerock.naber.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
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

//    @BindView(R.id.et_wifi_ssid) TextInputEditText et_wifi_ssid;
    @BindView(R.id.spnr_wifi_ssid) Spinner spinner;
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
    private ArrayList<String> scanResults;
    private String ssidSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);
        ButterKnife.bind(this);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        presenter = new WifiDetailsPresenter(wifiManager);
        scanResults = getIntent().getStringArrayListExtra(Constants.EXTRA_SCAN_RESULTS);

        initSpinner();
        initReceiver();

    }

    private void initSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scanResults);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void sendData(){
        task = new UDPSendTask(this, wifiManager);
        task.execute(jsonString);
    }

    @OnClick(R.id.btnConfigure)
    public void btnConfigure() {
        hubNetworkId = presenter.addWifiNetwork(
                PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_HUB),
                Constants.HUB_PASSWORD);

        jsonString = presenter.getJsonString(
                PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_CAMERA),
                ssidSelected,
                et_wifi_password.getText().toString());

        /*WifiConnectionManager manager = new WifiConnectionManager(this);
        WifiConnectionManager.setBindingEnabled(true);
        manager.connectToAvailableSSID(PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_HUB), new WifiConnectionManager.ConnectionStateChangedListener() {
            @Override
            public void onConnectionEstablished() {
                Log.d(TAG, "onConnectionEstablished: ");
                Log.d(TAG, "getJsonString: "+jsonString);
                sendData();
            }

            @Override
            public void onConnectionError(String reason) {
                Log.d(TAG, "onConnectionError: "+reason);
            }
        });*/

        Log.d(TAG, "Executing UDP task");
        if(wifiManager.getConnectionInfo().getSSID().contains(Constants.ID_HUB)){
            Log.d(TAG, "btnConfigure: naber connected");
        } else {
            Log.d(TAG, "btnConfigure: Not connected to Naber");
        }
    }

    @OnItemSelected(R.id.spnr_wifi_ssid)
    public void spinnerItemSelected(Spinner spinner, int position){
        ssidSelected = spinner.getItemAtPosition(position).toString();
        Log.d(TAG, "spinnerItemSelected: "+ssidSelected);
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
        sendData();
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
        finish();
    }
}
