package squarerock.naber.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.intentfilter.wificonnect.helpers.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import squarerock.naber.Constants;
import squarerock.naber.PreferencesManager;
import squarerock.naber.R;
import squarerock.naber.asynctasks.UDPSendTask;
import squarerock.naber.interfaces.IWifiDetailsPresenter;
import squarerock.naber.interfaces.NetworkStateChangeListener;
import squarerock.naber.interfaces.WifiConnectedCallback;
import squarerock.naber.presenters.WifiDetailsPresenter;
import squarerock.naber.receivers.WifiStateChangeReceiver;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class WiFiDetailsActivity extends AppCompatActivity implements WifiConnectedCallback,
        UDPSendTask.UDPSendCallback, NetworkStateChangeListener {

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
    private ConnectivityManager manager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private int networkId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);
        ButterKnife.bind(this);

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        presenter = new WifiDetailsPresenter(wifiManager);
        scanResults = getIntent().getStringArrayListExtra(Constants.EXTRA_SCAN_RESULTS);

        initSpinner();
        initReceiver();

        connectToWifi();
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
        if(wifiManager.getConnectionInfo().getSSID().equals(formatSSID("naber"))){
            jsonString = presenter.getJsonString(
                    PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_CAMERA),
                    ssidSelected,
                    et_wifi_password.getText().toString());
            sendData();
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
        //sendData();
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

    private void connectToWifi(){
        String ssid = PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_HUB);
        String password = Constants.HUB_PASSWORD;
        connectToSSID(ssid, password);
        bindToNetwork(ssid, this);
    }

    @TargetApi(LOLLIPOP)
    public void bindToNetwork(final String networkSSID, final NetworkStateChangeListener listener) {
        if (SDK_INT < LOLLIPOP) {
            Log.i(TAG, "bindToNetwork: Less than Lollipop");
            return;
        }

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        networkCallback = networkCallback(networkSSID, listener);
        manager.registerNetworkCallback(request, networkCallback);
    }

    @TargetApi(LOLLIPOP)
    private ConnectivityManager.NetworkCallback networkCallback(final String networkSSID,
                                                                final NetworkStateChangeListener listener) {
        return new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                NetworkInfo networkInfo = manager.getNetworkInfo(network);
                Log.i(TAG, "Network is Available. Network Info: " + networkInfo);

                if (areEqual(networkInfo.getExtraInfo(), networkSSID)) {
                    manager.unregisterNetworkCallback(this);
                    networkCallback = null;
                    bindToRequiredNetwork(network);
                    Log.i(TAG, String.format("Bound application to use %s network", networkSSID));
                    listener.onNetworkBound(); //do required stuff in this method
                }
            }
        };
    }

    @TargetApi(LOLLIPOP)
    public void bindToRequiredNetwork(Network network) {
        if (SDK_INT >= M) {
            manager.bindProcessToNetwork(network);
        } else {
            ConnectivityManager.setProcessDefaultNetwork(network);
        }
    }

    public boolean connectToSSID(String SSID, String password) {
        WifiConfiguration configuration = createOpenWifiConfiguration(SSID, password);
        networkId = wifiManager.addNetwork(configuration);
        Log.d(TAG, "networkId assigned while adding network is " + networkId);
        return enableNetwork(SSID, networkId);
    }

    private WifiConfiguration createOpenWifiConfiguration(String SSID, String password) {
        /*WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = formatSSID(SSID);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        assignHighestPriority(configuration);
        return configurationc
        */

        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = formatSSID(SSID);
        wc.preSharedKey = "\"" + password + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        assignHighestPriority(wc);
        return wc;
    }

    private boolean enableNetwork(String SSID, int networkId) {
        if (networkId == -1) {
            networkId = getExistingNetworkId(SSID);

            if (networkId == -1) {
                Log.e(TAG, "Couldn't add network with SSID: " + SSID);
                return false;
            }
        }
        return wifiManager.enableNetwork(networkId, true);
    }

    private int getExistingNetworkId(String SSID) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (areEqual(trimQuotes(existingConfig.SSID), trimQuotes(SSID))) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    //To tell OS to give preference to this network
    private void assignHighestPriority(WifiConfiguration config) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (config.priority <= existingConfig.priority) {
                    config.priority = existingConfig.priority + 1;
                }
            }
        }
    }

    private static String formatSSID(String wifiSSID) {
        return String.format("\"%s\"", wifiSSID);
    }

    private static String trimQuotes(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.replaceAll("^\"*", "").replaceAll("\"*$", "");
        }

        return str;
    }

    @Override
    public void onNetworkBound() {
        Log.d(TAG, "onNetworkBound: ");
        String ssid = PreferencesManager.getString(this, Constants.PREF_NABER, Constants.ITEM_HUB);
        String password = Constants.HUB_PASSWORD;

    }

    public boolean areEqual(String SSID, String anotherSSID) {
        return TextUtils.equals(StringUtil.trimQuotes(SSID), StringUtil.trimQuotes(anotherSSID));
    }
}
