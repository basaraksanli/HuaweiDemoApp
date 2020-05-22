package com.example.huaweidemoapp.Controllers.MapControllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.R;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Timer;

public class NotificationController {
    private static int counter=10;

    Handler handler = new Handler();

    public NotificationController() {
        Timer timer = new Timer(false);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel(NotificationManager notificationManager) {
        String CHANNEL_ID = "distanceNotification";
        String CHANNEL_NAME = "Distance Notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.canShowBadge();
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification(Context context, NotificationManager notificationManager) {
        String CHANNEL_ID = "distanceNotification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int NOTIFICATION_ID = 52;
            Notification notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Warning:")
                    .setSmallIcon(R.drawable.ic_dialog_alert)
                    .setContentText("Please get back to original position")
                    .setAutoCancel(true)
                    .build();
            if (counter <= 0) {
                notificationManager.notify(NOTIFICATION_ID, notification);
                counter = 10;
            }
            if (counter > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        counter--;
                    }
                }, 1000L);
            }
        } else {
            if (counter <= 0) {
                Toast.makeText(context, "Please get back to original position", Toast.LENGTH_SHORT).show();
                counter = 10;
            }
            if (counter > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        counter--;
                    }
                }, 1000L);
            }

        }
    }
}
