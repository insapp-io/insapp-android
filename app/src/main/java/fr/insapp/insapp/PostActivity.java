package fr.insapp.insapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    private ListView listView;
    private CommentAdapter adapter;

    public PostActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        /*
        this.adapter = new CommentAdapter(this, generateComments());

        this.listView = (ListView) findViewById(R.id.listview_post);
        listView.setAdapter(adapter);
        */

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_post);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(generateComments());
        recyclerView.setAdapter(adapter);
    }

    private List<Comment> generateComments() {
        List<Comment> comments = new ArrayList<>();

        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));
        comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire"));

        return comments;
    }
}
