package fr.insapp.insapp.utility

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.URLSpan
import android.widget.TextView
import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory
import com.google.gson.GsonBuilder
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.IntroActivity
import fr.insapp.insapp.models.User
import java.util.*

object Utils {

    val user: User?
        @Throws(NullPointerException::class)
        get() {
            val gson = GsonBuilder().registerTypeAdapterFactory(AutoParcelGsonTypeAdapterFactory()).create()
            val user = gson.fromJson(App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE).getString("user", ""), User::class.java)

            if (user == null) {
                disconnect()
            }

            return user
        }

    fun clearAndDisconnect() {
        val user = Utils.user
        if (user != null) {
            user.clearData()
            disconnect()
        }
    }

    private fun disconnect() {
        val context = App.getAppContext()

        val intent = Intent(context, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun convertToLinkSpan(context: Context?, textView: TextView) {
        val s = SpannableString(textView.text)
        val spans = s.getSpans(0, s.length, URLSpan::class.java)

        for (span in spans) {
            val start = s.getSpanStart(span)
            val end = s.getSpanEnd(span)

            s.removeSpan(span)
            s.setSpan(LinkSpan(context, span.url), start, end, 0)
        }

        textView.text = s
    }

    fun displayedDate(date: Date): String {
        val atm = Calendar.getInstance().time

        val diff = atm.time - date.time
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000)
        val diffInDays = (diff / (1000 * 60 * 60 * 24)).toInt()

        val ago = App.getAppContext().getString(R.string.ago)

        // at least 1 week

        if (diffInDays >= 7) {
            return ago + " " + Integer.toString(diffInDays / 7) + " " + App.getAppContext().getString(R.string.ago_week)
        }

        // at least 1 day

        if (diffInDays >= 1) {
            return ago + " " + java.lang.Long.toString(diffInDays.toLong()) + " " + App.getAppContext().getString(R.string.ago_day)
        }

        // at least 1 hour

        if (diffHours >= 1) {
            return ago + " " + java.lang.Long.toString(diffHours) + " " + App.getAppContext().getString(R.string.ago_hour)
        }

        // at least 1 minute

        return if (diffMinutes >= 1) {
            ago + " " + java.lang.Long.toString(diffMinutes) + " " + App.getAppContext().getString(R.string.ago_minute)
        } else App.getAppContext().getString(R.string.ago_now)

        // now

    }

    fun drawableProfileName(promo: String?, gender: String?): String {
        var drawableString = "avatar"

        if (promo != null && promo != "" && gender != null && gender != "") {
            var userPromotion = promo.toLowerCase()

            if (userPromotion.contains("personnel")) {
                userPromotion = "worker"
            } else if (userPromotion.contains("alternant")) {
                userPromotion = "apprentice"
            } else if (!userPromotion.contains("stpi") && Character.isDigit(userPromotion[0])) {
                userPromotion = userPromotion.substring(1)
            }

            drawableString += "_$userPromotion"
            drawableString += "_$gender"
        } else {
            drawableString = "avatar_default"
        }

        return drawableString
    }
}