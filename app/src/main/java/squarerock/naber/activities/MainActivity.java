package squarerock.naber.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import squarerock.naber.Constants;
import squarerock.naber.PreferencesManager;
import squarerock.naber.R;
import squarerock.naber.adapters.HubItemAdapter;
import squarerock.naber.interfaces.IMainPresenter;
import squarerock.naber.interfaces.views.MainView;
import squarerock.naber.presenters.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView,
        SwipeRefreshLayout.OnRefreshListener, HubItemAdapter.HubItemClickCallback, HubItemAdapter.DiscoveredItemsCallback {

    private IMainPresenter presenter;
    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private HubItemAdapter adapter;

    @BindView(R.id.rvCameras) RecyclerView rvCameras;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.btnNext) Button btnNext;

    private static final String TAG = "MainActivity";
    private BroadcastReceiver wifiDetectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new MainPresenter(this);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new HubItemAdapter(new ArrayList<ScanResult>(), this, this);
        rvCameras.setLayoutManager(new LinearLayoutManager(this));
        rvCameras.setAdapter(adapter);

        //initReceiver();
        initWifiDetectReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListOfCameras();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
        unregisterReceiver(wifiDetectReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayMessage(String errorMessage) {
        hideRefreshIcon();
        Snackbar.make(findViewById(R.id.container), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        updateListOfCameras();
    }

    private void updateListOfCameras() {
        if(wifiManager.isWifiEnabled()) {
            showRefreshIcon();
            if(presenter.startScanForWifi(wifiManager)) {
                registerForWifiDetails();
            }
        } else {
            displayMessage("Enable Wi-Fi");
        }
    }

    private void registerForWifiDetails(){
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiDetectReceiver, i);
    }

    private void showRefreshIcon() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshIcon() {
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onConfigureClicked(int position) {
        /*ScanResult scanResult = adapter.getItemAtPosition(position);
        FragmentManager fm = getSupportFragmentManager();
        ConfigureCameraFragment editNameDialogFragment = ConfigureCameraFragment.newInstance(scanResult.SSID);
        editNameDialogFragment.show(fm, "fragment_edit_name");*/
        displayMessage(adapter.getItemAtPosition(position).SSID);
    }

    @Override
    public void onItemChosen(int position) {
        /*ScanResult scanResult = adapter.getItemAtPosition(position);
        Intent intent = new Intent(this, CameraDetailActivity.class);
        intent.putExtra(CameraDetailActivity.EXTRA_SCAN_RESULT, scanResult);
        startActivity(intent);*/

        displayMessage(adapter.getItemAtPosition(position).SSID);
    }

    private void initWifiDetectReceiver(){
        wifiDetectReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                hideRefreshIcon();
                adapter.add(wifiManager.getScanResults());

                checkForHubAndCamera();
            }
        };
    }

    private void checkForHubAndCamera() {
        if(!adapter.isCameraFound() || !adapter.isHubFound()){
            displayMessage("Camera or hub not found. Try refreshing again");
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }

    @OnClick(R.id.btnNext)
    public void nextClicked(){
        startActivity(new Intent(this, WiFiDetailsActivity.class));
    }

    @Override
    public void onItemDiscovered(String type, String SSID) {
        // Save the items for later use
        PreferencesManager.putString(this, Constants.PREF_NABER, type, SSID);
    }
}
