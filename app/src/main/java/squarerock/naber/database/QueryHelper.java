package squarerock.naber.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import squarerock.naber.interfaces.ICameraInviteCallback;
import squarerock.naber.models.CameraInvite;

/**
 * Created by pranavkonduru on 2/14/17.
 */

public class QueryHelper {

    private String username = "Test@google.com";
    private static final String TAG = "QueryHelper";

    public void getCameraInvitesForUser(final ICameraInviteCallback callback){
        Queries queries = new Queries();
        queries.getCamerasForUser(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<CameraInvite> invites = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if(!children.iterator().hasNext()){
                    Log.d(TAG, "onDataChange: No camera invites found");
                    callback.cameraInvitesNotFound();
                } else {
                    Log.d(TAG, "onDataChange: camera invites were found");
                    for(DataSnapshot child: children){
                        CameraInvite cameraInvite = child.getValue(CameraInvite.class);
                        invites.add(cameraInvite);
                        Log.d(TAG, "onDataChange: "+cameraInvite.getContact());
                    }
                    callback.cameraInvitesFound(invites);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                callback.operationCancelled(databaseError);
            }
        });

    }
}
