package fr.insapp.insapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import fr.insapp.insapp.fragments.EventsClubFragment;
import fr.insapp.insapp.fragments.PostsFragment;

/**
 * Created by thoma on 11/11/2016.
 */
public class ClubActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout relativeLayout;
    private TextView descriptionTextView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        this.relativeLayout = (RelativeLayout) findViewById(R.id.club_profile);
        this.descriptionTextView = (TextView) findViewById(R.id.club_description_text);

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_club);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
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
                    collapsingToolbar.setTitle("Club");
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.large_sample_0);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();
                if (vibrant != null) {
                    relativeLayout.setBackgroundColor(vibrant.getRgb());
                    tabLayout.setBackgroundColor(vibrant.getRgb());
                    collapsingToolbar.setContentScrimColor(vibrant.getRgb());
                    collapsingToolbar.setStatusBarScrimColor(darkVibrant.getRgb());

                    descriptionTextView.setTextColor(vibrant.getBodyTextColor());
                    tabLayout.setTabTextColors(vibrant.getTitleTextColor(), vibrant.getBodyTextColor());
                    tabLayout.setSelectedTabIndicatorColor(darkVibrant.getBodyTextColor());
                }
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
        bundle1.putInt("layout", R.layout.row_post);
        postsFragment.setArguments(bundle1);
        adapter.addFragment(postsFragment, "News");

        Fragment eventsClubFragment = new EventsClubFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("layout", R.layout.row_event);
        eventsClubFragment.setArguments(bundle2);
        adapter.addFragment(eventsClubFragment, "Events");

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
}