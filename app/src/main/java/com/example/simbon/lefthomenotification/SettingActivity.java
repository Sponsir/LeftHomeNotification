package com.example.simbon.lefthomenotification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button okay = (Button)findViewById(R.id.okay);
        okay.setOnClickListener(this);
    }

    Timer timer;
    Handler handler;
    ProgressDialog dialog;
    int progress;
    int maxRssi;
    int minRssi;
    WifiManager wifiManager;
    boolean isDoor = true;
    int doorMax;
    int doorMin;
    String doorWifiName;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okay:
                // Create a dialog with a progress bar
                dialog = new ProgressDialog(SettingActivity.this);
                dialog.setProgress(0);
                dialog.setTitle("请等待片刻，我们正在校准WIFI信息");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(100);
                dialog.show();

                // Create a timer thread to show the progress and get the max and min value of rssi
                timer = new Timer();
                progress = 0;
                maxRssi = Integer.MIN_VALUE;
                minRssi = Integer.MAX_VALUE;
                Context context = getApplicationContext();
                wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Timer.run called");
                        progress++;
                        dialog.setProgress(progress);

                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        int rssi = -wifiInfo.getRssi();
                        if (rssi < minRssi) {
                            minRssi = rssi;
                        }
                        if (rssi > maxRssi) {
                            maxRssi = rssi;
                        }

                        if (progress == 100) {
                            Message msg = new Message();
                            handler.sendMessage(msg);
                        }
                    }
                }, 100, 100);
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        dialog.cancel();
                        timer.cancel();

                        // Check if set the configuration in front of the door
                        if (isDoor) {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            doorWifiName = wifiInfo.getSSID();
                            doorMax = maxRssi;
                            doorMin = minRssi;

                            // Modify the words
                            TextView textView = (TextView)findViewById(R.id.textView2);
                            textView.setText("现在，请走到您屋内距离WIFI路由器最远的地方，然后点击确定");
                            // Start the second configure
                            isDoor = false;
                        }
                        else {
                            String wifiName = wifiManager.getConnectionInfo().getSSID();
                            if (!doorWifiName.equals(wifiName)) {
                                Log.d(TAG, "Please make sure that you are using a same wifi in your house");
                            }
                            Context context = getApplicationContext();
                            ConfigureSave configureSave = new ConfigureSave(context);

                            // Save the lower rssi into the configuration file. Note that the higher rssi's absolute value , the lower wifi level
                            if (doorMax > maxRssi){
                                maxRssi = doorMax;
                            }
                            if (doorMin > minRssi) {
                                minRssi = doorMin;
                            }

                            configureSave.saveWifiNameAndRssi(wifiName, maxRssi, minRssi);

                            Intent newActivity = new Intent(SettingActivity.this, MainActivity.class);
                            startActivity(newActivity);
                        }
                    }
                };
                break;
            default:
                break;
        }
    }
}
