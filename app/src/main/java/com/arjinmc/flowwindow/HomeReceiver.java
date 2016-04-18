package com.arjinmc.flowwindow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * listen to home key boardcast
 * Created by Eminem on 2016/4/9.
 */
public class HomeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(SPUtil.getFlowWindowStatus(context)){
            Intent flowintent = new Intent(context, FloatWindowService.class);
            flowintent.setAction(FloatWindowService.STATUS_SHOW);
            context.startService(flowintent);
        }
    }
}
