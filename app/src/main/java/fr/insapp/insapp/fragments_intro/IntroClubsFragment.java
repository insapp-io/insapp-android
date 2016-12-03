package fr.insapp.insapp.fragments_intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insapp.insapp.R;

/**
 * Created by thoma on 03/12/2016.
 */

public class IntroClubsFragment extends Fragment {

    public IntroClubsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_intro_association, container, false);
        return rootView;
    }
}
