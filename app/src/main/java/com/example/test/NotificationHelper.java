package com.example.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper{
    public final String REMINDER_NOTIFICATION="reminder_notification";
    public final String REMINDER_NOTIFICATION_NAME="reminder_notification_name";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
            createNotifcation();
    }
    public void createNotifcation(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel reminder=new NotificationChannel(REMINDER_NOTIFICATION,REMINDER_NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            reminder.enableLights(true);
            reminder.enableVibration(true);
            reminder.setLightColor(R.color.colorPrimary);
            reminder.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(reminder);
        }
    }
    public NotificationManager getManager(){
            if(manager==null){
                manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            }
            return manager;
    }
    public NotificationCompat.Builder getReminderNotification(int IdNote,String Title,String Contents){

        Intent resultIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,IdNote,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), REMINDER_NOTIFICATION)
                .setContentTitle(Title)
                .setContentText(Contents)
                .setSmallIcon(R.drawable.ic_reminder)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        return builder;
    }
}
