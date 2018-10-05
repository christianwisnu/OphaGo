package utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by christian on 09/02/18.
 */

public class PrefUtil {

    private Activity activity;
    public static final String ID = "ID";
    public static final String NAME = "NAME";

    // Constructor
    public PrefUtil(Activity activity) {
        this.activity = activity;
    }

    public void clear() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveUserInfo(String name, String id){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME, name);
        editor.putString(ID, id);
        editor.apply();
    }

    public SharedPreferences getUserInfo(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        //Log.d("MyApp", "Name : "+prefs.getString("fb_name",null)+"\nEmail : "+prefs.getString("fb_email",null));
        return prefs;
    }
}
