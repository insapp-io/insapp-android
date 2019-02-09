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
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.os.Build
import android.util.Log
import fr.insapp.insapp.notifications.NotificationUtils


/**
 * Created by thomas on 17/07/2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
        // Update notification parameters from system parameter
        //updateNotificationChannelStatus()

        addPreferencesFromResource(R.xml.preferences)

        initSummary(preferenceScreen)

        val barcodeData = defaultSharedPreferences.getString("barcode", "")

        if (barcodeData != "") {
            findPreference("barcode_preferences").summary = barcodeData
        }

        val myPref = findPreference("notifications_system") as Preference
        myPref.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference): Boolean {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                //for Android 5-7
                intent.putExtra("app_package", activity?.packageName)
                intent.putExtra("app_uid", activity?.applicationInfo?.uid)
                // for Android 8 and above
                intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)
                startActivity(intent)
                return true
            }
        }
    }

    private fun updateNotificationChannelStatus(){
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            for(channel in NotificationUtils.channels){
                val channelEnable = isNotificationChannelEnabled(App.getAppContext(), channel)
                //Log.d(FirebaseMessaging.TAG, "Test channel - $channel - $channelEnable")
                if(channelEnable != defaultSharedPreferences.getBoolean("notifications_$channel", true)) {
                    //Log.d(FirebaseMessaging.TAG, "L'utilisateur a changé le paramètre dans le système, update dans l'application")
                    defaultSharedPreferences.edit().putBoolean("notifications_$channel", channelEnable).apply()
                    //Log.d(FirebaseMessaging.TAG, defaultSharedPreferences.getBoolean("notifications_$channel", true).toString())
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (findPreference(key) != null) {
            updatePreferenceSummary(findPreference(key))
        }

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

            "notifications_news", "notifications_events" -> {

                Log.d(FirebaseMessaging.TAG, key + " with channel " + key.removePrefix("notifications_") + " has changed and is now : " + sharedPreferences.getBoolean(key, true))
                //setNotificationChannel(key.removePrefix("notifications_"), sharedPreferences.getBoolean(key, true))
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, App.getAppContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, (App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getNotificationChannel(key.removePrefix("notifications_")).id)
                    }
                    startActivity(intent)
                }*/

                if (sharedPreferences.getBoolean(key, true)) {
                    FirebaseMessaging.subscribeToTopics(key.removePrefix("notifications_"))
                } else {
                    FirebaseMessaging.unsubscribeFromTopics(key.removePrefix("notifications_"))
                }
            }

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

    private fun isNotificationChannelEnabled(context: Context, channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelId)
                return channel.importance != NotificationManager.IMPORTANCE_NONE
            }
            return false
        } else {
            return preferenceScreen.sharedPreferences.getBoolean("notifications_$channelId",true)
        }
    }


    companion object {

        const val ID = "SETTINGS_FRAGMENT"

        fun newInstance(id: String): SettingsFragment {
            val fragment = SettingsFragment()

            val args = Bundle()
            args.putString(SettingsFragment.ID, id)
            fragment.arguments = args

            return fragment
        }
    }
}
