package fr.insapp.insapp;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Field;

import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.http.Client;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final boolean dev = true;

    public static final int REFRESH_TOKEN_MESSAGE = 5;

    private Toolbar toolbar;
    private MenuItem menuItem;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static User user;
    public static CustomTabsConnection customTabsConnection;

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

        FirebaseApp.initializeApp(getApplicationContext());

        // to make sure the token is refreshed
        String token = FirebaseInstanceId.getInstance().getToken();

        // view pager

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // tab layout

        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // user

        Call<User> call = ServiceGenerator.createService(Client.class).getUser(HttpGet.credentials.getUserID(), HttpGet.credentials.getSessionToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    toolbar.setTitle(user.getUsername());
                }
                else {
                    Toast.makeText(MainActivity.this, "MainActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "MainActivity", Toast.LENGTH_LONG).show();
            }
        });

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

        this.menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));

        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class).putExtra("user", user));
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

    /**
     *
     * @return
     */
    public static User getUser() {
        return user;
    }
}
