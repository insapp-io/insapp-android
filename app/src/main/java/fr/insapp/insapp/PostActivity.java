package fr.insapp.insapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(generateComments());
        recyclerView.setAdapter(adapter);
    }

    private List<Comment> generateComments() {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire, un long commentaire pour voir si ça marche bien, et effectivement, ça marche !"));

        return comments;
    }
}
