package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.insapp.insapp.App
import fr.insapp.insapp.R
import kotlinx.android.synthetic.main.fragment_intro_events.*

/**
 * Created by thomas on 03/12/2016.
 */

class IntroEventsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_intro_events, container, false)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()

        checkbox_enable_calendar.isChecked = false

        defaultSharedPreferences.putBoolean("calendar", false)
        defaultSharedPreferences.apply()

        checkbox_enable_calendar.setOnCheckedChangeListener { _, b ->
            defaultSharedPreferences.putBoolean("calendar", b)
            defaultSharedPreferences.apply()
        }

        return rootView
    }
}
