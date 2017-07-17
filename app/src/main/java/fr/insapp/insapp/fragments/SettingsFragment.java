package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import fr.insapp.insapp.R;

/**
 * Created by thomas on 17/07/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
