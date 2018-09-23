package fr.insapp.insapp.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.style.URLSpan
import android.util.Patterns
import android.view.View
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.MainActivity

/**
 * Created by thomas on 08/02/2017.
 */

@SuppressLint("ParcelCreator")
class LinkSpan(private val context: Context?, url: String) : URLSpan(url) {
    private var uri: Uri? = null

    init {

        if (Patterns.WEB_URL.matcher(url).matches()) {
            this.uri = Uri.parse(url)
            MainActivity.customTabsConnection?.customTabsSession?.mayLaunchUrl(uri, null, null)
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)

        ds.isUnderlineText = false
    }

    override fun onClick(view: View) {
        if (uri != null) {
            val builder = CustomTabsIntent.Builder()

            builder.setShowTitle(true)
            if (context != null) {
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            }

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, uri)
        } else {
            super.onClick(view)
        }
    }
}