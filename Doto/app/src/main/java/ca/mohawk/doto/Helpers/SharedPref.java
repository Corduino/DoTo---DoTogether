package ca.mohawk.doto.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {


    Context context;
    public SharedPref(Context context) {
        this.context = context;
    }


    public void SaveSharedPref(String Name, String Value)
    {
        SharedPreferences.Editor editor =context.getSharedPreferences(Name,0).edit(); // 0 - for private mode
        editor.putString(Name,Value);
        editor.apply();
    }


    public String GetSharedPref(String Name)
    {
        SharedPreferences sh =context.getSharedPreferences(Name,0); // 0 - for private mode
        return sh.getString(Name,"");
    }

}

