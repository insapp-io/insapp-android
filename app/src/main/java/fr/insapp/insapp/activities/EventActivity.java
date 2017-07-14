package fr.insapp.insapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.AboutFragment;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.User;
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

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    private AppBarLayout appBarLayout;

    private Event.PARTICIPATE status = Event.PARTICIPATE.UNDEFINED;

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

        // floating action menu

        /*
        this.status = event.getStatusForUser(HttpGet.sessionCredentials.getUserID());
        */

        // fab style

        this.floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_participate_event);
        floatingActionMenu.setIconAnimated(false);
        setFloatingActionMenuTheme(status);

        // hide fab is event is past

        final Date atm = Calendar.getInstance().getTime();
        if (event.getDateEnd().getTime() < atm.getTime())
            floatingActionMenu.setVisibility(View.GONE);

        // app bar layout

        this.appBarLayout = (AppBarLayout) findViewById(R.id.appbar_event);

        // if we come from an android notification

        if (this.event == null) {
            notification = intent.getParcelableExtra("notification");

            if (HttpGet.sessionCredentials != null)
                onActivityResult(PostActivity.NOTIFICATION_MESSAGE, RESULT_OK, null);
            /*
            else
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), PostActivity.NOTIFICATION_MESSAGE);
            */
        }
        else
            generateEvent();
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

        /*
        Fragment commentsEventFragment = new CommentsEventFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("event", event);
        commentsEventFragment.setArguments(bundle2);
        adapter.addFragment(commentsEventFragment, getResources().getString(R.string.comments));
        */

        viewPager.setAdapter(adapter);
    }

    private void setFloatingActionMenuTheme(Event.PARTICIPATE status) {
        switch (status) {
            case UNDEFINED:
                floatingActionMenu.setMenuButtonColorNormal(bgColor);
                floatingActionMenu.setMenuButtonColorPressed(bgColor);
                floatingActionMenu.getMenuIconView().setColorFilter(fgColor);
                break;

            case NO:
                floatingActionMenu.setMenuButtonColorNormal(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.setMenuButtonColorPressed(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close_black_24dp));
                floatingActionMenu.getMenuIconView().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_red));
                break;

            case MAYBE:
                floatingActionMenu.setMenuButtonColorNormal(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.setMenuButtonColorPressed(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_question_mark_black));
                floatingActionMenu.getMenuIconView().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_orange));
                break;

            case YES:
                floatingActionMenu.setMenuButtonColorNormal(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.setMenuButtonColorPressed(ContextCompat.getColor(getApplicationContext(), R.color.white));
                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check_black_24dp));
                floatingActionMenu.getMenuIconView().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_green));
                break;

            default:
                break;
        }

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
                /*
                request.execute(HttpGet.ROOTEVENT + "/" + notification.getContent() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                */
            }
        }
    }

    public void generateEvent() {

        // fab 1

        this.floatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab_item_1_event);
        floatingActionButton1.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton1.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable doubleTick = ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_check_black_24dp);
            doubleTick.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_green), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton1.setImageDrawable(doubleTick);
        }

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status) {
                    case NO:
                    case MAYBE:
                    case UNDEFINED:
                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                status = Event.PARTICIPATE.YES;

                                HttpGet get = new HttpGet(new AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        try {
                                            MainActivity.user = new User(new JSONObject(output));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                /*
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.sessionCredentials.getUserID() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                                */

                                floatingActionMenu.close(true);
                                setFloatingActionMenuTheme(status);
                                refreshFloatingActionButtons();

                                SharedPreferences prefs = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE);

                                // if first time user join an event
                                if (prefs.getString("addEventToCalender", "").equals("")) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventActivity.this);

                                    // set title
                                    alertDialogBuilder.setTitle(getResources().getString(R.string.add_to_calendar_action));

                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage(getResources().getString(R.string.add_to_calendar_are_you_sure))
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogAlert, int id) {
                                                    SharedPreferences.Editor prefs = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                                                    prefs.putString("addEventToCalender", "true");
                                                    prefs.apply();

                                                    addEventToCalendar();
                                                }
                                            })
                                            .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogAlert, int id) {
                                                    SharedPreferences.Editor prefs = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                                                    prefs.putString("addEventToCalender", "false");
                                                    prefs.apply();

                                                    dialogAlert.cancel();
                                                }
                                            });

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                                else if (prefs.getString("addEventToCalender", "true").equals("true"))
                                    addEventToCalendar();

                                try {
                                    JSONObject json = new JSONObject(output);
                                    event.refresh(json.getJSONObject("event"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                refreshAttendeesTextView();
                            }
                        });
                        /*
                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.sessionCredentials.getUserID() + "/status/going" + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                        */
                        break;

                    case YES:
                        floatingActionMenu.close(true);
                        break;

                    default:
                        break;
                }
            }
        });

        // fab 2

        this.floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab_item_2_event);
        floatingActionButton2.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton2.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable tick = ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_question_mark_black);
            tick.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_orange), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton2.setImageDrawable(tick);
        }

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status) {
                    case NO:
                    case YES:
                    case UNDEFINED:
                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                status = Event.PARTICIPATE.MAYBE;

                                HttpGet get = new HttpGet(new AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        try {
                                            MainActivity.user = new User(new JSONObject(output));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                /*
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.sessionCredentials.getUserID() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                                */

                                floatingActionMenu.close(true);
                                setFloatingActionMenuTheme(status);
                                refreshFloatingActionButtons();

                                try {
                                    JSONObject json = new JSONObject(output);
                                    event.refresh(json.getJSONObject("event"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                refreshAttendeesTextView();
                            }
                        });

                        /*
                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.sessionCredentials.getUserID() + "/status/maybe" + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                        */
                        break;

                    case MAYBE:
                        floatingActionMenu.close(true);
                        break;

                    default:
                        break;
                }
            }
        });

        // fab 3

        this.floatingActionButton3 = (FloatingActionButton) findViewById(R.id.fab_item_3_event);
        floatingActionButton3.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton3.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable close = ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_close_black_24dp);
            close.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.fab_red), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton3.setImageDrawable(close);
        }

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status) {
                    case YES:
                    case MAYBE:
                    case UNDEFINED:
                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                status = Event.PARTICIPATE.NO;

                                HttpGet get = new HttpGet(new AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        try {
                                            MainActivity.user = new User(new JSONObject(output));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                /*
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.sessionCredentials.getUserID() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                                */
                                floatingActionMenu.close(true);
                                setFloatingActionMenuTheme(status);
                                refreshFloatingActionButtons();

                                try {
                                    JSONObject json = new JSONObject(output);
                                    event.refresh(json.getJSONObject("event"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                refreshAttendeesTextView();
                            }
                        });

                        /*
                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.sessionCredentials.getUserID() + "/status/notgoing" + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                        */
                        break;

                    case NO:
                        floatingActionMenu.close(true);
                        break;

                    default:
                        break;
                }
            }
        });

        refreshFloatingActionButtons();

        Glide.with(this).load(HttpGet.IMAGEURL + event.getImage()).asBitmap().into(new BitmapImageViewTarget(headerImageView) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                headerImageView.setImageBitmap(Utils.darkenBitmap(bitmap));
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
            /*
            request.execute(HttpGet.ROOTASSOCIATION + "/"+ event.getAssociation() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
            */
        }
        else
            clubTextView.setText(club.getName());

        clubTextView.setTextColor(fgColor);

        // participants

        participantsImageView.setColorFilter(fgColor);
        participantsTextView.setTextColor(fgColor);

        refreshAttendeesTextView();

        // dateTextView

        dateImageView.setColorFilter(fgColor);

        SimpleDateFormat format = new SimpleDateFormat("EEEE dd/MM", Locale.FRANCE);
        SimpleDateFormat format_hours_minutes = new SimpleDateFormat("HH:mm", Locale.FRANCE);

        final int diffInDays = (int) ((event.getDateEnd().getTime() - event.getDateStart().getTime()) / (1000 * 60 * 60 * 24));
        if (diffInDays < 1 && event.getDateStart().getMonth() == event.getDateEnd().getMonth()) {
            String day = format.format(event.getDateStart());
            dateTextView.setText(day.replaceFirst(".", (day.charAt(0) + "").toUpperCase()) + " de " + format_hours_minutes.format(event.getDateStart()) + " à " + format_hours_minutes.format(event.getDateEnd()));
        } else {
            String start = format.format(event.getDateStart()) + " à " + format_hours_minutes.format(event.getDateStart());
            String end = format.format(event.getDateEnd()) + " à " + format_hours_minutes.format(event.getDateEnd());
            dateTextView.setText("Du " + start.replaceFirst(".", (start.charAt(0) + "").toUpperCase()) + " au " + end.replaceFirst(".", (end.charAt(0) + "").toUpperCase()));
        }

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

    public void refreshAttendeesTextView() {
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

    private void refreshFloatingActionButtons() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case YES:
                        floatingActionButton1.setVisibility(View.GONE);
                        floatingActionButton2.setVisibility(View.VISIBLE);
                        floatingActionButton3.setVisibility(View.VISIBLE);
                        break;

                    case MAYBE:
                        floatingActionButton1.setVisibility(View.VISIBLE);
                        floatingActionButton2.setVisibility(View.GONE);
                        floatingActionButton3.setVisibility(View.VISIBLE);
                        break;

                    case NO:
                        floatingActionButton1.setVisibility(View.VISIBLE);
                        floatingActionButton2.setVisibility(View.VISIBLE);
                        floatingActionButton3.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }
            }
        }, 500);
    }

    private void addEventToCalendar() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDateStart().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getDateEnd().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public FloatingActionMenu getFloatingActionMenu() {
        return floatingActionMenu;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }
}
