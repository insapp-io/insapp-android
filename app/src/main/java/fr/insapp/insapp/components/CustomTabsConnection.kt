package fr.insapp.insapp.components

import android.content.ComponentName
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsServiceConnection
import android.support.customtabs.CustomTabsSession

/**
 * Created by thomas on 09/02/2017.
 */

class CustomTabsConnection : CustomTabsServiceConnection() {

    private var customTabsClient: CustomTabsClient? = null

    var customTabsSession: CustomTabsSession? = null
        private set

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        this.customTabsClient = client

        customTabsClient?.warmup(0L)
        this.customTabsSession = customTabsClient?.newSession(null)
    }

    override fun onServiceDisconnected(name: ComponentName) {

    }
}
