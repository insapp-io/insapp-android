package fr.insapp.insapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import fr.insapp.insapp.activities.IntroActivity

import fr.insapp.insapp.activities.MainActivity
import fr.insapp.insapp.activities.ProfileActivity
import fr.insapp.insapp.utility.Utils

/**
 * Created by thomas on 11/07/2017.
 */

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Utils.user == null) {
            val intent = Intent(this@LauncherActivity, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            // manage shortcuts
            when (intent.getIntExtra("SHORTCUT", 0)) {
                1 -> { // event shortcut
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java).putExtra("FRAGMENT_ID", 1))

                    FirebaseAnalytics.getInstance(this).logEvent("use_event_shortcut", Bundle())
                }
                2 -> { // notification shortcut
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java).putExtra("FRAGMENT_ID", 3))

                    FirebaseAnalytics.getInstance(this).logEvent("use_notification_shortcut", Bundle())
                }
                3 -> { // profile shortcut
                    startActivity(Intent(this, ProfileActivity::class.java))

                    FirebaseAnalytics.getInstance(this).logEvent("use_profile_shortcut", Bundle())
                }
                else -> startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            }
        }

        finish()
    }
}
