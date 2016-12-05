package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.PostRecyclerViewAdapter;
import fr.insapp.insapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 27/10/2016.
 */

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;

    private PostRecyclerViewAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new PostRecyclerViewAdapter(getContext(), generatePosts());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_news, container, false);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_posts);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // swipe refresh layout

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_posts);
        swipeRefreshLayout.setOnRefreshListener(this);

        // onClick
/*
        adapter.setOnItemClickListener(new PostRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getContext().startActivity(new Intent(getContext(), PostActivity.class));
                Toast.makeText(getContext(), "index: " + position, Toast.LENGTH_SHORT).show();
            }
        });
*/
        return view;
    }

    private List<Post> generatePosts() {
        final List<Post> posts = new ArrayList<>();

        HttpGet request = new HttpGet(new AsyncResponse() {

            public void processFinish(String output) {
                if (!output.isEmpty()) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            final JSONObject jsonobject = jsonarray.getJSONObject(i);
                            posts.add(new Post(jsonobject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTPOST + "?token=" + HttpGet.credentials.getSessionToken());

        return posts;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}