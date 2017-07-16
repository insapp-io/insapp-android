package fr.insapp.insapp.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;

import fr.insapp.insapp.activities.MainActivity;
import fr.insapp.insapp.R;

/**
 * Created by thomas on 08/02/2017.
 */

@SuppressLint("ParcelCreator")
public class LinkSpan extends URLSpan {

    private Context context;
    private Uri uri;

    public LinkSpan(Context context, String url) {
        super(url);

        this.context = context;

        if (Patterns.WEB_URL.matcher(url).matches()) {
            this.uri = Uri.parse(url);

            if (MainActivity.customTabsConnection != null && MainActivity.customTabsConnection.getCustomTabsSession() != null)
                MainActivity.customTabsConnection.getCustomTabsSession().mayLaunchUrl(uri, null, null);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View view) {
        if (uri != null) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

            builder.setShowTitle(true);
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, uri);
        }
        else {
            super.onClick(view);
        }
    }
}