package squarerock.naber.interfaces;

import com.google.firebase.database.DatabaseError;

import java.util.List;

import squarerock.naber.models.CameraInvite;

/**
 * Created by pranavkonduru on 2/14/17.
 */

public interface ICameraInviteCallback {
    void cameraInvitesFound(List<CameraInvite> invites);
    void cameraInvitesNotFound();
    void operationCancelled(DatabaseError error);
}
