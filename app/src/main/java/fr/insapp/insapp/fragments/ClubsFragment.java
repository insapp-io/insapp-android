package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.ClubActivity;
import fr.insapp.insapp.adapters.ClubRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 27/10/2016.
 */

public class ClubsFragment extends Fragment {

    private ClubRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ClubRecyclerViewAdapter(getContext(), Glide.with(this), true);
        adapter.setOnItemClickListener(new ClubRecyclerViewAdapter.OnClubItemClickListener() {
            @Override
            public void onClubItemClick(Club club) {
                getContext().startActivity(new Intent(getContext(), ClubActivity.class).putExtra("club", club));
            }
        });

        generateClubs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_clubs);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void generateClubs() {
        Call<List<Club>> call = ServiceGenerator.create().getClubs();
        call.enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(@NonNull Call<List<Club>> call, @NonNull Response<List<Club>> response) {
                if (response.isSuccessful()) {
                    List<Club> clubs = response.body();

                    for (final Club club : clubs) {
                        if (!club.getProfilePicture().isEmpty() && !club.getCover().isEmpty()) {
                            adapter.addItem(club);
                        }
                    }
                }
                else {
                    Toast.makeText(getContext(), "ClubsFragment", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Club>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "ClubsFragment", Toast.LENGTH_LONG).show();
            }
        });
    }
}