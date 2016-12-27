package fr.insapp.insapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
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

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionButton1, floatingActionButton2;

    private boolean userParticipates = false;

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

        final Drawable upArrow = ContextCompat.getDrawable(EventActivity.this, R.drawable.abc_ic_ab_back_material);
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

        // floating action menu

        for (String id : event.getParticipants()) {
            if (HttpGet.credentials.getUserID().equals(id)) {
                this.userParticipates = true;
                break;
            }
        }

        this.floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_event);
        floatingActionMenu.setIconAnimated(false);

        if (!userParticipates) {
            floatingActionMenu.setMenuButtonColorNormal(bgColor);
            floatingActionMenu.setMenuButtonColorPressed(bgColor);
            floatingActionMenu.getMenuIconView().setColorFilter(fgColor);
        } else {
            floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
            floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
            floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_check_black_24dp));
            floatingActionMenu.getMenuIconView().setColorFilter(0xff4caf50);
        }

        this.floatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab_item_1_event);
        floatingActionButton1.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton1.setLabelTextColor(fgColor);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userParticipates) {
                    HttpPost request = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            userParticipates = true;

                            floatingActionMenu.close(true);
                            floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                            floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                            floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_check_black_24dp));
                            floatingActionMenu.getMenuIconView().setColorFilter(0xff4caf50);

                            SharedPreferences prefs = getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE);

                            // if first time user join an event
                            if (prefs.getString("addEventToCalender", "").equals("")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventActivity.this);

                                // set title
                                alertDialogBuilder.setTitle("Ajout au calendrier");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Voulez-vous ajouter les évènements auquels vous participer dans votre calendrier ?")
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
                                                SharedPreferences.Editor prefs = getSharedPreferences(
                                                        SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                                                prefs.putString("addEventToCalender", "false");
                                                prefs.apply();

                                                dialogAlert.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }
                            else if(prefs.getString("addEventToCalender", "true").equals("true")){
                                addEventToCalendar();
                            }
                        }
                    });
                    request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
                else
                    floatingActionMenu.close(true);
            }
        });

        this.floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab_item_2_event);
        floatingActionButton2.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton2.setLabelTextColor(fgColor);

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userParticipates) {
                    HttpDelete delete = new HttpDelete(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            userParticipates = false;

                            floatingActionMenu.close(true);
                            floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                            floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                            floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.ic_close_black_24dp));
                            floatingActionMenu.getMenuIconView().setColorFilter(R.color.colorAccent);
                        }
                    });
                    delete.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
                else
                    floatingActionMenu.close(true);
            }
        });

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }
    }

    public void addEventToCalendar() {
        /*Cursor cursor = null;
        int[] calIds = null;
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,};
        ContentResolver cr = EventProfil.this.getContentResolver();
        cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), projection, null, null, null);
        if (cursor.moveToFirst()) {
            final String[] calNames = new String[cursor.getCount()];
            calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
        }
        TimeZone tZone = TimeZone.getTimeZone("UTC");
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, mEvent.getDateStart().getTime());
            values.put(CalendarContract.Events.DTEND, mEvent.getDateEnd().getTime());
            values.put(CalendarContract.Events.TITLE, mEvent.getName());
            values.put(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());
            values.put(CalendarContract.Events.CALENDAR_ID, calIds[0]);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, tZone.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    Toast.makeText(EventProfil.this, "L'accès au calendrier est désactivé", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Uri mInsert = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Toast.makeText(EventProfil.this, "Évènement ajouté au calendrier", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EventProfil.this, "Erreur d'ajout au calendrier", Toast.LENGTH_SHORT).show();
        }*/
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        //intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,cal.getTimeInMillis());
        //intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,cal.getTimeInMillis()+60*60*1000);

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDateStart().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getDateEnd().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());
        //intent.putExtra(CalendarContract.EVENT_LOCATION, "Event Address");
        //intent.putExtra(CalendarContract.RRULE, "FREQ=YEARLY");
        startActivity(intent);
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
