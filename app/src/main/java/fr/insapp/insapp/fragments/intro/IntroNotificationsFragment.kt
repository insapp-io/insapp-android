package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.notifications.MyFirebaseMessagingService
import kotlinx.android.synthetic.main.fragment_intro_notifications.view.*

/**
 * Created by thomas on 03/12/2016.
 */

class IntroNotificationsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity).edit()
        defaultSharedPreferences.putBoolean("notifications_posts", true)
        defaultSharedPreferences.putBoolean("notifications_events", true)
        defaultSharedPreferences.apply()

        MyFirebaseMessagingService.subscribeToTopic("posts-android")
        MyFirebaseMessagingService.subscribeToTopic("events-android")

        MyFirebaseMessagingService.subscribeToTopic("posts-unknown-class")
        MyFirebaseMessagingService.subscribeToTopic("events-unknown-class")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.checkbox_enable_notifications?.isChecked = true

        view.checkbox_enable_notifications?.setOnCheckedChangeListener { _, b ->
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()
            defaultSharedPreferences.putBoolean("notifications_posts", b)
            defaultSharedPreferences.putBoolean("notifications_events", b)
            defaultSharedPreferences.apply()

            MyFirebaseMessagingService.subscribeToTopic("posts-android", b)
            MyFirebaseMessagingService.subscribeToTopic("events-android", b)
        }
    }
}
