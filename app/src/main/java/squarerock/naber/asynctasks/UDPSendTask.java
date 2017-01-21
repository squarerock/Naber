package squarerock.naber.asynctasks;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import squarerock.naber.Constants;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class UDPSendTask extends AsyncTask<String, Void, Void> {

    public interface UDPSendCallback{
        void onDataSent();
    }

    private UDPSendCallback callback;
    private WifiManager wifiManager;

    public UDPSendTask(UDPSendCallback callback, WifiManager wifiManager) {
        this.callback = callback;
        this.wifiManager = wifiManager;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String jsonMessage = strings[0];
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
            s.setBroadcast(true);

            String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
            InetAddress local = getBroadcastAddress();

//            int msg_length= jsonMessage.length();
            byte[] message = jsonMessage.getBytes();

            DatagramPacket p = new DatagramPacket(message, message.length, local, Constants.HUB_BROADCAST_UDP_PORT);
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(s != null && s.isConnected()){
                s.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.onDataSent();
    }

    InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        // handle null somehow
        if(dhcp != null) {
            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            return InetAddress.getByAddress(quads);
        } else {
            return InetAddress.getByName(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        }
    }
}
