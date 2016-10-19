package com.oceanstyxx.pddriver.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import static android.R.attr.id;

/**
 * Created by mohsin on 09/10/16.
 */

public class SessionManager
{
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PDDriverLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_DRIVER_NAME = "DRIVER_NAME";

    private static final String KEY_DRIVER_ID = "DRIVER_ID";

    private static final String KEY_DRIVER_CODE = "DRIVER_CODE";

    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn)
    {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn()
    {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setKeyDriverName(String driverName) {
        editor.putString(KEY_DRIVER_NAME, driverName);

        // commit changes
        editor.commit();
    }

    public String getDriverName(){
        return pref.getString(KEY_DRIVER_NAME,"");
    }


    public void setKeyDriverId(String driverId) {
        editor.putString(KEY_DRIVER_ID, driverId);

        // commit changes
        editor.commit();
    }

    public String getDriverId(){
        return pref.getString(KEY_DRIVER_ID,"");
    }


    public void setKeyDriverCode(String driverCode) {
        editor.putString(KEY_DRIVER_CODE, driverCode);

        // commit changes
        editor.commit();
    }

    public String getDriverCode(){
        return pref.getString(KEY_DRIVER_CODE,"");
    }
}

