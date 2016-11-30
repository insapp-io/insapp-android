package fr.insapp.insapp.fragments_tuto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insapp.insapp.R;


public class TutoEvent extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutoEvent newInstance() {
        TutoEvent fragment = new TutoEvent();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TutoEvent() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tuto_event, container, false);
        return rootView;
    }
}

