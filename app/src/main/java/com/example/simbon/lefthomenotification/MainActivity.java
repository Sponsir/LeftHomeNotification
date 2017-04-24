package com.example.simbon.lefthomenotification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    private Button clearConfig;
    private TextView textView;
    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearConfig = (Button)findViewById(R.id.clear_config);
        clearConfig.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.mainLabel);

        // Check if the service is running
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = activityManager.getRunningServices(50);
        boolean serviceExist = false;
        for (int i = 0; i < serviceInfoList.size(); i++) {
            Log.d(TAG, "" + serviceInfoList.get(i).service.getClassName());
            if (serviceInfoList.get(i).service.getClassName().equals("com.example.simbon.testapp1.NotifyService")) {
                serviceExist = true;
            }
        }

        if (!serviceExist) {
            // First launching check
            Context context = getApplicationContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("wifi_name", Context.MODE_PRIVATE);
            String wifiName = sharedPreferences.getString("wifi_name", null);
            int maxThreshold = sharedPreferences.getInt("max_threshold", Integer.MIN_VALUE);
            int minThreshold = sharedPreferences.getInt("min_threshold", Integer.MIN_VALUE);
            Log.d(TAG, "Wifi name is: " + wifiName + "Max threshold is: " + maxThreshold + "Min threshold is: " + minThreshold);
            if (wifiName == null || maxThreshold < 0 || minThreshold < 0) {
                // First launching
                Log.d(TAG, "It's the first time to launch the app");

                // Jump to Welcome activity
                Intent newActivity = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(newActivity);
            } else {
                // Not first launching
                Log.d(TAG, "It's not the first time to launch the app");

                // Start service
                Intent startIntent = new Intent(this, NotifyService.class);
                startService(startIntent);
            }
        }

        // Create a thread to show the status of user
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                if (NotifyService.isInHouse) {
                    msg.obj = "您现在正在家中";
                }
                else {
                    if (NotifyService.isCloseEquipments) {
                        msg.obj = "您已关闭闲置用电器与燃气阀门";
                    }
                    else {
                        msg.obj = "您还没有关闭闲置用电器与燃气阀门";
                    }
                }
                handler.sendMessage(msg);
            }
        }, 50, 50);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                textView.setText((String)msg.obj);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If this activity was destroyed, free it's thread
        timer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_config:
                Log.d(TAG, "clear_config clicked");
                // Stop the service
                Intent stopIntent = new Intent(this, NotifyService.class);
                stopService(stopIntent);

                // Clear the information saved before
                Context context = getApplicationContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("wifi_name", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                // Stop the thread used to show the status of user
                timer.cancel();

                // Jump to Welcome activity
                Intent newActivity = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(newActivity);
                break;
            default:
                break;
        }
    }
}
