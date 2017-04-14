package com.example.simbon.testapp1;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = "WelcomeActivity";
    private WifiManager wifiManager;
    private Button nextStep;
//    private Timer timer;
//    private Handler handler;
    private boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Context context = getApplicationContext();
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        nextStep = (Button)findViewById(R.id.next_step);
        nextStep.setOnClickListener(this);

        // Start a thread to check if wifi is enabled
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopThread) {
                    Log.d(TAG, "Wifi enabled check is running");

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiManager.isWifiEnabled()) {
                        if (wifiInfo.getSSID() == "<unknown ssid>") {
                            nextStep.setEnabled(false);
                            nextStep.setText("请连接WIFI后进行下一步");
                        }
                        else {
                            nextStep.setEnabled(true);
                            nextStep.setText("我已连接自家Wifi，下一步");
                        }
                    }
                    else {
                        nextStep.setEnabled(false);
                        nextStep.setText("请打开WIFI后进行下一步");
                    }
                }
            }
        });

/*
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Timer.run called");

                if (wifiManager.isWifiEnabled())
                {
                    Message msg = new Message();
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo.getSSID() == "<unknown ssid>") {
                        msg.what = 1;
                    }
                    else {
                        msg.what = 2;
                    }
                    handler.sendMessage(msg);
                }
                else {
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        }, 500, 500);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    nextStep.setEnabled(false);
                    nextStep.setText("请打开WIFI后进行下一步");
                }
                else if (msg.what == 1){
                    nextStep.setEnabled(false);
                    nextStep.setText("请链接WIFI后进行下一步");
                }
                else {
                    nextStep.setEnabled(true);
                    nextStep.setText("我已连接自家Wifi，下一步");
                }
            }
        };
        */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                Log.d(TAG, "next_step clicked");
                stopThread = true;
//                timer.cancel();
                Intent newActivity = new Intent(WelcomeActivity.this, SettingActivity.class);
                startActivity(newActivity);
                break;
            default:
                break;
        }
    }
}
