package fr.insapp.insapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import fr.insapp.insapp.WebViewActivity;

/**
 * Created by thomas on 01/02/2017.
 */

public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;


    protected void convertToLinkSpan(TextView textView) {
        Spannable s = new SpannableString(textView.getText());

        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);

            s.removeSpan(span);
            span = new LinkSpan(span.getURL());
            s.setSpan(span, start, end, 0);
        }

        textView.setText(s);
    }

    @SuppressLint("ParcelCreator")
    public class LinkSpan extends URLSpan {

        private String url;

        public LinkSpan(String url) {
            super(url);

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
}
