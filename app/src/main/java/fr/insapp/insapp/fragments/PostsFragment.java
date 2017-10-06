package fr.insapp.insapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;
import fr.insapp.insapp.activities.PostActivity;
import fr.insapp.insapp.adapters.PostRecyclerViewAdapter;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.DividerItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by thomas on 27/10/2016.
 */

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int layout;
    private String filter_club_id = null;
    private int swipeColor;

    private PostRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private static final int POST_REQUEST = 3;

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
                startActivityForResult(new Intent(getActivity(), PostActivity.class).putExtra("post", post), POST_REQUEST);
            }
        });

        generatePosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        // recycler view

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        if (layout == R.layout.post_with_avatars) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getResources(), R.drawable.half_divider));
        }
        else if (layout == R.layout.post) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getResources(), R.drawable.full_divider));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // progress bar

        this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        // swipe refresh layout

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_posts);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (filter_club_id != null) {
            swipeRefreshLayout.setColorSchemeColors(swipeColor);
        }
        else {
            swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        }

        return view;
    }

    private void generatePosts() {
        Call<List<Post>> call = ServiceGenerator.create().getLatestPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    adapter.getPosts().clear();

                    for (final Post post : response.body()) {
                        if (filter_club_id == null || filter_club_id.equals(post.getAssociation())) {
                            adapter.addItem(post);
                        }
                    }
                }
                else {
                    Toast.makeText(App.getAppContext(), "PostsFragment", Toast.LENGTH_LONG).show();
                }

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Toast.makeText(App.getAppContext(), "PostsFragment", Toast.LENGTH_LONG).show();

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == POST_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    final Post post = intent.getParcelableExtra("post");

                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (adapter.getPosts().get(i).getId().equals(post.getId())) {
                            adapter.updatePost(i, post);
                        }
                    }

                    break;

                case RESULT_CANCELED:
                default:
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        generatePosts();
    }
}