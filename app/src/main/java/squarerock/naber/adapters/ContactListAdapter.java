package squarerock.naber.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import squarerock.naber.activities.ShareCameraActivity;

/**
 * Created by pranavkonduru on 2/14/17.
 */

public class ContactListAdapter extends CursorAdapter implements Filterable {
    private ContentResolver mCR;

    public ContactListAdapter(Context context, Cursor c, boolean a) {
        super(context, c, true);
        mCR = context.getContentResolver();
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        ((TextView) view).setText(cursor.getString(1));
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate( android.R.layout.simple_dropdown_item_1line, parent, false);


        view.setText(cursor.getString(1));

        return view;

    }
    @Override
    public String convertToString(Cursor cursor) {



        return cursor.getString(1);
    }
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        StringBuilder buffer = null;
        String[] args = null;
        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append("UPPER(");
            buffer.append(ContactsContract.CommonDataKinds.Email.ADDRESS);
            buffer.append(") GLOB ?");
            args = new String[] { constraint.toString().toUpperCase() + "*" };
        }

        return mCR.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, ShareCameraActivity.PROJECTION ,buffer == null ? null : buffer.toString(), args,
                null);
    }

}
