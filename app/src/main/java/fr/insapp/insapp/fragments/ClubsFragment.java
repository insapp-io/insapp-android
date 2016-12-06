package fr.insapp.insapp.fragments;

/**
 * Created by thoma on 27/10/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.ClubActivity;
import fr.insapp.insapp.ClubThumbRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.ClubThumb;
import fr.insapp.insapp.R;

import java.util.ArrayList;
import java.util.List;

public class ClubsFragment extends Fragment {

    private View view;
    private ClubThumbRecyclerViewAdapter adapter;

    public ClubsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ClubThumbRecyclerViewAdapter(getContext(), generateClubThumbs());
        adapter.setOnItemClickListener(new ClubThumbRecyclerViewAdapter.OnClubThumbItemClickListener() {
            @Override
            public void onClubThumbItemClick(ClubThumb clubThumb) {
                getContext().startActivity(new Intent(getContext(), ClubActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_clubs, container, false);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_clubs);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<ClubThumb> generateClubThumbs() {
        final List<ClubThumb> thumbs = new ArrayList<>();
        HttpGet request = new HttpGet(new AsyncResponse() {

            public void processFinish(String output) {
                if (!output.isEmpty()) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            final JSONObject jsonobject = jsonarray.getJSONObject(i);
                            ClubThumb club = new ClubThumb(jsonobject);

                            if(!club.getProfilPicture().isEmpty() && !club.getCover().isEmpty())
                                thumbs.add(new ClubThumb(jsonobject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.execute(HttpGet.ROOTASSOCIATION + "?token=" + HttpGet.credentials.getSessionToken());

        return thumbs;
    }
}