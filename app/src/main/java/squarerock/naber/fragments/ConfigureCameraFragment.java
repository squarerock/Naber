package squarerock.naber.fragments;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import squarerock.naber.R;

/**
 * Created by pranavkonduru on 12/3/16.
 */

// This class shows the dialog to accept configuration for the camera/hub that was chosen. Not in use currently
// Can be used on configure button in item of recycler-view

public class ConfigureCameraFragment extends DialogFragment {


    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tilUsername) TextInputLayout tilUsername;
    @BindView(R.id.tilPassword) TextInputLayout tilPassword;
    @BindView(R.id.btnCancel) Button btnCancel;
    @BindView(R.id.btnOk) Button btnOk;
    @BindView(R.id.etUsername) TextInputEditText etUsername;
    @BindView(R.id.etPassword) TextInputEditText etPassword;

    private String title;
    public static final String EXTRA_TITLE = "title";

    public ConfigureCameraFragment() {
    }

    public static ConfigureCameraFragment newInstance(String title){
        ConfigureCameraFragment frag = new ConfigureCameraFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString(EXTRA_TITLE, "Access Point SSID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configure_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        ButterKnife.bind(this, view);

        etUsername= (TextInputEditText) view.findViewById(R.id.etUsername);
        etPassword = (TextInputEditText) view.findViewById(R.id.etPassword);

        String cameraUsername = title.substring(title.lastIndexOf("_")+1);
        etUsername.setText(cameraUsername);
        etUsername.setFocusable(false);
        etPassword.requestFocus();

        tvTitle.setText(title);
        // Show soft keyboard automatically and request focus to field
        etUsername.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnClick(R.id.btnOk)
    public void btnOk() {
        if(isAdded()) {
            WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            // setup a wifi configuration
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"" + title + "\"";
            wc.preSharedKey = "\"" + etPassword.getText().toString() + "\"";
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
        }
        getDialog().dismiss();
    }

    @OnClick(R.id.btnCancel)
    public void btnCancel() {
        getDialog().dismiss();
    }

}
