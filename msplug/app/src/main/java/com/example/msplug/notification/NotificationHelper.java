package com.example.msplug.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.msplug.R;
import com.example.msplug.dashboard.view.dashboardActivity;

import static com.example.msplug.utils.Constants.serviceStatusConstants.ONLINE_STATUS;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "response message";
    public static final String channelName = "Response";

    private NotificationManager Manager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }

    }


    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.colorComedyBlue);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }
    public  NotificationManager getManager(){
        if (Manager == null){
            Manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return Manager;
    }
    public NotificationCompat.Builder getChannelNotification(String message, String request_type){
        Intent notificationIntent = new Intent(this, dashboardActivity.class);
        notificationIntent.putExtra("frag_to_start", "messages");
        notificationIntent.putExtra("request_type", request_type);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("MsPlug")
                .setContentText(message)
                .setSmallIcon(R.drawable.msplugnotificationicon)
                .setContentIntent(pendingIntent);
    }
}
