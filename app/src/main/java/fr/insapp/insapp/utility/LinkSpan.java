package fr.insapp.insapp.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import fr.insapp.insapp.WebViewActivity;

/**
 * Created by thomas on 08/02/2017.
 */

@SuppressLint("ParcelCreator")
public class LinkSpan extends URLSpan {

    private Context context;
    private String url;

    public LinkSpan(Context context, String url) {
        super(url);

        this.context = context;
        this.url = url;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}