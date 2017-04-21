package com.example.simbon.testapp1;

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

import java.util.LinkedList;

/**
 * Created by Simbon on 2017/3/22.
 */

public class NotifyService extends Service {
    public static final String TAG = "NotifyService";
    private WifiManager wifiManager;
    private LinkedList<Integer> frontRssi;
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

        frontRssi = new LinkedList<>();

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
                        if (frontRssi.size() >= 5) {
                            int frontRssiAvg = 0;
                            int sum = 0;
                            for (int e : frontRssi) {
                                sum += e;
                            }
                            frontRssiAvg = sum / frontRssi.size();

                            int rssi = -wifiInfo.getRssi();
//                            Log.d(TAG, "Rssi: " + rssi + " FrontRssiAVG: " + frontRssiAvg);
                            // If RSSI is larger than the average value of thresholds, which means that user is out of home.
                            // And if the average value of front 5 RSSIs is less than max threshold, which means that user is go from home.
                            // And if the average value of front 5 RSSIs is less than RSSI, which is aimed to avoid user stand in the range
                            // between average threshold and max threshold.
                            if (rssi > (MAX_THRESHOLD + MIN_THRESHOLD) / 2 && frontRssiAvg < MAX_THRESHOLD && frontRssiAvg < rssi) {
                                // Initialize the custom notification
                                Context context = getApplicationContext();
                                NotificationManager notifyService = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
                                contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
                                contentView.setTextViewText(R.id.title, "Custom Notification");
                                contentView.setTextViewText(R.id.text, "This is a custom layout");
                                contentView.setTextViewText(R.id.confirm, "Confirm");

                                // Set the action when confirm button was clicked
                                Intent confirmIntent = new Intent(context, EquipmentClosed.class);
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
                        }
                        frontRssi.add(-wifiInfo.getRssi());
                        if (frontRssi.size() > 5) frontRssi.remove();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
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
