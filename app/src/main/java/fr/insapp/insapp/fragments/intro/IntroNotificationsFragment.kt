package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.notifications.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_intro_notifications.*

/**
 * Created by thomas on 03/12/2016.
 */

class IntroNotificationsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_intro_notifications, container, false)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()

        checkbox_enable_notifications.isChecked = true

        defaultSharedPreferences.putBoolean("notifications", true)
        defaultSharedPreferences.apply()

        FirebaseMessaging.subscribeToTopics()

        checkbox_enable_notifications.setOnCheckedChangeListener { _, b ->
            defaultSharedPreferences.putBoolean("notifications", b)
            defaultSharedPreferences.apply()

            if (b)
                FirebaseMessaging.subscribeToTopics()
            else
                FirebaseMessaging.unsubscribeFromTopics()
        }

        return rootView
    }
}
