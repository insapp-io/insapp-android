package fr.insapp.insapp.activities

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import android.view.MenuItem
import fr.insapp.insapp.R
import fr.insapp.insapp.fragments.BarcodeSettingsFragment
import fr.insapp.insapp.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by thomas on 15/12/2016.
 *
 * Based on https://stackoverflow.com/a/36051385
 */

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

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

        // handling nested preferences screen
        // bug, avec le code suivant, toutes les fonctions dans le fragment sont exécutées 2 fois.
        // Le fait de virer ce code n'a pas d'influence sur le comportement (qui n'est pas encore le bon)
        // TODO : corriger les préférences imbriquées
        /*if (savedInstanceState == null) {
            Log.d(fr.insapp.insapp.notifications.FirebaseMessaging.TAG, "savedInstanceState == null")
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = SettingsFragment.newInstance("General settings")

            fragmentTransaction.add(R.id.settings_fragment, fragment)
            fragmentTransaction.commit()
        }*/
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

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat, preferenceScreen: PreferenceScreen): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = BarcodeSettingsFragment.newInstance("Barcode settings")

        val args = Bundle()
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.key)
        fragment.arguments = args

        transaction.replace(R.id.settings_fragment, fragment, preferenceScreen.key)
        transaction.addToBackStack(null)
        transaction.commit()

        return true
    }
}