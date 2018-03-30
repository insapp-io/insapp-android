package fr.insapp.insapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import fr.insapp.insapp.fragments.intro.*

/**
 * Created by thomas on 02/12/2016.
 */
class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroNewsFragment())
        addSlide(IntroEventsFragment())
        addSlide(IntroClubsFragment())
        addSlide(IntroNotificationsFragment())
        addSlide(IntroProfileFragment())

        showSkipButton(false)
        isProgressButtonEnabled = true
        setVibrate(false)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)

        val i = Intent(this, LegalConditionsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(i)

        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        val i = Intent(this, SignInActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(i)

        finish()
    }
}
