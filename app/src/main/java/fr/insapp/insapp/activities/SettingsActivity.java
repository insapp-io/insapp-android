package fr.insapp.insapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import fr.insapp.insapp.R;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.SessionCredentials;

/**
 * Created by thomas on 15/12/2016.
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // toolbar

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout toolbar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(toolbar, 0); // insert at top

        ((Toolbar) toolbar.getChildAt(0)).setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateProfile();
                onBackPressed();
            }
        });

        // preferences

        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);
        initSummary(getPreferenceScreen());

        final Preference chimeMaster = findPreference("barcode");
        chimeMaster.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                ((EditTextPreference) preference).setText((String) newVal);
                return true;
            }
        });

        // filling fields

        final User user = new Gson().fromJson(getSharedPreferences("Credentials", MODE_PRIVATE).getString("session", ""), SessionCredentials.class).getUser();

        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        preferences.putString("name", user.getName());
        preferences.putString("sex", user.getGender());
        preferences.putString("class", user.getPromotion());
        preferences.putString("email", user.getEmail());
        preferences.putString("description", user.getDescription());
        preferences.apply();
    }

    private void initSummary(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;

            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                initSummary(preferenceGroup.getPreference(i));
            }
        }
        else {
            updatePreferenceSummary(preference);
        }

        // checkboxes

        SharedPreferences sharedPreferences = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE);

        CheckBoxPreference checkboxNotifications = (CheckBoxPreference) getPreferenceManager().findPreference("notifications");
        checkboxNotifications.setChecked(sharedPreferences.getBoolean("notifications", false));

        checkboxNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor preferences = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                preferences.putBoolean("notifications", (boolean) newValue);
                preferences.apply();

                return true;
            }
        });

        CheckBoxPreference checkboxCalendar = (CheckBoxPreference) getPreferenceManager().findPreference("calendar");
        checkboxCalendar.setChecked(sharedPreferences.getBoolean("calendar", false));

        checkboxCalendar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor preferences = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                preferences.putBoolean("calendar", (boolean) newValue);
                preferences.apply();

                return true;
            }
        });
    }

    private void updatePreferenceSummary(Preference preference) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(listPreference.getEntry());
        }

        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;

            if (editTextPreference.getText() != null && !editTextPreference.getText().isEmpty()) {
                preference.setSummary(editTextPreference.getText());
            }
        }

        if (preference instanceof MultiSelectListPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            preference.setSummary(editTextPreference.getText());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updatePreferenceSummary(findPreference(s));
    }

    public void updateProfile() {
    }
}