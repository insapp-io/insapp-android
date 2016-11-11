package fr.insapp.insapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import fr.insapp.insapp.Post;
import fr.insapp.insapp.PostAdapter;
import fr.insapp.insapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 27/10/2016.
 */

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View view;

    private ListView listView;
    private PostAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new PostAdapter(getContext(), generatePosts());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_news, container, false);

        this.listView = (ListView) view.findViewById(R.id.listview);
        this.listView.setAdapter(this.adapter);

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private List<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.sample_0, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 54, 0));
        posts.add(new Post(R.drawable.sample_1, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 4, 54));
        posts.add(new Post(R.drawable.sample_2, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 87, 1));
        posts.add(new Post(R.drawable.sample_3, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 105, 6));
        posts.add(new Post(R.drawable.sample_4, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 1, 8));
        posts.add(new Post(R.drawable.sample_5, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 65, 12));
        posts.add(new Post(R.drawable.sample_6, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 13, 3));
        posts.add(new Post(R.drawable.sample_7, "Paul Taylor au Gala", "Nous c'est le mégaphone, n'hésite pas à nous rejoindre", R.drawable.large_sample_0, 18, 2));
        return posts;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}