package fr.insapp.insapp;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

/**
 * Created by thomas on 09/02/2017.
 */

public class CustomTabsConnection extends CustomTabsServiceConnection {

    private CustomTabsClient customTabsClient;
    private CustomTabsSession customTabsSession;

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        this.customTabsClient = client;
        customTabsClient.warmup(0L);
        this.customTabsSession = customTabsClient.newSession(null);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public CustomTabsSession getCustomTabsSession() {
        return customTabsSession;
    }
}
