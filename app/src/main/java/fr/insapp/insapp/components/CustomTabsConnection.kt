package fr.insapp.insapp.components

import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

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
