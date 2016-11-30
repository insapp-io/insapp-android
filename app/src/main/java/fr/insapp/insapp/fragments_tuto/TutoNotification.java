package fr.insapp.insapp.fragments_tuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import fr.insapp.insapp.R;
import fr.insapp.insapp.Signin;


public class TutoNotification extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutoNotification newInstance() {
        TutoNotification fragment = new TutoNotification();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TutoNotification() {
    }

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
}
