package fr.insapp.insapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import fr.insapp.insapp.activities.IntroActivity
import fr.insapp.insapp.activities.MainActivity

/**
 * Created by thomas on 11/07/2017.
 */

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getSharedPreferences("Credentials", Context.MODE_PRIVATE).contains("login")) {
            startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@LauncherActivity, IntroActivity::class.java))
        }

        finish()
    }
}
