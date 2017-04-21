package com.example.simbon.testapp1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = "MainActivity";

    private Button clearConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearConfig = (Button)findViewById(R.id.clear_config);
        clearConfig.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_config:
                Log.d(TAG, "clear_config clicked");
                Intent stopIntent = new Intent(this, NotifyService.class);
                stopService(stopIntent);
                Context context = getApplicationContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("wifi_name", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent newActivity = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(newActivity);
                break;
            default:
                break;
        }
    }
}
