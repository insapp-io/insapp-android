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

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.notifications.FirebaseMessaging;

/**
 * Created by thomas on 03/12/2016.
 */

public class IntroNotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_notifications, container, false);

        final SharedPreferences.Editor defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit();

        CheckBox checkbox = (CheckBox) rootView.findViewById(R.id.checkbox_enable_notifications);
        checkbox.setChecked(true);

        defaultSharedPreferences.putBoolean("notifications", true);
        defaultSharedPreferences.apply();

        FirebaseMessaging.Companion.subscribeToTopics();

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                defaultSharedPreferences.putBoolean("notifications", b);
                defaultSharedPreferences.apply();

                if (b)
                    FirebaseMessaging.Companion.subscribeToTopics();
                else
                    FirebaseMessaging.Companion.unsubscribeFromTopics();
            }
        });

        return rootView;
    }
}
