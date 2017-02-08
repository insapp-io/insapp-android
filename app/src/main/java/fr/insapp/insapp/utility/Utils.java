package fr.insapp.insapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;

        try {
            byte[] bytes = new byte[buffer_size];

            for(;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;

                os.write(bytes, 0, count);
            }
        } catch(Exception ex){}
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void convertToLinkSpan(Context context, TextView textView) {
        Spannable s = new SpannableString(textView.getText());

        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);

            s.removeSpan(span);
            span = new LinkSpan(context, span.getURL());
            s.setSpan(span, start, end, 0);
        }

        textView.setText(s);
    }
}