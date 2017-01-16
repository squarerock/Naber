package squarerock.naber.presenters;

import android.net.wifi.WifiManager;

import squarerock.naber.interfaces.IMainPresenter;
import squarerock.naber.interfaces.views.MainView;

/**
 * Created by pranavkonduru on 12/2/16.
 */

public class MainPresenter implements IMainPresenter {

    private MainView view;

    public MainPresenter(MainView mainView) {
        this.view = mainView;
    }

    @Override
    public boolean startScanForWifi(WifiManager wifiManager) {
        return wifiManager.startScan();
    }
}
