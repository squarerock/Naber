package squarerock.naber.activities;

import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import squarerock.naber.R;

/**
 * Created by pranavkonduru on 12/3/16.
 */

// This class shows the details about the camera/hub that was chosen. Not in use currently

public class CameraDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvBssid) TextView tvBssid;
    @BindView(R.id.tvCapabilities) TextView tvCapabilities;
    @BindView(R.id.tvLevel) TextView tvLevel;
    @BindView(R.id.tvVenueName) TextView tvVenueName;
    @BindView(R.id.tvSsid) TextView tvSsid;
    @BindView(R.id.ivProfile) ImageView ivProfile;

    public static final String EXTRA_SCAN_RESULT = "scanResult";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detail);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ScanResult scanResult = getIntent().getParcelableExtra(CameraDetailActivity.EXTRA_SCAN_RESULT);

        if(scanResult != null){
            tvBssid.setText(String.format(Locale.ENGLISH, "MAC address: %s",scanResult.BSSID));
            tvSsid.setText(String.format(Locale.ENGLISH, "SSID: %s", scanResult.SSID));
            tvCapabilities.setText(String.format(Locale.ENGLISH, "Auth management: %s", scanResult.capabilities));
            tvLevel.setText(String.format(Locale.ENGLISH, "Signal Strength: %s dBm", String.valueOf(scanResult.level)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!TextUtils.isEmpty(scanResult.venueName)) {
                    tvVenueName.setText(String.format(Locale.ENGLISH, "Venue: %s", scanResult.venueName));
                } else {
                    tvVenueName.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
