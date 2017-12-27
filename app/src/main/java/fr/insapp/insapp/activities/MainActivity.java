package fr.insapp.insapp.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import fr.insapp.insapp.App;
import fr.insapp.insapp.BuildConfig;
import fr.insapp.insapp.R;
import fr.insapp.insapp.adapters.ViewPagerAdapter;
import fr.insapp.insapp.fragments.ClubsFragment;
import fr.insapp.insapp.fragments.EventsFragment;
import fr.insapp.insapp.fragments.NotificationsFragment;
import fr.insapp.insapp.fragments.PostsFragment;
import fr.insapp.insapp.notifications.FirebaseMessaging;
import fr.insapp.insapp.utility.CustomTabsConnection;
import fr.insapp.insapp.utility.Utils;

public class MainActivity extends AppCompatActivity {

    public static final boolean dev = BuildConfig.DEBUG;

    public static CustomTabsConnection customTabsConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            getSupportActionBar().setTitle(Utils.getUser().getUsername());
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

        // Huawei protected apps

        ifHuaweiAlert();

        // topic notifications

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
        if (defaultSharedPreferences.getBoolean("notifications", true))
            FirebaseMessaging.subscribeToTopics();
        else
            FirebaseMessaging.unsubscribeFromTopics();
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class));
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

        if (MainActivity.customTabsConnection != null) {
            this.unbindService(MainActivity.customTabsConnection);
            MainActivity.customTabsConnection = null;
        }
    }

    private void ifHuaweiAlert() {
        if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());

            if (!defaultSharedPreferences.getBoolean("protected_apps", false)) {
                final SharedPreferences.Editor editor = defaultSharedPreferences.edit();

                Intent intent = new Intent();
                intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");

                if (isCallable(intent)) {
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(this);
                    dontShowAgain.setText(getString(R.string.protected_apps_skip));
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("protected_apps", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.protected_apps_dialog_title))
                            .setMessage(String.format(getString(R.string.protected_apps_dialog_message), getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton(getString(R.string.protected_apps_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    huaweiProtectedApps();
                                }
                            })
                            .setNegativeButton(R.string.close_button, null)
                            .show();
                }
                else {
                    editor.putBoolean("protected_apps", true);
                    editor.apply();
                }
            }
        }
    }

    private boolean isCallable(Intent intent) {
        final List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            Runtime.getRuntime().exec("am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity --user " + getUserSerial());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");

        if (null == userManager) {
            return "";
        }

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);

            if (userSerial != null) {
                return String.valueOf(userSerial);
            }
            else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
