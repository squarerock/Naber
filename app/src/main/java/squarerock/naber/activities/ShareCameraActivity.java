package squarerock.naber.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import squarerock.naber.Constants;
import squarerock.naber.R;
import squarerock.naber.adapters.ContactListAdapter;
import squarerock.naber.models.CameraInvite;
/**
 * Created by pranavkonduru on 2/14/17.
 */

public class ShareCameraActivity extends Activity implements DatabaseReference.CompletionListener{


    @BindView(R.id.et_contact) MultiAutoCompleteTextView et_contact;
    @BindView(R.id.btn_share) Button btn_share;

    private String cameraId;
    private String contactAddress;
    private String contactMethod;
    private DatabaseReference cameraInviteRef;

    private static final String TAG = "ShareCameraActivity";
    public static final String[] PROJECTION =
            {
                    Email._ID,
                    Email.ADDRESS,
                    Email.TYPE,
                    Email.LABEL
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_camera);
        ButterKnife.bind(this);

        cameraId = getIntent().getStringExtra(Constants.EXTRA_SHARE_CAMERA_ID);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cameraInviteRef = database.getReference(Constants.CAMERA_INVITE_TABLE);

        ContentResolver content = getContentResolver();
        Cursor cursor = content.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, null);

        ContactListAdapter adapter = new ContactListAdapter(this, cursor, true);
        et_contact.setThreshold(0);
        et_contact.setAdapter(adapter);
        et_contact.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick(R.id.btn_share)
    public void btn_share() {
        contactAddress = et_contact.getText().toString();
        if(!validateContact(contactAddress)){
            Snackbar.make(findViewById(R.id.share_container), "Please check the entered contact", Snackbar.LENGTH_LONG).show();
            return;
        }

        CameraInvite ci = prepareCameraInvite();
        cameraInviteRef.push().setValue(ci, this);
//        cameraInviteRef.setValue(ci, this);
    }

    private CameraInvite prepareCameraInvite() {
        CameraInvite ci = new CameraInvite();
        ci.setContact(contactAddress);
        ci.setInvitedTo(cameraId);
        ci.setInvitedVia(contactMethod);
        ci.setInvitedBy("k.pranav.kumar@gmail.com");        // Get this from firebase auth object
        ci.setAccepted(false);
        return ci;
    }

    private boolean validateContact(String contact) {
        if(Patterns.EMAIL_ADDRESS.matcher(contact).matches()){
            contactMethod = "email";
            return true;
        } else if(Patterns.PHONE.matcher(contact).matches()){
            contactMethod = "phone";
            return true;
        }
        return false;
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Log.d(TAG, "onComplete: "+databaseError.getMessage());
        }

        finish();
    }
}
