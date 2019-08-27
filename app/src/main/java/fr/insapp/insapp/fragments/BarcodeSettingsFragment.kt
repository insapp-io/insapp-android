package fr.insapp.insapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.BarcodeDetectorActivity

/**
 * Created by thomas on 04/08/2017.
 */

class BarcodeSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.barcode_preferences, rootKey)

        // barcode input from camera

        findPreference<PreferenceScreen>("barcode_camera")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
            } else {
                startActivity(Intent(context, BarcodeDetectorActivity::class.java))
            }

            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted

                startActivity(Intent(context, BarcodeDetectorActivity::class.java))
            }

            else -> {
            }
        }
    }

    companion object {

        const val ID = "BARCODE_SETTINGS_FRAGMENT"

        private const val CAMERA_PERMISSION_REQUEST = 111
    }
}