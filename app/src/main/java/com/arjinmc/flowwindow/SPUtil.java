package com.arjinmc.flowwindow;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eminem Lu on 18/4/16.
 * Email arjinmc@hotmail.com
 */
public class SPUtil {

    private static final String FLOW_WINDOW_STATUS = "FLOW_WINDOW_STATUS";

    public static void saveFlowWindowStatus(Context context,boolean isOpen){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FLOW_WINDOW_STATUS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isOpen",isOpen);
        editor.commit();
    }

    public static boolean getFlowWindowStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FLOW_WINDOW_STATUS,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isOpen",false);
    }
}
