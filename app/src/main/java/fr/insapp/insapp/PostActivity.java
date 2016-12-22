package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.CommentRecyclerViewAdapter;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Post post = null;

    private CircleImageView avatar_club;
    private TextView title;
    private TextView description;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        this.avatar_club = (CircleImageView) findViewById(R.id.club_avatar_post);
        this.title = (TextView) findViewById(R.id.post_title);
        this.description = (TextView) findViewById(R.id.post_text);
        this.date = (TextView) findViewById(R.id.post_date);

        Intent intent = getIntent();

        post = intent.getParcelableExtra("post");

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Fill post elements

        final Club club = HttpGet.clubs.get(post.getAssociation());

        Glide.with(getApplicationContext()).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(this.avatar_club);

        this.title.setText(post.getTitle());
        this.description.setText(post.getDescription());
        this.date.setText(new String("il y a " + Operation.displayedDate(post.getDate())));

        this.avatar_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ClubActivity.class).putExtra("club", club));
            }
        });

        // recycler view

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(PostActivity.this, post.getComments());
        recyclerView.setAdapter(adapter);

        // swipe

        initSwipe(this);
    }

    private void initSwipe(final Context context) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(context, "" + direction, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    float h = (float) itemView.getBottom() - (float) itemView.getTop();
                    float w = h / 2;

                    if (dX < 0) {
                        Paint paint = new Paint();
                        paint.setColor(Color.parseColor("#ec5d57"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(),(float) itemView.getBottom());
                        canvas.drawRect(background, paint);
                    }
                }

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
