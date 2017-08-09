package fr.insapp.insapp.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.insapp.insapp.App;
import fr.insapp.insapp.R;

public class Utils {

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

    public static Bitmap darkenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xffbababa, 0x00000000);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);

        return bitmap;
    }

    public static Date parseMongoDate(String mongoDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ");

        try {
            return df.parse(mongoDate);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String displayedDate(Date date){
        final Date atm = Calendar.getInstance().getTime();

        final long diff = atm.getTime() - date.getTime();
        final long diffMinutes = diff / (60 * 1000) % 60;
        final long diffHours = diff / (60 * 60 * 1000);
        final int diffInDays = (int) ((diff) / (1000 * 60 * 60 * 24));

        final String ago = App.getAppContext().getString(R.string.ago);

        // at least 1 week

        if (diffInDays >= 7) {
            return ago + " " + Integer.toString(diffInDays / 7) + " " + App.getAppContext().getString(R.string.ago_week);
        }

        // at least 1 day

        if (diffInDays >= 1) {
            return ago + " " + Long.toString(diffInDays) + " " + App.getAppContext().getString(R.string.ago_day);
        }

        // at least 1 hour

        if (diffHours >= 1) {
            return ago + " " + Long.toString(diffHours) + " " + App.getAppContext().getString(R.string.ago_hour);
        }

        // at least 1 minute

        if (diffMinutes >= 1) {
            return ago + " " + Long.toString(diffMinutes) + " " + App.getAppContext().getString(R.string.ago_minute);
        }

        // now

        return App.getAppContext().getString(R.string.ago_now);
    }

    public static String drawableProfileName(String promo, String gender){
        String drawable_string = "avatar";

        if (!promo.equals("") && !gender.equals("")) {
            String userPromotion = promo.toLowerCase();

            if (userPromotion.contains("personnel")) {
                userPromotion = "worker";
            }
            else if (userPromotion.contains("alternant")) {
                userPromotion = "apprentice";
            }
            else if (!userPromotion.contains("stpi") && Character.isDigit(userPromotion.charAt(0))) {
                userPromotion = userPromotion.substring(1);
            }

            drawable_string += "_" + userPromotion;
            drawable_string += "_" + gender;
        }
        else {
            drawable_string = "avatar_default";
        }

        return drawable_string;
    }
}