package fr.insapp.insapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent

import fr.insapp.insapp.activities.IntroActivity
import fr.insapp.insapp.activities.MainActivity
import fr.insapp.insapp.activities.ProfileActivity

/**
 * Created by thomas on 11/07/2017.
 */

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getSharedPreferences("Credentials", Context.MODE_PRIVATE).contains("login")) {
            // manage shortcuts
            val intentValue = intent.getIntExtra("SHORTCUT", 0)
            when (intentValue) {
                1 -> { // event shortcut
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java).putExtra("FRAGMENT_ID", 1))
                    Answers.getInstance().logCustom(CustomEvent("Use event shortcut"))
                }
                2 -> { // notification shortcut
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java).putExtra("FRAGMENT_ID", 3))
                    Answers.getInstance().logCustom(CustomEvent("Use notification shortcut"))
                }
                3 -> { // profile shortcut
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                    startActivity(Intent(this, ProfileActivity::class.java))
                    Answers.getInstance().logCustom(CustomEvent("Use profile shortcut"))
                }
                else -> startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            }
        } else {
            startActivity(Intent(this@LauncherActivity, IntroActivity::class.java))
        }

        finish()
    }
}
