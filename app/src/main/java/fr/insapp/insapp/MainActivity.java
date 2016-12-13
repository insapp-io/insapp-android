package fr.insapp.insapp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout searchContainer;
    private Menu menu;
    private MenuItem menuItem;
    private SearchView searchView;
    private EditText toolbarSearchView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // view pager

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // tab layout

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
        adapter.addFragment(new NotificationsFragment(), "Notifications");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);

        this.menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        this.searchView = searchView;

        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_credits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
