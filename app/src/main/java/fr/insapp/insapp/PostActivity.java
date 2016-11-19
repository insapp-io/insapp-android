package fr.insapp.insapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 12/11/2016.
 */

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(generateComments());
        recyclerView.setAdapter(adapter);

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

    private List<Comment> generateComments() {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            comments.add(new Comment(R.drawable.sample_0, "tbouvier", "Ceci est un commentaire, un long commentaire pour voir si ça marche bien, et effectivement, ça marche !"));

        return comments;
    }
}
