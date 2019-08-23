package fr.insapp.insapp.activities

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import fr.insapp.insapp.R
import fr.insapp.insapp.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by thomas on 15/12/2016.
 *
 * https://developer.android.com/guide/topics/ui/settings
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // toolbar
        setSupportActionBar(toolbar_settings)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val upArrow = ResourcesCompat.getDrawable(resources, R.drawable.abc_ic_ab_back_material, null)
        upArrow?.setColorFilter(-0x1, PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_fragment, SettingsFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }
}