package com.example.simbon.lefthomenotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by lenovo on 2017/4/24.
 */

public class LoadingActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
            default:
                break;
        }
    }
}
