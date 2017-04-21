package com.example.simbon.testapp1;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Simbon on 2017/4/20.
 */

public class EquipmentClosed extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If the confirm button on the notification was clicked
//        NotifyService.isClosedEquipment = true;
        finish();
    }
}
