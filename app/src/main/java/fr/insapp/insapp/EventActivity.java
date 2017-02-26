package fr.insapp.insapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.AboutFragment;
import fr.insapp.insapp.fragments.CommentsEventFragment;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thomas on 05/12/2016.
 */

public class EventActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;

    private ImageView headerImageView;
    private ImageView clubImageView;
    private TextView clubTextView;
    private ImageView participantsImageView;
    private TextView participantsTextView;
    private ImageView dateImageView;
    private TextView dateTextView;

    private Event event;

    private int bgColor;
    private int fgColor;

    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        this.event = intent.getParcelableExtra("event");

        this.relativeLayout = (RelativeLayout) findViewById(R.id.event_info);

        this.headerImageView = (ImageView) findViewById(R.id.header_image_event);
        this.clubImageView = (ImageView) findViewById(R.id.event_club_icon);
        this.clubTextView = (TextView) findViewById(R.id.event_club_text);
        this.participantsImageView = (ImageView) findViewById(R.id.event_participants_icon);
        this.participantsTextView = (TextView) findViewById(R.id.event_participants_text);
        this.dateImageView = (ImageView) findViewById(R.id.event_date_icon);
        this.dateTextView = (TextView) findViewById(R.id.event_date_text);

        final LinearLayout participantsLayout = (LinearLayout) findViewById(R.id.event_participants_layout);
        participantsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AttendeesActivity.class);

                intent.putExtra("attendees", event.getAttendees());
                intent.putExtra("maybe", event.getMaybe());

                startActivity(intent);
            }
        });

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_event);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // dynamic color

        this.bgColor = Color.parseColor("#" + event.getBgColor());
        this.fgColor = Color.parseColor("#" + event.getFgColor());

        // if we come from an android notification

        if (this.event == null) {
            notification = intent.getParcelableExtra("notification");

            if (HttpGet.credentials != null)
                onActivityResult(PostActivity.NOTIFICATION_MESSAGE, RESULT_OK, null);
            else
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), PostActivity.NOTIFICATION_MESSAGE);
        }
        else
            generateEvent();

        // view pager

        this.viewPager = (ViewPager) findViewById(R.id.viewpager_event);
        setupViewPager(viewPager, bgColor);

        if (fgColor != 0xffffffff)
            setupViewPager(viewPager, fgColor);
        else
            setupViewPager(viewPager, bgColor);

        // tab layout

        this.tabLayout = (TabLayout) findViewById(R.id.tabs_event);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(bgColor);

        if (fgColor == 0xffffffff)
            tabLayout.setTabTextColors(0xffdbdbdb, fgColor);
        else
            tabLayout.setTabTextColors(0xff5e5e5e, fgColor);
    }

    private void setupViewPager(ViewPager viewPager, int swipeColor) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment aboutFragment = new AboutFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable("event", event);
        bundle1.putInt("bg_color", bgColor);
        bundle1.putInt("fg_color", fgColor);
        aboutFragment.setArguments(bundle1);
        adapter.addFragment(aboutFragment, getResources().getString(R.string.about));

        Fragment commentsEventFragment = new CommentsEventFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("event", event);
        commentsEventFragment.setArguments(bundle2);
        adapter.addFragment(commentsEventFragment, getResources().getString(R.string.comments));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PostActivity.NOTIFICATION_MESSAGE) {
            if (resultCode == RESULT_OK){

                HttpGet request = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            event = new Event(new JSONObject(output));

                            generateEvent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                request.execute(HttpGet.ROOTEVENT + "/" + notification.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
            }
        }
    }

    public void generateEvent() {
        Glide.with(this).load(HttpGet.IMAGEURL + event.getImage()).asBitmap().into(new BitmapImageViewTarget(headerImageView) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                Utils.darkenBitmap(bitmap);
            }
        });

        relativeLayout.setBackgroundColor(bgColor);

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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Drawable upArrow = ContextCompat.getDrawable(EventActivity.this, R.drawable.abc_ic_ab_back_material);
                        upArrow.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    }
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Drawable upArrow = ContextCompat.getDrawable(EventActivity.this, R.drawable.abc_ic_ab_back_material);
                        upArrow.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    }
                }
            }
        });

        collapsingToolbar.setCollapsedTitleTextColor(fgColor);
        collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setStatusBarScrimColor(bgColor);

        // club

        clubImageView.setColorFilter(fgColor);

        final Club club = HttpGet.clubs.get(event.getAssociation());
        if (club == null) {
            HttpGet request = new HttpGet(new AsyncResponse() {
                public void processFinish(String output) {
                    if (!output.isEmpty()) {
                        try {
                            JSONObject jsonobject = new JSONObject(output);

                            final Club club = new Club(jsonobject);
                            HttpGet.clubs.put(club.getId(), club);

                            clubTextView.setText(club.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            request.execute(HttpGet.ROOTASSOCIATION + "/"+ event.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());
        }
        else
            clubTextView.setText(club.getName());

        clubTextView.setTextColor(fgColor);

        // participants

        participantsImageView.setColorFilter(fgColor);
        participantsTextView.setTextColor(fgColor);

        refreshAttendeesTextView();

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

        //dateTextView.setText("" + event.getDateStart() + " au " + event.getDateEnd());
        dateTextView.setTextColor(fgColor);

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent_black));
        }
    }

    @Override
    public void finish() {
        Intent sendIntent = new Intent();
        sendIntent.putExtra("event", event);

        setResult(RESULT_OK, sendIntent);

        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (isTaskRoot()) {
                    Intent i = new Intent(EventActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else
                    finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshAttendeesTextView() {
        final int nbParticipants = event.getAttendees().size();
        final int nbInterested = event.getMaybe().size();

        if (nbParticipants == 0) {
            if (nbInterested == 0)
                participantsTextView.setText(getResources().getString(R.string.no_attendees_no_interested));
            else if (nbInterested == 1)
                participantsTextView.setText(getResources().getString(R.string.no_attendees_one_interested));
            else
                participantsTextView.setText(String.format(getResources().getString(R.string.no_attendees_x_interested), nbInterested));
        }
        else if (nbParticipants == 1) {
            if (nbInterested == 0)
                participantsTextView.setText(getResources().getString(R.string.one_attendee_no_interested));
            else if (nbInterested == 1)
                participantsTextView.setText(getResources().getString(R.string.one_attendee_one_interested));
            else
                participantsTextView.setText(String.format(getResources().getString(R.string.one_attendee_x_interested), nbInterested));
        }
        else {
            if (nbInterested == 0)
                participantsTextView.setText(String.format(getResources().getString(R.string.x_attendees_no_interested), nbParticipants));
            else if (nbInterested == 1)
                participantsTextView.setText(String.format(getResources().getString(R.string.x_attendees_one_interested), nbParticipants));
            else
                participantsTextView.setText(String.format(getResources().getString(R.string.x_attendees_x_interested), nbParticipants, nbInterested));
        }
    }
}
