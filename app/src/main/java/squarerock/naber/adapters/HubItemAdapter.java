package squarerock.naber.adapters;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import squarerock.naber.Constants;
import squarerock.naber.R;

/**
 * Created by pranavkonduru on 12/2/16.
 */

public class HubItemAdapter extends RecyclerView.Adapter<HubItemAdapter.HubViewHolder> {

    public interface HubItemClickCallback {
        void onShareClicked(int position);
        void onItemChosen(int position);
    }

    public interface DiscoveredItemsCallback{
        void onItemDiscovered(String type, String SSID);
    }

    private List<ScanResult> scanResults;
    private HubItemClickCallback callback;
    private DiscoveredItemsCallback discoveredItemsCallback;
    private boolean hubFound = false;
    private boolean cameraFound = true;

    private static final String TAG = "HubItemAdapter";

    public HubItemAdapter(List<ScanResult> scanResults, HubItemClickCallback hubItemClickCallback, DiscoveredItemsCallback discoveredItemsCallback) {
        this.scanResults = scanResults;
        this.callback = hubItemClickCallback;
        this.discoveredItemsCallback = discoveredItemsCallback;
    }

    @Override
    public HubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
        return new HubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HubViewHolder holder, int position) {
        ScanResult result = scanResults.get(position);
        holder.tvWifiId.setText(result.SSID);
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    public void clear(){
        scanResults.clear();
    }

    public void add(List<ScanResult> results){
        clear();
        for(ScanResult result : results){
            if(result.SSID.toUpperCase().startsWith(Constants.ID_HUB)){
                hubFound = true;
                scanResults.add(result);
                discoveredItemsCallback.onItemDiscovered(Constants.ITEM_HUB, result.SSID);
            } else if(result.SSID.toUpperCase().startsWith(Constants.ID_CAMERA)){
                cameraFound = true;
                scanResults.add(result);
                discoveredItemsCallback.onItemDiscovered(Constants.ITEM_CAMERA, result.SSID);
            }
        }
        notifyDataSetChanged();
    }

    public void addInFront(ScanResult result){
        scanResults.add(0, result);
        notifyItemInserted(0);
    }

    public class HubViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivHub) ImageView ivCamera;
        @BindView(R.id.tvWifiId) TextView tvWifiId;
        @BindView(R.id.ivShare) ImageView ivShare;

        public HubViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.ivHub, R.id.ivShare})
        public void onItemClicked(View view) {
            int position = getAdapterPosition();

            switch (view.getId()){
                case R.id.ivHub:
                    Log.d(TAG, "onItemClicked: "+itemView);
                    callback.onItemChosen(position);
                    break;
                case R.id.ivShare:
                    callback.onShareClicked(position);
                    break;
            }
        }
    }

    public ScanResult getItemAtPosition(int position){
        return scanResults.get(position);
    }

    public boolean isHubFound(){
        return hubFound;
    }

    public boolean isCameraFound(){
        return cameraFound;
    }
}
