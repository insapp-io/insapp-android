package fr.insapp.insapp.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import fr.insapp.insapp.R;
import fr.insapp.insapp.fragments.BarcodeSettingsFragment;
import fr.insapp.insapp.fragments.SettingsFragment;

/**
 * Created by thomas on 15/12/2016.
 *
 * Based on https://stackoverflow.com/a/36051385
 */

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Drawable upArrow = ContextCompat.getDrawable(SettingsActivity.this, R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        // handling nested preferences screen

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SettingsFragment fragment = SettingsFragment.newInstance("General settings");

            fragmentTransaction.add(R.id.settings_fragment, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen preferenceScreen) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BarcodeSettingsFragment fragment = BarcodeSettingsFragment.newInstance("Barcode settings");

        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);

        transaction.replace(R.id.settings_fragment, fragment, preferenceScreen.getKey());
        transaction.addToBackStack(null);
        transaction.commit();

        return true;
    }
}