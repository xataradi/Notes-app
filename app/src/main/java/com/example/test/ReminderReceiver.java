package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int idNote=intent.getIntExtra("idNote",0);
        String title=intent.getStringExtra("title");
        String contents=intent.getStringExtra("contents");
        NotificationHelper notificationHelper= new NotificationHelper(context);
        NotificationCompat.Builder nb=notificationHelper.getReminderNotification(idNote,title,contents);
        notificationHelper.getManager().notify(1,nb.build());

    }
}
