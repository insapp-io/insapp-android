package fr.insapp.insapp.fragments_tuto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.insapp.insapp.LegalConditionActivity;
import fr.insapp.insapp.R;


public class TutoProfil extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutoProfil newInstance() {
        TutoProfil fragment = new TutoProfil();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TutoProfil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tuto_profil, container, false);
        ImageView go = (ImageView) rootView.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), LegalConditionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
        return rootView;
    }
}
