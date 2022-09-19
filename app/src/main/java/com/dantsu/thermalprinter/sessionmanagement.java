package com.dantsu.thermalprinter;



import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONObject;

public class sessionmanagement  {

    Context context;
    String SHARED_PREF="sharedprefer";
    String TEXT="text";

    public sessionmanagement(Context x){
   x=context;
    }
    public void ShareSession(JSONObject s, JSONObject d){
        SharedPreferences sharedPreferences =context.getSharedPreferences(SHARED_PREF,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, d.toString());
        editor.apply();
        Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show();
    }
}
class setting{
    public int activeOrderFlow;
}
class userdata{
    public int id;
    public String name;
    public String username;
    public String token;
    public int adminid;
}
