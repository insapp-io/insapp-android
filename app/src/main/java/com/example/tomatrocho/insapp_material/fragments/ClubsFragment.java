package com.example.tomatrocho.insapp_material.fragments;

/**
 * Created by thoma on 27/10/2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.thoma.material.ClubThumb;
import com.example.thoma.material.ClubThumbAdapter;
import com.example.thoma.material.Post;
import com.example.thoma.material.R;

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
        List<ClubThumb> thumbs = new ArrayList<>();
        thumbs.add(new ClubThumb(R.drawable.sample_0, "Le Mégaphone"));
        thumbs.add(new ClubThumb(R.drawable.sample_1, "EAI"));
        thumbs.add(new ClubThumb(R.drawable.sample_2, "Gala"));
        thumbs.add(new ClubThumb(R.drawable.sample_3, "Ouest INSA"));
        thumbs.add(new ClubThumb(R.drawable.sample_4, "Bebop"));
        thumbs.add(new ClubThumb(R.drawable.sample_5, "Rock'n Solex"));
        thumbs.add(new ClubThumb(R.drawable.sample_6, "Ins'India"));
        thumbs.add(new ClubThumb(R.drawable.sample_7, "Club robot"));
        thumbs.add(new ClubThumb(R.drawable.sample_0, "Le Mégaphone"));
        thumbs.add(new ClubThumb(R.drawable.sample_1, "EAI"));
        thumbs.add(new ClubThumb(R.drawable.sample_2, "Gala"));
        thumbs.add(new ClubThumb(R.drawable.sample_3, "Ouest INSA"));
        thumbs.add(new ClubThumb(R.drawable.sample_4, "Bebop"));
        thumbs.add(new ClubThumb(R.drawable.sample_5, "Rock'n Solex"));
        thumbs.add(new ClubThumb(R.drawable.sample_6, "Ins'India"));
        thumbs.add(new ClubThumb(R.drawable.sample_7, "Club robot"));
        thumbs.add(new ClubThumb(R.drawable.sample_0, "Le Mégaphone"));
        thumbs.add(new ClubThumb(R.drawable.sample_1, "EAI"));
        thumbs.add(new ClubThumb(R.drawable.sample_2, "Gala"));
        thumbs.add(new ClubThumb(R.drawable.sample_3, "Ouest INSA"));
        thumbs.add(new ClubThumb(R.drawable.sample_4, "Bebop"));
        thumbs.add(new ClubThumb(R.drawable.sample_5, "Rock'n Solex"));
        thumbs.add(new ClubThumb(R.drawable.sample_6, "Ins'India"));
        thumbs.add(new ClubThumb(R.drawable.sample_7, "Club robot"));
        return thumbs;
    }
}