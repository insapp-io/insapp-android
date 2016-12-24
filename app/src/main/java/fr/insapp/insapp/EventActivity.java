package fr.insapp.insapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thomas on 05/12/2016.
 */

public class EventActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    private ImageView header_image_event;
    private ImageView clubImageView;
    private TextView clubTextView;
    private ImageView participantsImageView;
    private TextView participantsTextView;
    private ImageView dateImageView;
    private TextView dateTextView;
    private TextView descriptionTextView;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        this.event = intent.getParcelableExtra("event");

        this.relativeLayout = (RelativeLayout) findViewById(R.id.event_info);
        this.header_image_event = (ImageView) findViewById(R.id.header_image_event);
        this.clubImageView = (ImageView) findViewById(R.id.event_club_icon);
        this.clubTextView = (TextView) findViewById(R.id.event_club_text);
        this.participantsImageView = (ImageView) findViewById(R.id.event_participants_icon);
        this.participantsTextView = (TextView) findViewById(R.id.event_participants_text);
        this.dateImageView = (ImageView) findViewById(R.id.event_date_icon);
        this.dateTextView = (TextView) findViewById(R.id.event_date_text);
        this.descriptionTextView = (TextView) findViewById(R.id.event_desc);

        final LinearLayout participantsLayout = (LinearLayout) findViewById(R.id.event_participants_layout);
        participantsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), UsersActivity.class));
            }
        });

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_event);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // collapsing toolbar

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_event);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_event);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(event.getName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        // dynamic color

        int bgColor = Color.parseColor("#" + event.getBgColor());
        int fgColor = Color.parseColor("#" + event.getFgColor());

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setStatusBarScrimColor(bgColor);

        Glide.with(this).load(HttpGet.IMAGEURL + event.getImage()).into(header_image_event);

        relativeLayout.setBackgroundColor(bgColor);

        // club

        clubImageView.setColorFilter(fgColor);

        clubTextView.setText(event.getAssociation());
        clubTextView.setTextColor(fgColor);

        // participants

        participantsImageView.setColorFilter(fgColor);

        int nb_participants = event.getParticipants().size();
        if (nb_participants <= 1)
            participantsTextView.setText("Pas encore de participants");
        else
            participantsTextView.setText(Integer.toString(nb_participants) + " participants");
        participantsTextView.setTextColor(fgColor);

        // date

        dateImageView.setColorFilter(fgColor);

        SimpleDateFormat format = new SimpleDateFormat("EEEE dd/MM", Locale.FRANCE);
        SimpleDateFormat format_hours_minutes = new SimpleDateFormat("HH:mm", Locale.FRANCE);

        if (event.getDateStart().getDay() == event.getDateEnd().getDay() && event.getDateStart().getMonth() == event.getDateEnd().getMonth()) {
            String day = format.format(event.getDateStart());
            dateTextView.setText(day.replaceFirst(".", (day.charAt(0) + "").toUpperCase()) + " de " + format_hours_minutes.format(event.getDateStart()) + " à " + format_hours_minutes.format(event.getDateEnd()));
        } else {
            String start = format.format(event.getDateStart()) + " à " + format_hours_minutes.format(event.getDateStart());
            String end = format.format(event.getDateEnd()) + " à " + format_hours_minutes.format(event.getDateEnd());
            dateTextView.setText("Du " + start.replaceFirst(".", (start.charAt(0) + "").toUpperCase()) + " au " + end.replaceFirst(".", (end.charAt(0) + "").toUpperCase()));
        }

        dateTextView.setText("" + event.getDateStart() + " au " + event.getDateEnd());
        dateTextView.setTextColor(fgColor);

        // description

        this.descriptionTextView.setText(event.getDescription());

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.stripUnderlines(descriptionTextView);

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }
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
