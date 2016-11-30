package fr.insapp.insapp.fragments;

/**
 * Created by thoma on 27/10/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.ClubThumb;
import fr.insapp.insapp.ClubThumbAdapter;
import fr.insapp.insapp.R;
import fr.insapp.insapp.modeles.Post;

import java.util.ArrayList;
import java.util.List;

public class ClubsFragment extends Fragment {

    private View view;

    private GridView gridView;
    private ClubThumbAdapter adapter;

    public ClubsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ClubThumbAdapter(getContext(), generateClubThumbs());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_clubs, container, false);

        this.gridView = (GridView) view.findViewById(R.id.gridview);
        this.gridView.setAdapter(this.adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

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