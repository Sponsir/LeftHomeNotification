package com.example.simbon.lefthomenotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Simbon on 2017/3/22.
 */

public class NotifyService extends Service {
    public static final String TAG = "NotifyService";

    public static boolean isCloseEquipments = false;
    public static boolean isInHouse = false;

    private WifiManager wifiManager;
    private Context context;
    private boolean stopService = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() was called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() was called");

        // Get the wifi's name and thresholds saved before
        context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("wifi_name", Context.MODE_PRIVATE);
        final String WIFI_NAME = sharedPreferences.getString("wifi_name", null);
        final int MAX_THRESHOLD = sharedPreferences.getInt("max_threshold", Integer.MAX_VALUE);
        final int MIN_THRESHOLD = sharedPreferences.getInt("min_threshold", Integer.MAX_VALUE);

        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        // Start a thread to handle the main logic of this app
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopService) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    // If the user is not in the range, do noting
                    if (wifiInfo.getSSID().equals(WIFI_NAME)) {

                            int rssi = -wifiInfo.getRssi();
//                            Log.d(TAG, "Rssi: " + rssi + " FrontRssiAVG: " + frontRssiAvg);
                            // RSSI is larger than the average value of thresholds, which means that user is out of home.
                            // And the isInHouse is true, which means that user is going from house to outside
                            if (rssi > (MAX_THRESHOLD + MIN_THRESHOLD) / 2 && isInHouse) {
                                sendNotification();
                                isInHouse = false;
                            }
                            // If user is in home
                            if (rssi <= (MAX_THRESHOLD + MIN_THRESHOLD) / 2) {
                                isInHouse = true;
                                isCloseEquipments = false;

                                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancel(1);
                            }
                        }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendNotification() {
        // Initialize the custom notification
        Context context = getApplicationContext();
        NotificationManager notifyService = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "请关闭闲置用电器与燃气阀门");
        contentView.setTextViewText(R.id.text, "若已关闭请点击右侧按钮");
        contentView.setTextViewText(R.id.confirm, "我已关闭");

//        contentView.setBoolean(R.id.confirm, "setEnable", false);

        // Set the action when confirm button was clicked
        Intent confirmIntent = new Intent(context, EquipmentsClosed.class);
        PendingIntent confirmPendingIntent = PendingIntent.getActivity(context, 0, confirmIntent, 0);
        contentView.setOnClickPendingIntent(R.id.confirm, confirmPendingIntent);
        // Set the action when notification was touched
        Intent touchIntent = new Intent(context, MainActivity.class);
        PendingIntent touchPendingIntent = PendingIntent.getActivity(context, 0, touchIntent, 0);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCustomContentView(contentView);
        builder.setContentIntent(touchPendingIntent);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
        notifyService.notify(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() was called");
        stopService = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
