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

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications_$channel", true)){
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channel)

            builder
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 500, 1000)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)

            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.notify(randomNotificationId, builder.build())
        }
    }

    private val randomNotificationId: Int
        get() = Random().nextInt(9999 - 1000) + 1000

    companion object {
        const val TAG = "FA_DEBUG"

        private val topics = arrayOf(
            "posts-android",
            "posts-unknown-class",
            "posts-1STPI",
            "posts-2STPI",
            "posts-3CDTI",
            "posts-4CDTI",
            "posts-5CDTI",
            "posts-3EII",
            "posts-4EII",
            "posts-5EII",
            "posts-3GM",
            "posts-4GM",
            "posts-5GM",
            "posts-3GMA",
            "posts-4GMA",
            "posts-5GMA",
            "posts-3GCU",
            "posts-4GCU",
            "posts-5GCU",
            "posts-3INFO",
            "posts-4INFO",
            "posts-5INFO",
            "posts-3SGM",
            "posts-4SGM",
            "posts-5SGM",
            "posts-3SRC",
            "posts-4SRC",
            "posts-5SRC",
            "posts-STAFF",
            "events-android",
            "events-unknown-class",
            "events-1STPI",
            "events-2STPI",
            "events-3CDTI",
            "events-4CDTI",
            "events-5CDTI",
            "events-3EII",
            "events-4EII",
            "events-5EII",
            "events-3GM",
            "events-4GM",
            "events-5GM",
            "events-3GMA",
            "events-4GMA",
            "events-5GMA",
            "events-3GCU",
            "events-4GCU",
            "events-5GCU",
            "events-3INFO",
            "events-4INFO",
            "events-5INFO",
            "events-3SGM",
            "events-4SGM",
            "events-5SGM",
            "events-3SRC",
            "events-4SRC",
            "events-5SRC",
            "events-STAFF"
        )

        fun subscribeToTopic(topic: String, flag: Boolean = true, forceSubscribe: Boolean = false) {
            if (!forceSubscribe) {
                if (topic !in topics) {
                    Log.d(TAG, "Topic $topic is unknown")
                    return
                }
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

        /**
         * Persist token to the server.
         *
         * Modify this method to associate the user's FCM InstanceID token with any server-side account
         * maintained by the application.
         *
         * @param token The new token.
         */
        fun sendRegistrationTokenToServer(token: String) {
            val user = Utils.user
            if (user != null) {
                val notificationUser = NotificationUser(null, user.id, token, "android")

                val call = ServiceGenerator.client.registerNotification(notificationUser)
                call.enqueue(object : Callback<NotificationUser> {
                    override fun onResponse(call: Call<NotificationUser>, response: Response<NotificationUser>) {
                        var msg = "Firebase token successfully registered on server: $token"
                        if (!response.isSuccessful) {
                            msg = "Failed to register Firebase token on server"
                        }
                        Log.d(TAG, msg)
                    }

                    override fun onFailure(call: Call<NotificationUser>, t: Throwable) {
                        Log.d(TAG, "Failed to register Firebase token on server: network failure")
                    }
                })
            } else {
                Log.d(TAG, "Failed to register Firebase token on server: user is null")
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
            val call2 = ServiceGenerator.create().getAssociationFromId(notification.post.association)
            call2.enqueue(object : Callback<Association> {
                override fun onResponse(call: Call<Association>, response: Response<Association>) {
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

                override fun onFailure(call: Call<Association>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                }
            })
        }

        "event" -> {
            val call3 = ServiceGenerator.create().getAssociationFromId(notification.event.association)
            call3.enqueue(object : Callback<Association> {
                override fun onResponse(call: Call<Association>, response: Response<Association>) {
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

                override fun onFailure(call: Call<Association>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                }
            })
        }

        else -> {
        }
    }
    */
}
