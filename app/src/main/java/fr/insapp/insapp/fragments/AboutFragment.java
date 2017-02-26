package fr.insapp.insapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.R;
import fr.insapp.insapp.SigninActivity;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thoma on 25/02/2017.
 */

public class AboutFragment extends Fragment {

    private View view;

    private Event event;

    private TextView descriptionTextView;

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    private Event.PARTICIPATE userParticipates = Event.PARTICIPATE.NO;

    private int bgColor;
    private int fgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // arguments

        final Bundle bundle = getArguments();
        if (bundle != null) {
            this.event = bundle.getParcelable("event");

            this.bgColor = bundle.getInt("bg_color");
            this.fgColor = bundle.getInt("fg_color");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_about, container, false);

        // description

        this.descriptionTextView = (TextView) view.findViewById(R.id.event_description);
        descriptionTextView.setText(event.getDescription());

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(getContext(), descriptionTextView);

        // floating action menu

        for (final String id : event.getAttendees()) {
            if (HttpGet.credentials.getUserID().equals(id)) {
                this.userParticipates = Event.PARTICIPATE.YES;
                break;
            }
        }

        for (final String id : event.getMaybe()) {
            if (HttpGet.credentials.getUserID().equals(id)) {
                this.userParticipates = Event.PARTICIPATE.MAYBE;
                break;
            }
        }

        for (final String id : event.getNotgoing()) {
            if (HttpGet.credentials.getUserID().equals(id)) {
                this.userParticipates = Event.PARTICIPATE.NO;
                break;
            }
        }

        this.floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.fab_participate_event);
        floatingActionMenu.setIconAnimated(false);

        switch (userParticipates) {
            case NO:
                floatingActionMenu.setMenuButtonColorNormal(bgColor);
                floatingActionMenu.setMenuButtonColorPressed(bgColor);
                floatingActionMenu.getMenuIconView().setColorFilter(fgColor);
                break;

            case MAYBE:
                floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_question_mark_black));
                floatingActionMenu.getMenuIconView().setColorFilter(0xffff9523);
                break;

            case YES:
                floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp));
                floatingActionMenu.getMenuIconView().setColorFilter(0xff4caf50);
                break;

            default:
                break;
        }

        final Date atm = Calendar.getInstance().getTime();
        if (event.getDateEnd().getTime() < atm.getTime())
            floatingActionMenu.setVisibility(View.GONE);

        // fab 1

        this.floatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.fab_item_1_event);
        floatingActionButton1.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton1.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable doubleTick = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp);
            doubleTick.setColorFilter(0xff4caf50, PorterDuff.Mode.SRC_ATOP);
            floatingActionButton1.setImageDrawable(doubleTick);
        }

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (userParticipates) {
                    case NO:
                    case MAYBE:
                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                userParticipates = Event.PARTICIPATE.YES;

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
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());

                                floatingActionMenu.close(true);
                                floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                                floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp));
                                floatingActionMenu.getMenuIconView().setColorFilter(0xff4caf50);

                                SharedPreferences prefs = getContext().getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE);

                                // if first time user join an event
                                if (prefs.getString("addEventToCalender", "").equals("")) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                                    // set title
                                    alertDialogBuilder.setTitle("Ajout au calendrier");

                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Voulez-vous ajouter les évènements auxquels vous participez à votre calendrier ?")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogAlert, int id) {
                                                    SharedPreferences.Editor prefs = getContext().getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
                                                    prefs.putString("addEventToCalender", "true");
                                                    prefs.apply();

                                                    addEventToCalendar();
                                                }
                                            })
                                            .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogAlert, int id) {
                                                    SharedPreferences.Editor prefs = getContext().getSharedPreferences(SigninActivity.class.getSimpleName(), SigninActivity.MODE_PRIVATE).edit();
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
                                    event.refresh(new JSONObject(output));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.credentials.getUserID() + "/status/going" + "?token=" + HttpGet.credentials.getSessionToken());
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

        this.floatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.fab_item_2_event);
        floatingActionButton2.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton2.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable tick = ContextCompat.getDrawable(getContext(), R.drawable.ic_question_mark_black);
            tick.setColorFilter(0xffff9523, PorterDuff.Mode.SRC_ATOP);
            floatingActionButton2.setImageDrawable(tick);
        }

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (userParticipates) {
                    case NO:
                    case YES:
                        HttpPost request = new HttpPost(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                userParticipates = Event.PARTICIPATE.MAYBE;

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
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());

                                floatingActionMenu.close(true);
                                floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                                floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_question_mark_black));
                                floatingActionMenu.getMenuIconView().setColorFilter(0xffff9523);

                                try {
                                    event.refresh(new JSONObject(output));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        request.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.credentials.getUserID() + "/status/maybe" + "?token=" + HttpGet.credentials.getSessionToken());
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

        this.floatingActionButton3 = (FloatingActionButton) view.findViewById(R.id.fab_item_3_event);
        floatingActionButton3.setLabelColors(bgColor, bgColor, 0x99ffffff);
        floatingActionButton3.setLabelTextColor(fgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable close = ContextCompat.getDrawable(getContext(), R.drawable.ic_close_black_24dp);
            close.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton3.setImageDrawable(close);
        }

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (userParticipates) {
                    case YES:
                    case MAYBE:
                        HttpDelete delete = new HttpDelete(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                userParticipates = Event.PARTICIPATE.NO;

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
                                get.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());

                                floatingActionMenu.close(true);
                                floatingActionMenu.setMenuButtonColorNormal(0xffffffff);
                                floatingActionMenu.setMenuButtonColorPressed(0xffffffff);
                                floatingActionMenu.getMenuIconView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_black_24dp));
                                floatingActionMenu.getMenuIconView().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));

                                try {
                                    event.refresh(new JSONObject(output));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        delete.execute(HttpGet.ROOTEVENT + "/" + event.getId() + "/participant/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                        break;

                    case NO:
                        floatingActionMenu.close(true);
                        break;

                    default:
                        break;
                }
            }
        });

        return view;
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
}
