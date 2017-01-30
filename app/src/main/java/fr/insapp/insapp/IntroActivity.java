package fr.insapp.insapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import fr.insapp.insapp.fragments_intro.IntroClubsFragment;
import fr.insapp.insapp.fragments_intro.IntroEventsFragment;
import fr.insapp.insapp.fragments_intro.IntroNewsFragment;
import fr.insapp.insapp.fragments_intro.IntroNotificationsFragment;
import fr.insapp.insapp.fragments_intro.IntroProfileFragment;

/**
 * Created by thoma on 02/12/2016.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new IntroNewsFragment());
        addSlide(new IntroEventsFragment());
        addSlide(new IntroClubsFragment());
        addSlide(new IntroNotificationsFragment());
        addSlide(new IntroProfileFragment());

        showSkipButton(false);
        setProgressButtonEnabled(true);
        setVibrate(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        Intent i = new Intent(this, LegalConditionsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent i = new Intent(this, LegalConditionsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

        finish();
    }
}
