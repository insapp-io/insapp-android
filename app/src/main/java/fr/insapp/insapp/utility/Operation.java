package fr.insapp.insapp.utility;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fr.insapp.insapp.models.User;

/**
 * Created by Antoine on 24/09/2016.
 */
public class Operation {

    public static Date stringToDate(String format, String dateInString, boolean timeZoneUTC){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        //if(timeZoneUTC)
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));

        /*if(!TimeZone.getDefault().equals(TimeZone.getTimeZone("GMT+2:00"))) {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+2:00"));
        }*/

        Date date = null;
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String displayedDate(Date date){

        Date atm = Calendar.getInstance().getTime();

        final long diff = atm.getTime() - date.getTime();
        final long diffMinutes = diff / (60 * 1000) % 60;
        final long diffHours = diff / (60 * 60 * 1000);
        final int diffInDays = (int) ((diff) / (1000 * 60 * 60 * 24));

        // At least 1 week
        if(diffInDays >= 7)
            return Integer.toString(diffInDays/7) + "s";
        // At least 1 day
        if(diffInDays >= 1)
            return Long.toString(diffInDays) + "j";
        // At least 1 hour
        if (diffHours >= 1)
            return Long.toString(diffHours) + "h";

        return Long.toString(diffMinutes) + "m";
    }

    public static String drawableProfilName(User user){

        // Initializing a String Array (PROMO)
        final String[] all_promos = new String[]{
                "",
                "1STPI",
                "2STPI",
                "3EII",
                "4EII",
                "5EII",
                "3GCU",
                "4GCU",
                "5GCU",
                "3GM",
                "4GM",
                "5GM",
                "3GMA",
                "4GMA",
                "5GMA",
                "3INFO",
                "4INFO",
                "5INFO",
                "3SGM",
                "4SGM",
                "5SGM",
                "3SRC",
                "4SRC",
                "5SRC",
                "Personnel/Enseignant"
        };

        final String[] promo_drawable_name = new String[]{
                "",
                "1stpi",
                "2stpi",
                "eii",
                "eii",
                "eii",
                "gcu",
                "gcu",
                "gcu",
                "gm",
                "gm",
                "gm",
                "gma",
                "gma",
                "gma",
                "info",
                "info",
                "info",
                "sgm",
                "sgm",
                "sgm",
                "src",
                "src",
                "src",
                "worker"
        };

        final String[] genre_drawable_name = new String[]{
                "",
                "female",
                "male"
        };

        System.out.println(user.getPromotion() + " et " + user.getGender());

        String drawable_string = "avatar_";
        if(!user.getPromotion().equals("") && !user.getGender().equals("")) {
            for (int i = 0; i < all_promos.length; i++) {
                if (promo_drawable_name[i].equals(user.getPromotion()))
                    drawable_string += promo_drawable_name[i];
            }

            for (int i = 0; i < genre_drawable_name.length; i++) {
                if (genre_drawable_name[i].equals(user.getGender()))
                    drawable_string += "_" + genre_drawable_name[i];
            }
        }
        else
            drawable_string = "avatar_default";

        return drawable_string;
    }
}
