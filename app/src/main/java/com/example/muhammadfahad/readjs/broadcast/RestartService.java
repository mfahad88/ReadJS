package com.example.muhammadfahad.readjs.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muhammadfahad.readjs.service.MyService;


public class RestartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Service","Restart");
        context.startService(new Intent(context,MyService.class));
    }
}