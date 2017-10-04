package fr.insapp.insapp.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.EventsClubFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.utility.Utils;

/**
 * Created by thomas on 11/11/2016.
 */

public class ClubActivity extends AppCompatActivity {

    private Club club;

    private int bgColor;
    private int fgColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        // club

        Intent intent = getIntent();
        this.club = intent.getParcelableExtra("club");

        // Answers

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(club.getId())
                .putContentName(club.getName())
                .putContentType("Club"));

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.club_profile);
        final TextView nameTextView = (TextView) findViewById(R.id.club_name);
        final TextView descriptionTextView = (TextView) findViewById(R.id.club_description_text);
        final CircleImageView iconImageView = (CircleImageView) findViewById(R.id.club_avatar);
        final ImageView headerImageView = (ImageView) findViewById(R.id.header_image_club);

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_club);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // dynamic color

        this.bgColor = Color.parseColor("#" + club.getBgColor());
        this.fgColor = Color.parseColor("#" + club.getFgColor());

        // collapsing toolbar

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_club);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_club);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(club.getName());
                    isShow = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Drawable upArrow = ContextCompat.getDrawable(ClubActivity.this, R.drawable.abc_ic_ab_back_material);
                        upArrow.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    }
                }
                else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Drawable upArrow = ContextCompat.getDrawable(ClubActivity.this, R.drawable.abc_ic_ab_back_material);
                        upArrow.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    }
                }
            }
        });

        // dynamic color

        collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setStatusBarScrimColor(bgColor);

        relativeLayout.setBackgroundColor(bgColor);

        nameTextView.setText(club.getName());
        nameTextView.setTextColor(fgColor);

        descriptionTextView.setText(club.getDescription());
        descriptionTextView.setTextColor(fgColor);

        collapsingToolbar.setCollapsedTitleTextColor(fgColor);

        Glide
                .with(this)
                .load(ServiceGenerator.CDN_URL + club.getProfilePicture())
                .crossFade()
                .into(iconImageView);


        Glide
                .with(this)
                .load(ServiceGenerator.CDN_URL + club.getCover())
                .asBitmap()
                .into(new BitmapImageViewTarget(headerImageView) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);

                        headerImageView.setImageBitmap(Utils.darkenBitmap(bitmap));
                    }
                });

        // links

        Linkify.addLinks(descriptionTextView, Linkify.ALL);
        Utils.convertToLinkSpan(ClubActivity.this, descriptionTextView);

        // send a mail

        Button clubContactButton = (Button) findViewById(R.id.club_contact);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable email = ContextCompat.getDrawable(ClubActivity.this, R.drawable.ic_email_black_24dp);

            if (fgColor != 0xffffffff) {
                email.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP);
            }
            else {
                email.setColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP);
            }

            clubContactButton.setCompoundDrawablesWithIntrinsicBounds(email, null, null, null);
        }

        clubContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparentBlack));
        }

        // view pager

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_club);
        setupViewPager(viewPager, bgColor);

        if (fgColor != 0xffffffff) {
            setupViewPager(viewPager, fgColor);
        }
        else {
            setupViewPager(viewPager, bgColor);
        }

        // tab layout

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_club);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(bgColor);

        if (fgColor == 0xffffffff) {
            tabLayout.setTabTextColors(0xffdbdbdb, fgColor);
        }
        else {
            tabLayout.setTabTextColors(0xff5e5e5e, fgColor);
        }

        // recent apps system UI

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final String title = getString(R.string.app_name);
            final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            setTaskDescription(new ActivityManager.TaskDescription(title, icon, bgColor));
        }
    }

    private void setupViewPager(ViewPager viewPager, int swipeColor) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment postsFragment = new PostsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("layout", R.layout.post);
        bundle1.putString("filter_club_id", club.getId());
        bundle1.putInt("swipe_color", swipeColor);
        postsFragment.setArguments(bundle1);
        adapter.addFragment(postsFragment, getResources().getString(R.string.posts));

        Fragment eventsClubFragment = new EventsClubFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("layout", R.layout.row_event);
        bundle2.putString("filter_club_id", club.getId());
        bundle2.putInt("swipe_color", swipeColor);
        bundle2.putParcelable("club", club);
        eventsClubFragment.setArguments(bundle2);
        adapter.addFragment(eventsClubFragment, getResources().getString(R.string.events));

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                club.getEmail()
        });
        intent.putExtra(Intent.EXTRA_SUBJECT, "");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}