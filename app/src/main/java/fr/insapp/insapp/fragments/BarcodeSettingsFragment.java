package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import fr.insapp.insapp.R;

/**
 * Created by thomas on 04/08/2017.
 */

public class BarcodeSettingsFragment extends PreferenceFragmentCompat {

    public static final String ID = "BARCODE_SETTINGS_FRAGMENT";

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
    }
}
