package fr.insapp.insapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import fr.insapp.insapp.App
import fr.insapp.insapp.BuildConfig
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.ViewPagerAdapter
import fr.insapp.insapp.components.CustomTabsConnection
import fr.insapp.insapp.fragments.ClubsFragment
import fr.insapp.insapp.fragments.EventsFragment
import fr.insapp.insapp.fragments.NotificationsFragment
import fr.insapp.insapp.fragments.PostsFragment
import fr.insapp.insapp.notifications.FirebaseMessaging
import fr.insapp.insapp.notifications.NotificationUtils
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {

        const val dev = BuildConfig.BUILD_VARIANT == "dev"

        @JvmField var customTabsConnection: CustomTabsConnection? = null
    }

    private val userSerial: String
        get() {
            val userManager = getSystemService(Context.USER_SERVICE) ?: return ""

            try {
                val myUserHandleMethod = Process::class.java.getMethod("myUserHandle", null)
                val myUserHandle = myUserHandleMethod.invoke(Process::class.java, null)
                val getSerialNumberForUser = userManager.javaClass.getMethod("getSerialNumberForUser", myUserHandle.javaClass)
                val userSerial = getSerialNumberForUser.invoke(userManager, myUserHandle) as Long

                return userSerial.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar
        setSupportActionBar(toolbar_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = Utils.user?.username

        // view pager
        setupViewPager(viewpager)

        // tab layout
        tabs.setupWithViewPager(viewpager)

        // custom tabs optimization
        MainActivity.customTabsConnection = CustomTabsConnection()
        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", customTabsConnection)

        // display correct fragment for shortcuts
        val fragmentId = intent.getIntExtra("FRAGMENT_ID", 0)
        viewpager.currentItem = fragmentId

        // Huawei protected apps
        ifHuaweiAlert()

        // notification channel (android O and more)
        //Log.d(FirebaseMessaging.TAG, "Création des groupes de notifications.")
        /*createNotificationChannel("news", getString(R.string.notification_news_name), getString(R.string.notification_news_description))
        createNotificationChannel("events", getString(R.string.notification_events_name), getString(R.string.notification_events_description))
        createNotificationChannel("others", getString(R.string.notification_others_name), getString(R.string.notification_others_description))*/

        // topic notifications
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
        for (channel in NotificationUtils.channels) {
            if (defaultSharedPreferences.getBoolean("notifications_$channel", true)) {
                FirebaseMessaging.subscribeToTopics(channel)
            } else {
                FirebaseMessaging.unsubscribeFromTopics(channel)
            }
        }

        // Firebase token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(object: OnCompleteListener<InstanceIdResult> {
                override fun onComplete(task:Task<InstanceIdResult>) {
                    if (!task.isSuccessful) {
                        Log.w(FirebaseMessaging.TAG, "getInstanceId failed", task.exception)
                        return
                    }

                    Log.d(FirebaseMessaging.TAG, "Current Firebase token: " + task.result.token)
                }
            })
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val postsFragment = PostsFragment()
        val bundle1 = Bundle()
        bundle1.putInt("layout", R.layout.post_with_avatars)
        postsFragment.arguments = bundle1
        adapter.addFragment(postsFragment, resources.getString(R.string.posts))

        val eventsFragment = EventsFragment()
        val bundle2 = Bundle()
        bundle2.putInt("layout", R.layout.row_event_with_avatars)
        eventsFragment.arguments = bundle2
        adapter.addFragment(eventsFragment, resources.getString(R.string.events))

        adapter.addFragment(ClubsFragment(), resources.getString(R.string.clubs))
        adapter.addFragment(NotificationsFragment(), resources.getString(R.string.notifications))

        viewPager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, SearchActivity::class.java)))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> startActivity(Intent(this, ProfileActivity::class.java))

            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))

            R.id.action_how_to_post -> startActivity(Intent(this, HowToPostActivity::class.java))

            R.id.action_legal_conditions -> startActivity(Intent(this, LegalConditionsActivity::class.java))

            R.id.action_credits -> startActivity(Intent(this, CreditsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (MainActivity.customTabsConnection != null) {
            this.unbindService(MainActivity.customTabsConnection)
            MainActivity.customTabsConnection = null
        }
    }

    private fun ifHuaweiAlert() {
        if (Build.MANUFACTURER.equals("huawei", ignoreCase = true)) {
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())

            if (!defaultSharedPreferences.getBoolean("protected_apps", false)) {
                val editor = defaultSharedPreferences.edit()

                val intent = Intent().setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")

                if (isCallable(intent)) {
                    val dontShowAgain = AppCompatCheckBox(this)
                    dontShowAgain.text = getString(R.string.protected_apps_skip)
                    dontShowAgain.setOnCheckedChangeListener { _, isChecked ->
                        editor.putBoolean("protected_apps", isChecked)
                        editor.apply()
                    }

                    AlertDialog.Builder(this, R.style.InsappMaterialTheme_AlertDialogTheme)
                            .setTitle(getString(R.string.protected_apps_dialog_title))
                            .setMessage(String.format(getString(R.string.protected_apps_dialog_message), getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton(getString(R.string.protected_apps_button)) { _, _ -> huaweiProtectedApps() }
                            .setNegativeButton(R.string.close_button, null)
                            .show()
                } else {
                    editor.putBoolean("protected_apps", true)
                    editor.apply()
                }
            }
        }
    }

    private fun isCallable(intent: Intent): Boolean {
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    private fun huaweiProtectedApps() {
        try {
            Runtime.getRuntime().exec("am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity --user $userSerial")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }


    private fun createNotificationChannel(id: String, name: String, description: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}
