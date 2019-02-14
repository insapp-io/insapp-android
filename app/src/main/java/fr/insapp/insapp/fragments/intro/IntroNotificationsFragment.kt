package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.notifications.NotificationUtils
import kotlinx.android.synthetic.main.fragment_intro_notifications.*

/**
 * Created by thomas on 03/12/2016.
 */

class IntroNotificationsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()
        defaultSharedPreferences.putBoolean("notifications_news", true)
        defaultSharedPreferences.putBoolean("notifications_events", true)
        defaultSharedPreferences.putBoolean("notifications_others", true)
        defaultSharedPreferences.apply()

        NotificationUtils.registerAllTopics(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkbox_enable_notifications?.isChecked = true

        checkbox_enable_notifications?.setOnCheckedChangeListener { _, b ->
            val defaultSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()
            defaultSharedPreferences.putBoolean("notifications_news", b)
            defaultSharedPreferences.putBoolean("notifications_events", b)
            defaultSharedPreferences.apply()

            NotificationUtils.registerAllTopics(b)
        }
    }
}
