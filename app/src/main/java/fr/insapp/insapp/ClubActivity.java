package fr.insapp.insapp;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 11/11/2016.
 */

public class ClubActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private PostAdapter adapter;

    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        this.adapter = new PostAdapter(this, generatePosts(), R.layout.row_post_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }

        this.listView = (ListView) findViewById(R.id.listview_club_profile);
        this.listView.setAdapter(this.adapter);

        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_club_profile);
        this.swipeRefreshLayout.setOnRefreshListener(this);
    }

    private List<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.sample_0, "Paul Taylor au Gala", "Yes, except the Dave Matthews Band doesn't rock. That's right, baby. I ain't your loverboy Flexo, the guy you love so much.", R.drawable.large_sample_0, 54, 0));
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
        Toast.makeText(this, "onRefresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}