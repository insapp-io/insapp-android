package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.insapp.insapp.R;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thoma on 25/02/2017.
 */

public class AboutFragment extends Fragment {

    private View view;

    private Event event;

    private TextView descriptionTextView;

    private int bgColor;
    private int fgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.event = bundle.getParcelable("event");

            this.bgColor = bundle.getInt("bg_color");
            this.fgColor = bundle.getInt("fg_color");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_about, container, false);

        // description

        this.descriptionTextView = (TextView) view.findViewById(R.id.event_description);
        descriptionTextView.setText(event.getDescription());

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(getContext(), descriptionTextView);

        return view;
    }
}
