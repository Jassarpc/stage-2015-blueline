package mg.blueline.gulfsat.argusfinder.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mg.blueline.gulfsat.argusfinder.login.LoginActivity;


/**
 * Created by hamidullah on 9/20/15.
 */
public class SessionManagement {



    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;


    public static final String PREF_NAME = "droidSession";

    public static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String SESSION_USERNAME= "username";
    public static final String SESSION_PASSWORD= "password";
    public static final String SESSION_LAT="latitude";
    public static final String SESSION_LONG="longitude";
    public static final String SESSION_MAIL="mail";
    public static final String SESSION_NAME="name";


    public SessionManagement(Context context) {
        this._context = context;

        pref = PreferenceManager.getDefaultSharedPreferences(_context);
        //pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String username,String password,String name,String mail,String lat,String lon) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(SESSION_USERNAME, username);
        editor.putString(SESSION_PASSWORD, password);
        editor.putString(SESSION_LAT, lat);
        editor.putString(SESSION_LONG, lon);
        editor.putString(SESSION_MAIL,mail);
        editor.putString(SESSION_NAME,name);

        editor.commit();

        Log.d("Session", "User login session modified!");
    }

    public void setLogout(Context c){

        editor=pref.edit();
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.commit();
        Intent i = new Intent(c.getApplicationContext(), LoginActivity.class);
        c.startActivity(i);

    };
    public List<String> getUserSession(){

         List<String> info = new ArrayList<>();

        info.add(pref.getString(SESSION_USERNAME, null));
        info.add(pref.getString(SESSION_PASSWORD,null));
        info.add(pref.getString(SESSION_LAT, null));
        info.add(pref.getString(SESSION_LONG, null));
        info.add(pref.getString(SESSION_MAIL,null));
        info.add(pref.getString(SESSION_NAME,null));

        return info;
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
