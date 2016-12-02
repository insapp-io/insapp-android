package fr.insapp.insapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by thoma on 02/12/2016.
 */

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int primaryColor = getResources().getColor(R.color.colorPrimary);

        addSlide(AppIntroFragment.newInstance("Associations", "Retrouve toutes les associations de l'INSA de Rennes", R.drawable.tutorial_association_android1, primaryColor));
        addSlide(AppIntroFragment.newInstance("Évènements", "Reste au courant des derniers évènements des associations", R.drawable.tutorial_event_android3, primaryColor));
        addSlide(AppIntroFragment.newInstance("News", "Suis l'actualité de tes associations préférées grâce à la page des news", R.drawable.tutorial_news_android2, primaryColor));
        addSlide(AppIntroFragment.newInstance("Notifications", "Reçois les notifications pour ne jamais rien louper", R.drawable.android_notification, primaryColor));
        addSlide(AppIntroFragment.newInstance("Profil", "Porte haut les couleurs de ton départ' en éditant ton profil", R.drawable.avatars, primaryColor));

        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tuto_notification, container, false);
        ImageView activate = (ImageView) rootView.findViewById(R.id.activer);
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sharedPref = getActivity().getSharedPreferences(
                        Signin.class.getSimpleName(), getContext().MODE_PRIVATE).edit();

                sharedPref.putBoolean("notifications", true);
                sharedPref.commit();

                Toast.makeText(getContext(), "Notifications activées", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
         */

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        setVibrate(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        Intent i = new Intent(this, LegalConditionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent i = new Intent(this, LegalConditionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

        finish();
    }
}
