package fr.insapp.insapp.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.*
import android.widget.Toast
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.User
import fr.insapp.insapp.notifications.FirebaseMessaging
import fr.insapp.insapp.utility.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 17/07/2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        initSummary(preferenceScreen)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
        val barcodeData = defaultSharedPreferences.getString("barcode", "")

        if (barcodeData != "") {
            findPreference("barcode_preferences").summary = barcodeData
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        updatePreferenceSummary(findPreference(key))

        when (key) {
            "name", "sex", "class", "email", "description" -> {

                // user change

                val user = Utils.user

                val updatedUser = User.create(
                        user!!.id,
                        sharedPreferences.getString("name", ""),
                        user.username,
                        sharedPreferences.getString("description", ""),
                        sharedPreferences.getString("email", ""),
                        user.isEmailPublic,
                        sharedPreferences.getString("class", ""),
                        sharedPreferences.getString("sex", ""),
                        user.events,
                        user.postsLiked)

                val call = ServiceGenerator.create().updateUser(user.id, updatedUser)
                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (!response.isSuccessful) {
                            Toast.makeText(App.getAppContext(), "SettingsFragment", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(App.getAppContext(), "SettingsFragment", Toast.LENGTH_LONG).show()
                    }
                })
            }

            "notifications" ->

                // notification change

                if (sharedPreferences.getBoolean("notifications", true))
                    FirebaseMessaging.subscribeToTopics()
                else
                    FirebaseMessaging.unsubscribeFromTopics()

            else -> {
            }
        }
    }

    private fun initSummary(preference: Preference) {
        if (preference is PreferenceGroup) {

            for (i in 0 until preference.preferenceCount) {
                initSummary(preference.getPreference(i))
            }
        } else {
            updatePreferenceSummary(preference)
        }
    }

    private fun updatePreferenceSummary(preference: Preference) {
        if (preference is ListPreference) {
            preference.setSummary(preference.entry)
        } else if (preference is EditTextPreference) {

            if (preference.text != null && !preference.text.isEmpty()) {
                preference.setSummary(preference.text)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()

        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        val ID = "SETTINGS_FRAGMENT"

        fun newInstance(id: String): SettingsFragment {
            val fragment = SettingsFragment()

            val args = Bundle()
            args.putString(SettingsFragment.ID, id)
            fragment.arguments = args

            return fragment
        }
    }
}
