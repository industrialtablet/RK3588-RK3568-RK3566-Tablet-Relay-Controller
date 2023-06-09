package com.szhyy.relay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Collections;
import java.util.List;

public class RelayService extends Service {


    private boolean isOpenSerial = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("owenstar", "onCreate !!");
        //PropertyConfigurator.configure("config/log4j.properties");

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String id = "1";
        String name = "channel_name_1";
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setSound(null, null);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentTitle("Relay")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_launcher_background).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Relay")
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_launcher_background).setColor(Color.parseColor("#0972EE"));
            notification = notificationBuilder.build();
        }
        startForeground(1, notification);
        intTtyUSB0();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    private void intTtyUSB0() {

        HyyRelayCtl hyyRelayCtl = HyyRelayCtl.instance();
        boolean opened = hyyRelayCtl.open(new HyyRelay.RelaySwitchingEventListener() {
            @Override
            public void on() {
                hyyRelayCtl.relayOn();
            }

            @Override
            public void off() {
                hyyRelayCtl.relayOff();
            }
        });

        Intent intent = new Intent();
        intent.setAction("com.szhyy.broadcaster.USB_STATE");
        if(opened){
            intent.putExtra("connect", "success");
        } else {
            intent.putExtra("connect", "failed");
        }
        sendBroadcast(intent);
    }

    public void launchApp(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if(resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i=new Intent(Intent.ACTION_MAIN);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        } else {
            Toast.makeText(this, packageName+" 未安装", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        HyyRelayCtl.instance().close();
        stopForeground(true);
        Intent intent = new Intent(this, BootstrapReceiver.class);
        intent.setAction("com.szhyy.relay.RELAY_SERVICE_DESTROY");
        sendBroadcast(intent);
        super.onDestroy();
    }
}