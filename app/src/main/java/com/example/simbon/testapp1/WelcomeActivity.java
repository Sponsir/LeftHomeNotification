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
    public static final String TAG = "WelcomeActivity";

    private WifiManager wifiManager;
    private Button nextStep;
    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        nextStep = (Button)findViewById(R.id.next_step);
        nextStep.setOnClickListener(this);

        Context context = getApplicationContext();
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        // Create a thread to check if the wifi has been open and connected to a network, otherwise set the next button disable
        timer = new Timer();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure that other threads can be closed
        timer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                Log.d(TAG, "next_step clicked");
                // Stop the thread
                timer.cancel();

                // Jump to Setting activity
                Intent newActivity = new Intent(WelcomeActivity.this, SettingActivity.class);
                startActivity(newActivity);
                break;
            default:
                break;
        }
    }
}
