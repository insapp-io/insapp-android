package fr.insapp.insapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.utility.File;
import fr.insapp.insapp.utility.Operation;

/**
 * Created by thomas on 15/12/2016.
 */

public class ProfileActivity extends AppCompatActivity {

    private EventRecyclerViewAdapter adapter;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CircleImageView avatarCircleImageView = (CircleImageView) findViewById(R.id.profil_avatar);
        TextView usernameTextView = (TextView) findViewById(R.id.profile_username);
        TextView nameTextView = (TextView) findViewById(R.id.profile_name);
        TextView emailTextView = (TextView) findViewById(R.id.profile_email);
        TextView promotionTextView = (TextView) findViewById(R.id.profile_class);
        TextView descriptionTextView = (TextView) findViewById(R.id.profile_description);

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }

        // adapter

        this.adapter = new EventRecyclerViewAdapter(this, R.layout.row_event_with_avatars);
        adapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnEventItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                startActivity(new Intent(getBaseContext(), EventActivity.class).putExtra("event", event));
            }
        });

        // recycler view

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_events_participate);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // fill the main layout

        final User user = new Gson().fromJson(getSharedPreferences("Credentials", MODE_PRIVATE).getString("session", ""), SessionCredentials.class).getUser();

        final int id = getResources().getIdentifier(Operation.drawableProfileName(user.getPromotion(), user.getGender()), "drawable", getPackageName());
        final Drawable drawable = ContextCompat.getDrawable(ProfileActivity.this, id);

        avatarCircleImageView.setImageDrawable(drawable);
        usernameTextView.setText(user.getUsername());
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        promotionTextView.setText(user.getPromotion());
        descriptionTextView.setText(user.getDescription());

        if (user.getName().isEmpty())
            nameTextView.setVisibility(View.GONE);

        if (user.getEmail().isEmpty())
            emailTextView.setVisibility(View.GONE);

        if (user.getPromotion().isEmpty())
            promotionTextView.setVisibility(View.GONE);

        // links

        Linkify.addLinks(emailTextView, Linkify.EMAIL_ADDRESSES);
        emailTextView.setLinkTextColor(Color.parseColor("#ffffff"));

        if (this.user != null)
            generateEvents();
    }

    @Override
    public void onResume(){
        super.onResume();

        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode);
        if (this.user == null) {
            generateEvents();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String barcode_data = preferences.getString("barcode", "");

            if (!barcode_data.equals("")) {
                ((TextView) findViewById(R.id.barcodeText)).setText(barcode_data);

                // barcode image

                Bitmap bitmap;

                try {
                    bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 700, 300);
                    barcodeImageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            else {
                barcodeImageView.setVisibility(View.GONE);
                findViewById(R.id.titleBarCode).setVisibility(View.GONE);
            }
        }
        else {
            barcodeImageView.setVisibility(View.GONE);
            findViewById(R.id.titleBarCode).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        if (this.user == null)
            menu.getItem(0).setTitle(R.string.delete_account);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_report:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);

                if (!user.getId().equals(MainActivity.getUser().getId())) {
                    alertDialogBuilder.setTitle(getString(R.string.report_user_action));
                    alertDialogBuilder
                            .setMessage(R.string.report_user_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    HttpPut report = new HttpPut(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            Toast.makeText(ProfileActivity.this, getString(R.string.report_user_success), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    /*
                                    report.execute(HttpGet.ROOTURL + "/report/user/" + user.getId() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                                    */
                                }
                            })
                            .setNegativeButton(getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    dialogAlert.cancel();
                                }
                            });
                }
                else {
                    alertDialogBuilder.setTitle(getString(R.string.delete_account_action));
                    alertDialogBuilder
                            .setMessage(R.string.delete_account_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    HttpDelete delete = new HttpDelete(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            HttpGet.sessionCredentials = null;
                                            File.writeSettings(ProfileActivity.this, "");

                                            Intent activity = new Intent(ProfileActivity.this, IntroActivity.class);
                                            activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(activity);

                                            Toast.makeText(ProfileActivity.this, R.string.delete_account_success, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                    /*
                                    delete.execute(HttpGet.ROOTUSER + "/" + HttpGet.sessionCredentials.getUserID() + "?token=" + HttpGet.sessionCredentials.getSessionToken());
                                     */
                                }
                            })
                            .setNegativeButton(getString(R.string.negative_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogAlert, int id) {
                                    dialogAlert.cancel();
                                }
                            });
                }

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void generateEvents(){
        adapter.getEvents().clear();
        adapter.notifyDataSetChanged();
        final List<Event> events = new ArrayList<>();

        /*
        for (String idEvent : user.getEvents()) {
                HttpGet request = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            Event event = new Event(new JSONObject(output));
                            events.add(event);
                        } catch (JSONException e) {

                        }

                        if(events.size() == user.getEvents().size())
                            showEvents(events);
                    }
                });
                request.execute(HttpGet.ROOTEVENT + "/" + idEvent + "?token=" + HttpGet.sessionCredentials.getSessionToken());
        }
        */
    }

    private void showEvents(List<Event> events){
        Date atm = Calendar.getInstance().getTime();

        Collections.sort(events);
        for (final Event event : events) {
            if (event.getDateEnd().getTime() > atm.getTime())
                adapter.addItem(event);
        }
    }



    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0x00FFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
