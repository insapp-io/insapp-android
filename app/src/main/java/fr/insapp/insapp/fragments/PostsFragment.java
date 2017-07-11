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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.PostActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter;
import fr.insapp.insapp.http.retrofit.Client;
import fr.insapp.insapp.http.retrofit.ServiceGenerator;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.DividerItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by thoma on 27/10/2016.
 */

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;
    private String filter_club_id = null;
    private int swipeColor;

    private View view;
    private PostRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int WRITE_COMMENT_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.layout = bundle.getInt("layout", R.layout.post_with_avatars);
            this.filter_club_id = bundle.getString("filter_club_id");
            this.swipeColor = bundle.getInt("swipe_color");
        }

        // adapter

        this.adapter = new PostRecyclerViewAdapter(getContext(), layout);
        adapter.setOnItemClickListener(new PostRecyclerViewAdapter.OnPostItemClickListener() {
            @Override
            public void onPostItemClick(Post post) {
                startActivityForResult(new Intent(getActivity(), PostActivity.class).putExtra("post", post), WRITE_COMMENT_REQUEST);
            }
        });

        generatePosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_posts, container, false);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        if (layout == R.layout.post_with_avatars)
            recyclerView.addItemDecoration(new DividerItemDecoration(getResources(), R.drawable.half_divider));
        else if (layout == R.layout.post)
            recyclerView.addItemDecoration(new DividerItemDecoration(getResources(), R.drawable.full_divider));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // swipe refresh layout

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_posts);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (filter_club_id != null)
            swipeRefreshLayout.setColorSchemeColors(swipeColor);
        else
            swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case WRITE_COMMENT_REQUEST:
                    Post post = data.getParcelableExtra("post");
                    int id = adapter.getPosts().indexOf(post);
                    adapter.updatePost(id, post);
                    break;

                case MainActivity.REFRESH_TOKEN_MESSAGE:
                    generatePosts();
                    break;
            }
        }
    }

    private void generatePosts() {
        adapter.getPosts().clear();

        Call<List<Post>> call = ServiceGenerator.createService(Client.class).getLatestPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    for (Post post : response.body()) {
                        if (filter_club_id == null || filter_club_id.equals(post.getAssociation()))
                            adapter.addItem(post);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "PostsFragment", Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getActivity(), "PostsFragment", Toast.LENGTH_LONG).show();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        generatePosts();
    }
}