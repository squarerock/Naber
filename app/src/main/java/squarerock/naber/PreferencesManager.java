package squarerock.naber;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pranavkonduru on 1/15/17.
 */

public class PreferencesManager {

    public static void putString(Context context, String preference, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String preference, String key){
        return context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(key, "");
    }
}
