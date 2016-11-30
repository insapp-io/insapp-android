package fr.insapp.insapp.fragments_tuto;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insapp.insapp.R;


public class TutoPost extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutoPost newInstance() {
        TutoPost fragment = new TutoPost();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TutoPost() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tuto_post, container, false);
        return rootView;
    }
}

