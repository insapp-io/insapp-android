package fr.insapp.insapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.models.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 18/07/2017.
 */

class FirebaseMessaging : FirebaseMessagingService() {

    private val randomNotificationId: Int
        get() = Random().nextInt(9999 - 1000) + 1000

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(FirebaseService.TAG, "From: " + remoteMessage!!.from!!)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(FirebaseService.TAG, "Data: " + remoteMessage.data)

            sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!);
        } else {
            sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!);
        }
    }

    private fun buildNotificationFromData(title: String, body: String, data: Map<String, String>) {
        val notification = fr.insapp.insapp.models.Notification.create(data)

        if (notification != null) {
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
            if (!defaultSharedPreferences.getBoolean("notifications", true) && (notification.type == "post" || notification.type == "event")) {
                return
            }

            when (notification.type) {
                "tag", "post" -> {
                    val call1 = ServiceGenerator.create().getPostFromId(notification.content)
                    call1.enqueue(object : Callback<Post> {
                        override fun onResponse(call: Call<Post>, response: Response<Post>) {
                            if (response.isSuccessful) {
                                notification.post = response.body()
                            } else {
                                Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Post>, t: Throwable) {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                "eventTag", "event" -> {
                    val call2 = ServiceGenerator.create().getEventFromId(notification.content)
                    call2.enqueue(object : Callback<Event> {
                        override fun onResponse(call: Call<Event>, response: Response<Event>) {
                            if (response.isSuccessful) {
                                notification.event = response.body()
                            } else {
                                Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Event>, t: Throwable) {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                else -> {
                }
            }
        }
    }

    private fun sendNotification(title: String, body: String) {
        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel");

        builder
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setAutoCancel(true)
                .setLights(Color.RED, 500, 1000)
                .setColor(ContextCompat.getColor(App.getAppContext(), R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)

        manager.notify(randomNotificationId, builder.build());
    }

    private fun sendNotificationWithData(title: String, body: String, clickAction: String) {
        val intent = Intent(clickAction);

        // add data here

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel");

        builder
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setAutoCancel(true)
                .setLights(Color.RED, 500, 1000)
                .setColor(ContextCompat.getColor(App.getAppContext(), R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)

        manager.notify(randomNotificationId, builder.build());

        /*
        when (notification.type) {
            "eventTag", "tag" -> {
                val call1 = ServiceGenerator.create().getUserFromId(notification.sender)
                call1.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()

                            val id = resources.getIdentifier(Utils.drawableProfileName(user!!.promotion, user.gender), "drawable", App.getAppContext().packageName)
                            Glide
                                .with(App.getAppContext())
                                .asBitmap()
                                .load(id)
                                .into(notificationTarget)
                        } else {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                })
            }

            "post" -> {
                val call2 = ServiceGenerator.create().getClubFromId(notification.post.association)
                call2.enqueue(object : Callback<Club> {
                    override fun onResponse(call: Call<Club>, response: Response<Club>) {
                        if (response.isSuccessful) {
                            val club = response.body()

                            Glide
                                    .with(App.getAppContext())
                                    .asBitmap()
                                    .load(ServiceGenerator.CDN_URL + club!!.profilePicture)
                                    .into(notificationTarget)
                        } else {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Club>, t: Throwable) {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                })
            }

            "event" -> {
                val call3 = ServiceGenerator.create().getClubFromId(notification.event.association)
                call3.enqueue(object : Callback<Club> {
                    override fun onResponse(call: Call<Club>, response: Response<Club>) {
                        if (response.isSuccessful) {
                            val club = response.body()

                            Glide
                                    .with(App.getAppContext())
                                    .asBitmap()
                                    .load(ServiceGenerator.CDN_URL + club!!.profilePicture)
                                    .into(notificationTarget)
                        } else {
                            Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Club>, t: Throwable) {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                })
            }

            else -> {
            }
        }
        */
    }

    companion object {

        fun subscribeToTopics() {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("news")
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("events")
        }

        fun unsubscribeFromTopics() {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic("news")
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic("events")
        }
    }
}
