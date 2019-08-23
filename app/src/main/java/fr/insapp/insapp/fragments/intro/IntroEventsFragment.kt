package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import kotlinx.android.synthetic.main.fragment_intro_events.view.*

/**
 * Created by thomas on 03/12/2016.
 */

class IntroEventsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()
        defaultSharedPreferences.putBoolean("calendar", false)
        defaultSharedPreferences.apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.checkbox_enable_calendar?.isChecked = false

        view.checkbox_enable_calendar?.setOnCheckedChangeListener { _, b ->
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit()
            defaultSharedPreferences.putBoolean("calendar", b)
            defaultSharedPreferences.apply()
        }
    }
}
