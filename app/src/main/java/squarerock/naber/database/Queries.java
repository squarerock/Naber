package squarerock.naber.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import squarerock.naber.Constants;

/**
 * Created by pranavkonduru on 2/14/17.
 */

public class Queries {

    private FirebaseDatabase database;

    public Queries() {
        database = FirebaseDatabase.getInstance();
    }

    public Query getCamerasForUser(String username){
        DatabaseReference reference = database.getReference().child(Constants.CAMERA_INVITE_TABLE);
        return reference.orderByChild("contact").equalTo(username.toLowerCase());
    }
}
