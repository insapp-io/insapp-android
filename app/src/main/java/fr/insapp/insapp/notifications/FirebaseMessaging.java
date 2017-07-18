package fr.insapp.insapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.activities.IntroActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.models.Notification;

/**
 * Created by thomas on 18/07/2017.
 */

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Firebase", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("Firebase", "Data: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("Firebase", "Body: " + remoteMessage.getNotification().getBody());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
            if (sharedPreferences.getBoolean("notifications", true)) {
                sendNotification(getRandomNotificationId(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
            }
        }
    }

    private void sendNotification(int notificationId, String body, Map<String, String> data) {
        Notification notification = Notification.create(data);

        if (notification != null) {
            PendingIntent pendingIntent;

            switch (notification.getType()) {
                case "tag":
                case "post":
                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, PostActivity.class)
                            .putExtra("notification", notification)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
                    break;

                case "event":
                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, EventActivity.class)
                            .putExtra("notification", notification)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
                    break;

                default:
                    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, IntroActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setLights(ContextCompat.getColor(App.getAppContext(), R.color.colorPrimary), 500, 1000)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

            builder.setDefaults(android.app.Notification.DEFAULT_SOUND | android.app.Notification.DEFAULT_VIBRATE);

            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Lock");
            wakeLock.acquire();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private int getRandomNotificationId() {
        return new Random().nextInt(9999 - 1000) + 1000;
    }
}
