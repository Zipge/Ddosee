package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver{

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String state = intent.getExtras().getString("state");

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        service_intent.putExtra("state", state);
        // start the ringtone service

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}