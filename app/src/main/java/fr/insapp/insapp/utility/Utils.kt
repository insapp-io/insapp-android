package fr.insapp.insapp.utility

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.URLSpan
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.IntroActivity
import fr.insapp.insapp.models.User
import java.util.*

object Utils {

    val user: User?
        get() {
            val user = Gson().fromJson(App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE).getString("user", ""), User::class.java)

            if (user == null) {
                disconnect()
            }

            return user
        }

    fun clearAndDisconnect() {
        if (user != null) {
            App.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE).edit().clear().apply()
            PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit().clear().apply()

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
            return ago + " " + (diffInDays / 7).toString() + " " + App.getAppContext().getString(R.string.ago_week)
        }

        // at least 1 day

        if (diffInDays >= 1) {
            return ago + " " + diffInDays.toLong().toString() + " " + App.getAppContext().getString(R.string.ago_day)
        }

        // at least 1 hour

        if (diffHours >= 1) {
            return ago + " " + diffHours.toString() + " " + App.getAppContext().getString(R.string.ago_hour)
        }

        // at least 1 minute

        return if (diffMinutes >= 1) {
            ago + " " + diffMinutes.toString() + " " + App.getAppContext().getString(R.string.ago_minute)
        } else App.getAppContext().getString(R.string.ago_now)

        // now

    }

    fun drawableProfileName(promo: String?, gender: String?): String {
        var drawableString = "avatar"

        if (promo != null && promo != "" && gender != null && gender != "") {
            var userPromotion = promo.toLowerCase(Locale.FRANCE)

            if (userPromotion.contains("staff")) {
                userPromotion = "staff"
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