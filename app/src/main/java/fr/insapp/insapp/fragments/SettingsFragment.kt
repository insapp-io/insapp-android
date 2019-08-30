package fr.insapp.insapp.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.User
import fr.insapp.insapp.notifications.MyFirebaseMessagingService
import fr.insapp.insapp.utility.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 17/07/2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())

        val barcode = defaultSharedPreferences.getString("barcode", "")
        if (!barcode.isNullOrEmpty()) {
            findPreference<Preference>("barcode_fragment")?.summary = barcode
        }

        val systemNotifications = findPreference<PreferenceScreen>("notifications_system")
        systemNotifications?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            //for Android 5-7
            intent.putExtra("app_package", activity?.packageName)
            intent.putExtra("app_uid", activity?.applicationInfo?.uid)
            // for Android 8 and above
            intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)
            startActivity(intent)
            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "name", "gender", "class", "email", "description" -> {

                // user change
                val user = Utils.user

                val updatedUser = User(
                        user!!.id,
                        sharedPreferences.getString("name", ""),
                        user.username,
                        sharedPreferences.getString("description", ""),
                        sharedPreferences.getString("email", ""),
                        user.isEmailPublic,
                        sharedPreferences.getString("class", ""),
                        sharedPreferences.getString("gender", ""),
                        user.events,
                        user.postsLiked)

                val call = ServiceGenerator.create().updateUser(user.id, updatedUser)
                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (!response.isSuccessful) {
                            Toast.makeText(App.getAppContext(), TAG, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(App.getAppContext(), TAG, Toast.LENGTH_LONG).show()
                    }
                })
            }

            "notifications_posts" -> {
                Log.d(TAG, "$key has changed and is now ${sharedPreferences.getBoolean(key, true)}")
                MyFirebaseMessagingService.subscribeToTopic("posts-android", sharedPreferences.getBoolean(key, true))
            }

            "notifications_events" -> {
                Log.d(TAG, "$key has changed and is now ${sharedPreferences.getBoolean(key, true)}")
                MyFirebaseMessagingService.subscribeToTopic("events-android", sharedPreferences.getBoolean(key, true))
            }

            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val barcode = preferenceScreen.sharedPreferences.getString("barcode", "")
        barcode?.let {
            findPreference<Preference>("barcode_fragment")?.summary = barcode
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        const val TAG = "SETTINGS_FRAGMENT"
    }
}
