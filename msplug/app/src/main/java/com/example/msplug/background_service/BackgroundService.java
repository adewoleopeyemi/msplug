package com.example.msplug.background_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.msplug.R;
import com.example.msplug.dashboard.view.dashboardActivity;

import static com.example.msplug.notification.App.CHANNEL_ID;
import static com.example.msplug.utils.Constants.serviceStatusConstants.ONLINE_STATUS;

public class BackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra(ONLINE_STATUS);

        Intent notificationIntent = new Intent(this, dashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MsPlug")
                .setContentText(input)
                .setSmallIcon(R.drawable.msplugnotificationicon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work in a background thread using intentService;
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
