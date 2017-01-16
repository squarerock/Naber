package squarerock.naber.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import squarerock.naber.R;

/**
 * Created by pranavkonduru on 12/4/16.
 */

// This class shows the video feed from the camera. Not in use currently

public class CameraFeedActivity extends AppCompatActivity {

    VideoView videoView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Create a VideoView widget in the layout file
        //use setContentView method to set content of the activity to the layout file which contains videoView
        this.setContentView(R.layout.activity_camera_feed);

        videoView = (VideoView)this.findViewById(R.id.vvCameraFeed);

        //add controls to a MediaPlayer like play, pause.
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        //Set the path of Video or URI
        videoView.setVideoURI(Uri.parse("rtsp://192.168.1.1:554/onvif1"));
        //

        //Set the focus
        videoView.requestFocus();
    }
}
