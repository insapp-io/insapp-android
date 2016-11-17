package fr.insapp.insapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import static java.security.AccessController.getContext;

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

        this.adapter = new CommentAdapter(getContext(), generateComments());
    }
}
