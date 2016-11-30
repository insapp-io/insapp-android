package fr.insapp.insapp.fragments_tuto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insapp.insapp.R;


public class TutoAssociation extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutoAssociation newInstance() {
        TutoAssociation fragment = new TutoAssociation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TutoAssociation() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tuto_association, container, false);
        return rootView;
    }
}
