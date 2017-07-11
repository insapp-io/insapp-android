package fr.insapp.insapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import fr.insapp.insapp.EventActivity;
import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Notification;

/**
 * Created by Antoine on 06/03/2017.
 */

public class MyFcmListenerService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 1;
    public static final String TAG = "GCM Demo";

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getBaseContext().class.getSimpleName(), getBaseContext().MODE_PRIVATE);
        System.out.println("RECU");
        if(preferences.getBoolean("notifications", true)) {
            Random random = new Random();
            NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;
            System.out.println("NOTIF ACTIVEE");
            sendMessageNotification(data);
        }
    }

/*
    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);
        //.setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"); wl.acquire(15000);


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification ID = " + NOTIFICATION_ID);
        Log.d(TAG, "Notification sent successfully.");
    }*/

    /**
     *  Cette méthode permet à partir des informations envoyées par le serveur * de notification de créer le message et la notification à afficher sur * le terminal de l'utilisateur. * *
     * @param extras les extras envoyés par le serveur de notification
     */
    private void sendMessageNotification(Map<String, String> data) {
        Log.d(TAG, "Preparing to send notification with message...: " + data.toString());
        // On crée un objet Message à partir des informations récupérées dans
        // le flux JSON du message envoyé par l'application server

        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        Notification msg = new Notification(bundle);


        // On associe notre notification à une Activity. Ici c'est l'activity
        // qui affiche le message à l'utilisateur
        PendingIntent contentIntent = null;

        // On récupère le gestionnaire de notification android
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String title = "Insapp";
        if(msg.getType().equals("tag"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, PostActivity.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        else if(msg.getType().equals("post"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, PostActivity.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        else if(msg.getType().equals("event"))
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, EventActivity.class).putExtra("notification", msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
        /*
        else
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        */

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setVibrate(new long[]{400, 400})
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg.getMessage()))
                        .setContentText(msg.getMessage())
                        .setAutoCancel(true)
                        .setLights(getResources().getColor(R.color.theme_red), 500, 1000)
                        .setDefaults(android.app.Notification.DEFAULT_SOUND);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(15000);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }

}
