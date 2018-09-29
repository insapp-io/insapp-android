package fr.insapp.insapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import java.util.*
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.NotificationUser
import fr.insapp.insapp.utility.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
/**
 * Created by Pierre on 29/09/2018.
 */

class NotificationUtils {

    companion object {
        val channels = arrayOf("news", "events", "others")

        fun sendNotification(title: String?, body: String?, clickAction: String?, data: Map<String, String>?) {
            val intent = Intent(clickAction)
            if (data != null) {
                for ((key, value) in data) {
                    intent.putExtra(key, value)
                }
            }

            var channel = "others"
            if(body?.contains("news")!!){
                channel = "news"
            } else if(body?.contains("invite")!!){
                channel = "events"
            }

            if(PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).getBoolean("notifications_$channel", true)){
                val pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT)

                val manager: NotificationManager = App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

                manager.notify(randomNotificationId, builder.build())
            }
        }

        fun registerAllTopics(enable: Boolean){
            if (enable) {
                for(channel in channels){
                    FirebaseMessaging.subscribeToTopics(channel)
                }
            } else {
                for(channel in channels){
                    FirebaseMessaging.subscribeToTopics(channel)
                }
            }
        }

        private val randomNotificationId: Int
            get() = Random().nextInt(9999 - 1000) + 1000

    }

}
