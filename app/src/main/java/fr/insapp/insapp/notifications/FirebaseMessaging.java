package fr.insapp.insapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.EventActivity;
import fr.insapp.insapp.activities.IntroActivity;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 18/07/2017.
 */

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(FirebaseService.TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(FirebaseService.TAG, "Data: " + remoteMessage.getData());

            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
            if (defaultSharedPreferences.getBoolean("notifications", true)) {
                prepareNotification(getRandomNotificationId(), remoteMessage.getData());
            }
        }
    }

    private void prepareNotification(final int notificationId, Map<String, String> data) {
        final Notification notification = Notification.create(data);

        if (notification != null) {
            switch (notification.getType()) {
                case "tag":
                case "post":
                    Call<Post> call1 = ServiceGenerator.create().getPostFromId(notification.getContent());
                    call1.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                            if (response.isSuccessful()) {
                                notification.setPost(response.body());

                                final PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, new Intent(App.getAppContext(), PostActivity.class)
                                        .putExtra("post", notification.getPost())
                                        .putExtra("notification", notification)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);

                                final String title = notification.getPost().getTitle();

                                sendNotification(notificationId, title, notification, pendingIntent);
                            }
                            else {
                                Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;

                case "event":
                    Call<Event> call2 = ServiceGenerator.create().getEventFromId(notification.getContent());
                    call2.enqueue(new Callback<Event>() {
                        @Override
                        public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                            if (response.isSuccessful()) {
                                notification.setEvent(response.body());

                                final PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, new Intent(App.getAppContext(), EventActivity.class)
                                        .putExtra("event", notification.getEvent())
                                        .putExtra("notification", notification)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);

                                final String title = notification.getEvent().getName();

                                sendNotification(notificationId, title, notification, pendingIntent);
                            }
                            else {
                                Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;

                default:
                    final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, IntroActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    final String title = getResources().getString(R.string.app_name);

                    sendNotification(notificationId, title, notification, pendingIntent);
                    break;
            }
        }
    }

    private void sendNotification(int notificationId, String title, Notification notification, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(notification.getMessage())
                .setAutoCancel(true)
                .setLights(Color.RED, 500, 1000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        builder.setDefaults(android.app.Notification.DEFAULT_SOUND | android.app.Notification.DEFAULT_VIBRATE);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Lock");
        wakeLock.acquire();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    private int getRandomNotificationId() {
        return new Random().nextInt(9999 - 1000) + 1000;
    }
}
