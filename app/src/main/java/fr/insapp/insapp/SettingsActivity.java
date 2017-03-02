package fr.insapp.insapp;

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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.models.User;

/**
 * Created by thomas on 15/12/2016.
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private User user;

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
                ((EditTextPreference)preference).setText((String)newVal);
                return true;
            }

        });

        // request

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    user = new User(new JSONObject(output));

                    SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    preferences.putString("name", user.getName());
                    preferences.putString("sex", user.getGender());
                    preferences.putString("class", user.getPromotion());
                    preferences.putString("email", user.getEmail());
                    preferences.putString("description", user.getDescription());
                    preferences.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
    }

    private void initSummary(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;

            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                initSummary(preferenceGroup.getPreference(i));
            }
        } else {
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

            if (editTextPreference.getText() != null && !editTextPreference.getText().isEmpty())
                preference.setSummary(editTextPreference.getText());
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
/*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        EditTextPreference chimeMaster = (EditTextPreference)findPreference("barcode");
        chimeMaster.setText(sharedPreferences.getString("barcode", ""));

        System.out.println("WTF" + sharedPreferences.getString("barcode", ""));*/
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

    public void updateProfile(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        JSONObject json = new JSONObject();

        try {
            json.put("name", preferences.getString("name", ""));
            json.put("username", user.getUsername());
            json.put("description", preferences.getString("description", ""));
            json.put("email", preferences.getString("email", ""));
            json.put("emailpublic", user.isEmailPublic());
            json.put("promotion", preferences.getString("class", ""));
            json.put("gender", preferences.getString("sex", ""));

            JSONArray events = new JSONArray();
            for (String s : user.getEvents())
                events.put(s);

            json.put("events", events);

            JSONArray likes = new JSONArray();
            for (String s : user.getPostsLiked())
                likes.put(s);

            json.put("postsliked", likes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpPut put = new HttpPut(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output == null)
                    Toast.makeText(SettingsActivity.this, "Erreur lors de la modification du profil", Toast.LENGTH_LONG).show();
                else {
                    try {
                        MainActivity.user = new User(new JSONObject(output));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        put.execute(HttpGet.ROOTUSER + "/" + user.getId() + "?token=" + HttpGet.credentials.getSessionToken(), json.toString());
    }
}