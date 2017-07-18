package fr.insapp.insapp.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.models.User;

public class MainActivity extends AppCompatActivity {

    public static final boolean dev = false;

    public static CustomTabsConnection customTabsConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase

        Log.d("Firebase", "Token: " + FirebaseInstanceId.getInstance().getToken());

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
            getSupportActionBar().setTitle(gson.fromJson(getSharedPreferences("User", MODE_PRIVATE).getString("user", ""), User.class).getUsername());
        }

        // view pager

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // tab layout

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // custom tabs optimization

        MainActivity.customTabsConnection = new CustomTabsConnection();
        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", customTabsConnection);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment postsFragment = new PostsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("layout", R.layout.post_with_avatars);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));

        try {
            Field cursorDrawable = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawable.setAccessible(true);
            cursorDrawable.set(searchView, R.drawable.cursor);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelGsonTypeAdapterFactory()).create();
                startActivity(new Intent(this, ProfileActivity.class).putExtra("user", gson.fromJson(getSharedPreferences("User", MODE_PRIVATE).getString("user", ""), User.class)));
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_legal_conditions:
                startActivity(new Intent(this, LegalConditionsActivity.class));
                break;

            case R.id.action_credits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unbindService(MainActivity.customTabsConnection);
        MainActivity.customTabsConnection = null;
    }
}
