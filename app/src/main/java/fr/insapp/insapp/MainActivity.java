package fr.insapp.insapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment postsFragment = new PostsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("layout", R.layout.row_post_with_avatars);
        postsFragment.setArguments(bundle1);
        adapter.addFragment(postsFragment, "News");

        Fragment eventsFragment = new EventsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("layout", R.layout.row_event_with_avatars);
        eventsFragment.setArguments(bundle2);
        adapter.addFragment(eventsFragment, "Events");


        adapter.addFragment(new ClubsFragment(), "Associations");
        adapter.addFragment(new NotificationsFragment(), "Activité");

        viewPager.setAdapter(adapter);
    }
}
