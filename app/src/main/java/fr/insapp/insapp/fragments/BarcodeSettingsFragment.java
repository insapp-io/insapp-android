package fr.insapp.insapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.BarcodeDetectorActivity;
import fr.insapp.insapp.activities.SettingsActivity;

/**
 * Created by thomas on 04/08/2017.
 */

public class BarcodeSettingsFragment extends PreferenceFragmentCompat {

    public static final String ID = "BARCODE_SETTINGS_FRAGMENT";

    private static final int CAMERA_PERMISSION_REQUEST = 111;

    public static BarcodeSettingsFragment newInstance(String id) {
        BarcodeSettingsFragment fragment = new BarcodeSettingsFragment();

        Bundle args = new Bundle();
        args.putString(BarcodeSettingsFragment.ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // barcode input from camera

        Preference barcodeCamera = findPreference("barcode_camera");

        barcodeCamera.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, BarcodeSettingsFragment.CAMERA_PERMISSION_REQUEST);
                }
                else {
                    startActivity(new Intent(getContext(), BarcodeDetectorActivity.class));
                }

                return true;
            }
        });

        // barcode input from dialog

        Preference barcodeDialog = findPreference("barcode_dialog");

        barcodeDialog.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final SharedPreferences.Editor defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();

                defaultSharedPreferences.putString("barcode", (String) newValue);
                defaultSharedPreferences.apply();

                final Intent next = new Intent(getContext(), SettingsActivity.class);
                next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(next);

                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted

                    startActivity(new Intent(getContext(), BarcodeDetectorActivity.class));
                }
                break;

            default:
                break;
        }
    }
}