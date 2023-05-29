package com.szhyy.relay;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Utils {

    public static void startRelayControllerService(Context context){
        Intent relayService = new Intent(context, RelayService.class);
        Log.d("owenstar", "startRelayControllerService ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(relayService);
        } else {
            context.startService(relayService);
        }
    }
}
