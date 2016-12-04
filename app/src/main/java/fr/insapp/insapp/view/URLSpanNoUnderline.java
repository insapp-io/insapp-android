package fr.insapp.insapp.view;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by thoma on 04/12/2016.
 */
@SuppressLint("ParcelCreator")
public class URLSpanNoUnderline extends URLSpan {

    public URLSpanNoUnderline(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        super.updateDrawState(tp);
        tp.setUnderlineText(false);
    }
}