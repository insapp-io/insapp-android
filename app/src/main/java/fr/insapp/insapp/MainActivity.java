package fr.insapp.insapp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;
import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.utility.Operation;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout searchContainer;
    private Menu menu;
    private MenuItem menuItem;
    private SearchView searchView;
    private EditText toolbarSearchView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar

        this.toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // view pager

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // tab layout

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // user

        HttpGet get = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject json = null;
                try {
                    user = new User(new JSONObject(output));
                    toolbar.setTitle(user.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        get.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment postsFragment = new PostsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("layout", R.layout.row_post_with_avatars);
        postsFragment.setArguments(bundle1);
        adapter.addFragment(postsFragment, getResources().getString(R.string.posts));

        Fragment eventsFragment = new EventsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("layout", R.layout.row_event_with_avatars);
        eventsFragment.setArguments(bundle2);
        adapter.addFragment(eventsFragment, getResources().getString(R.string.events));

        adapter.addFragment(new ClubsFragment(), getResources().getString(R.string.clubs));
        adapter.addFragment(new NotificationsFragment(), getResources().getString(R.string.notifications));

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
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class).putExtra("user", user));
                break;

            case R.id.action_credits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
