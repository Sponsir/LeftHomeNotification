package com.example.simbon.testapp1;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
                    if (frontRssi.size() >= 5) {
                        int frontRssiAvg = 0;
                        int sum = 0;
                        for (int e : frontRssi) {
                            sum += e;
                        }
                        frontRssiAvg = sum / frontRssi.size();

                        if (wifiInfo.getSSID().equals(WIFI_NAME)) {
                            int rssi = -wifiInfo.getRssi();
                            Log.d(TAG, "Rssi: " + rssi + " FrontRssiAVG: " + frontRssiAvg);
                            if (rssi > (MAX_THRESHOLD + MIN_THRESHOLD) / 2 && frontRssiAvg < MAX_THRESHOLD && frontRssiAvg < rssi) {
                                NotificationManager notifyService = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                                builder.setContentTitle("Test Title")
                                        .setContentText("Test Content")
                                        .setSmallIcon(R.mipmap.ic_launcher);
                                notifyService.notify(1, builder.build());
                            }
                        }
                    }
                    frontRssi.add(-wifiInfo.getRssi());
                    if (frontRssi.size() > 5) frontRssi.remove();
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
