package my.edu.utar.sweetzmobileapp;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLoginManager {

    private static final String name = "UserLoginName";
    private static final String loginMode = "loginMode";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserLoginManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLoginMode(boolean isGuest) {
        editor.putBoolean(loginMode, isGuest);
        editor.apply();
    }

    public boolean isGuest() {
        return pref.getBoolean(loginMode, true);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}

