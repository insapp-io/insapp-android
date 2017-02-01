package fr.insapp.insapp;

import android.content.Intent;
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

import de.hdodenhof.circleimageview.CircleImageView;
import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.EventsClubFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.Club;

/**
 * Created by thoma on 11/11/2016.
 */
public class ClubActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout relativeLayout;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private CircleImageView iconImageView;
    private ImageView headerImageView;
    private ViewPager viewPager;

    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        Intent intent = getIntent();
        this.club = intent.getParcelableExtra("club");

        this.relativeLayout = (RelativeLayout) findViewById(R.id.club_profile);
        this.nameTextView = (TextView) findViewById(R.id.club_name);
        this.descriptionTextView = (TextView) findViewById(R.id.club_description_text);
        this.iconImageView = (CircleImageView) findViewById(R.id.club_avatar);
        this.headerImageView = (ImageView) findViewById(R.id.header_image_club);

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_club);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
                } else if(isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        // view pager

        this.viewPager = (ViewPager) findViewById(R.id.viewpager_club);
        setupViewPager(viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.tabs_club);
        tabLayout.setupWithViewPager(viewPager);

        // dynamic color

        int bgColor = Color.parseColor("#" + club.getBgColor());
        int fgColor = Color.parseColor("#" + club.getFgColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setStatusBarScrimColor(bgColor);

        relativeLayout.setBackgroundColor(bgColor);
        tabLayout.setBackgroundColor(bgColor);

        if (fgColor == 0xffffffff)
            tabLayout.setTabTextColors(0xffdbdbdb, fgColor);
        else
            tabLayout.setTabTextColors(0xff5e5e5e, fgColor);

        nameTextView.setText(club.getName());
        nameTextView.setTextColor(fgColor);

        descriptionTextView.setText(club.getDescription());
        descriptionTextView.setTextColor(fgColor);

        collapsingToolbar.setCollapsedTitleTextColor(fgColor);

        Glide.with(this).load(HttpGet.IMAGEURL + club.getProfilPicture()).into(iconImageView);
        Glide.with(this).load(HttpGet.IMAGEURL + club.getCover()).into(headerImageView);

        // links

        Linkify.addLinks(descriptionTextView, Linkify.WEB_URLS);

        // send a mail

        Button club_contact = (Button) findViewById(R.id.club_contact);
        club_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment postsFragment = new PostsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("layout", R.layout.post);
        bundle1.putString("filter_club_id", club.getId());
        postsFragment.setArguments(bundle1);
        adapter.addFragment(postsFragment, getResources().getString(R.string.posts));

        Fragment eventsClubFragment = new EventsClubFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("layout", R.layout.row_event);
        bundle2.putString("filter_club_id", club.getId());
        bundle2.putParcelable("club", club);
        eventsClubFragment.setArguments(bundle2);
        adapter.addFragment(eventsClubFragment, getResources().getString(R.string.events));

        viewPager.setAdapter(adapter);
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

    public void sendEmail(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ club.getEmail() });
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}