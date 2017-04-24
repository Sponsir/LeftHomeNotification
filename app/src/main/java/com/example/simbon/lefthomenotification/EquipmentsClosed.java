package com.example.simbon.lefthomenotification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Simbon on 2017/4/20.
 */

public class EquipmentsClosed extends Activity {
    public static final String TAG = "EquipmentsClosed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Confirm button has clicked");
        // If the confirm button on the notification was clicked
        NotifyService.isCloseEquipments = true;

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        finish();
    }
}
