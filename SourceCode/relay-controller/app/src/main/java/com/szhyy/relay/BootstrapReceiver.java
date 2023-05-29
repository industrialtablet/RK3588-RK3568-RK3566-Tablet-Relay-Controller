package com.szhyy.relay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Objects;


public class BootstrapReceiver extends BroadcastReceiver {

    private static final String TAG = "BootstrapReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Utils.startRelayControllerService(context);
            //context.startService(bootServiceIntent);//startForegroundService
        } else if(Objects.equals(action, "com.szhyy.relay.RELAY_SERVICE_DESTROY")){
            Utils.startRelayControllerService(context);
        }
    }
}
