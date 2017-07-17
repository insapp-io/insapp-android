package fr.insapp.insapp.fragments.intro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import fr.insapp.insapp.R;

/**
 * Created by thomas on 03/12/2016.
 */

public class IntroNotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_notifications, container, false);

        final SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        CheckBox checkbox = (CheckBox) rootView.findViewById(R.id.checkbox_enable_notifications);
        checkbox.setChecked(true);

        preferences.putBoolean("notifications", true);
        preferences.apply();

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.putBoolean("notifications", b);
                preferences.apply();
            }
        });

        return rootView;
    }
}
