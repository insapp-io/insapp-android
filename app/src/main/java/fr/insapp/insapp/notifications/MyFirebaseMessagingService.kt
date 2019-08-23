package fr.insapp.insapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.NotificationUser
import fr.insapp.insapp.utility.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 18/07/2017.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationTokenToServer(token)
    }

    /**
     * Persist token to the server.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by the application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationTokenToServer(token: String?) {
        val user = Utils.user

        val notificationUser = NotificationUser(null, user?.id, token, "android")

        val call = ServiceGenerator.create().registerNotification(notificationUser)
        call.enqueue(object : Callback<NotificationUser> {
            override fun onResponse(call: Call<NotificationUser>, response: Response<NotificationUser>) {
                var msg = "Firebase token successfully registered on server: $token"
                if (!response.isSuccessful) {
                    msg = "Failed to register Firebase token on server"
                }
                Log.d(TAG, msg)
            }

            override fun onFailure(call: Call<NotificationUser>, t: Throwable) {
                Log.d(TAG, "Failed to register Firebase token on server")
            }
        })
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Data: " + remoteMessage.data)

            val notification = remoteMessage.notification
            sendNotification(notification?.title, notification?.body, notification?.clickAction, remoteMessage.data)
        }
    }

    private fun sendNotification(title: String?, body: String?, clickAction: String?, data: Map<String, String>?) {
        val intent = Intent(clickAction)
        if (data != null) {
            for ((key, value) in data) {
                intent.putExtra(key, value)
            }
        }

        var channel = "others"
        if(body?.contains("news")!!){
            channel = "posts"
        } else if(body.contains("invite")){
            channel = "events"
        }

        if(PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).getBoolean("notifications_$channel", true)){
            val pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(App.getAppContext(), channel)

            builder
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 500, 1000)
                    .setColor(ContextCompat.getColor(App.getAppContext(), R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)

            val manager: NotificationManager = App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.notify(randomNotificationId, builder.build())
        }
    }

    private val randomNotificationId: Int
        get() = Random().nextInt(9999 - 1000) + 1000

    companion object {
        const val TAG = "FA_DEBUG"

        private val topics = arrayOf(
            "posts-android",
            "events-android",
            "posts-unknown-class",
            "events-unknown-class"
        )

        fun subscribeToTopic(topic: String, flag: Boolean = true) {
            if (topic !in topics) {
                Log.d(TAG, "Topic $topic is unknown")
                return
            }

            if (flag) {
                Log.d(TAG, "Subscribing to topic $topic..")
                FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener { task ->
                    var msg = "Successfully subscribed to topic $topic"
                    if (!task.isSuccessful) {
                        msg = "Failed to subscribe to topic $topic"
                    }
                    Log.d(TAG, msg)
                }
            } else {
                Log.d(TAG, "Unsubscribing from topic $topic..")
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener { task ->
                    var msg = "Successfully unsubscribed from topic $topic"
                    if (!task.isSuccessful) {
                        msg = "Failed to unsubscribe from topic $topic"
                    }
                    Log.d(TAG, msg)
                }
            }
        }
    }

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
