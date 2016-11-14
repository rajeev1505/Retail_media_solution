package com.retail.solutions.pvt.Ltd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class BootReciever extends BroadcastReceiver {
    public BootReciever() {
    }

    // new change
    TextView tv;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // TODO Auto-generated method stub
        Intent myIntent = new Intent(context, MediaAdActivity
                .class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
