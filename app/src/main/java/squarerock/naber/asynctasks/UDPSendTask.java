package squarerock.naber.asynctasks;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class UDPSendTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "UDPSendTask";

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
        if(TextUtils.isEmpty(jsonMessage)){
            return null;
        }

        DatagramSocket sendingSocket = null;

        try {
            Log.d(TAG, "doInBackground: sleeping pre sending");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int localPort = -1;
        int serverPort = 34567;
        try {
            byte[] messageToBeSent = jsonMessage.getBytes();
            int messageLength = jsonMessage.length();

            sendingSocket = new DatagramSocket();
            localPort = sendingSocket.getLocalPort();

            InetAddress serverIP = InetAddress.getByName("192.168.4.111");
            Log.d(TAG, "doInBackground: "+serverIP.getHostAddress());
            Log.d(TAG, "doInBackground: Port: "+localPort);


            DatagramPacket sendingPacket = new DatagramPacket(messageToBeSent, messageLength, serverIP, serverPort);
            sendingSocket.send(sendingPacket);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
            localPort = -1;
        } finally {
            if (sendingSocket != null) {
                sendingSocket.close();
            }
        }

        // Receive response from server
        byte[] messageToBeReceived = new byte[1500];
        DatagramPacket receivingPacket = new DatagramPacket(messageToBeReceived, messageToBeReceived.length);
        DatagramSocket receivingSocket = null;
        try {
            receivingSocket = new DatagramSocket(localPort);
            receivingSocket.setReuseAddress(true);
            receivingSocket.setSoTimeout(1000);

            while(localPort != -1){
                try {
                    receivingSocket.receive(receivingPacket);
                    String text = new String(messageToBeReceived , 0, receivingPacket.getLength());
                    if(!TextUtils.isEmpty(text)){
                        localPort = -1;
                        Log.d(TAG, "doInBackground: text is: "+text);
                    }

                } catch (SocketTimeoutException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: ",e);
            e.printStackTrace();
        } finally {
            if (receivingSocket != null) {
                receivingSocket.close();
            }
        }

        try {
            Log.d(TAG, "doInBackground: sleeping post receive");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        callback.onDataSent();
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
