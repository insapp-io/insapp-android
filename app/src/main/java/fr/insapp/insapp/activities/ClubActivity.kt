package fr.insapp.insapp.activities

import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.view.MenuItem
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.ViewPagerAdapter
import fr.insapp.insapp.fragments.EventsClubFragment
import fr.insapp.insapp.fragments.PostsFragment
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Club
import fr.insapp.insapp.utility.DarkenTransformation
import fr.insapp.insapp.utility.GlideApp
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_club.*

/**
 * Created by thomas on 11/11/2016.
 */

class ClubActivity : AppCompatActivity() {

    private var club: Club? = null

    private var bgColor: Int = 0
    private var fgColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club)

        // club

        val intent = intent
        this.club = intent.getParcelableExtra("club")

        // Answers

        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentId(club!!.id)
                .putContentName(club!!.name)
                .putContentType("Club"))

        // toolbar

        setSupportActionBar(toolbar_club)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // dynamic color

        this.bgColor = Color.parseColor("#" + club!!.bgColor)
        this.fgColor = Color.parseColor("#" + club!!.fgColor)

        // collapsing toolbar

        appbar_club.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsing_toolbar_club.title = club!!.name
                    isShow = true

                    val upArrow = ContextCompat.getDrawable(this@ClubActivity, R.drawable.abc_ic_ab_back_material)
                    upArrow?.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP)
                    supportActionBar?.setHomeAsUpIndicator(upArrow)
                } else if (isShow) {
                    collapsing_toolbar_club.title = " "
                    isShow = false

                    val upArrow = ContextCompat.getDrawable(this@ClubActivity, R.drawable.abc_ic_ab_back_material)
                    upArrow?.setColorFilter(-0x1, PorterDuff.Mode.SRC_ATOP)
                    supportActionBar?.setHomeAsUpIndicator(upArrow)
                }
            }
        })

        // dynamic color

        collapsing_toolbar_club.setContentScrimColor(bgColor)
        collapsing_toolbar_club.setStatusBarScrimColor(bgColor)

        club_profile.setBackgroundColor(bgColor)

        club_name.text = club!!.name
        club_name.setTextColor(fgColor)

        club_description_text.text = club!!.description
        club_description_text.setTextColor(fgColor)

        collapsing_toolbar_club.setCollapsedTitleTextColor(fgColor)

        GlideApp
            .with(this)
            .load(ServiceGenerator.CDN_URL + club!!.profilePicture)
            .into(club_avatar)

        GlideApp
            .with(this)
            .load(ServiceGenerator.CDN_URL + club!!.cover)
            .transform(DarkenTransformation())
            .into(header_image_club)

        // links

        Linkify.addLinks(club_description_text, Linkify.ALL)
        Utils.convertToLinkSpan(this@ClubActivity, club_description_text)

        // send a mail

        val email = ContextCompat.getDrawable(this@ClubActivity, R.drawable.ic_email_black_24dp)

        if (fgColor != -0x1) {
            email?.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP)
        } else {
            email?.setColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP)
        }

        club_contact.setCompoundDrawablesWithIntrinsicBounds(email, null, null, null)
        club_contact.setOnClickListener { sendEmail() }

        // view pager

        setupViewPager(viewpager_club, bgColor)

        if (fgColor != -0x1) {
            setupViewPager(viewpager_club, fgColor)
        } else {
            setupViewPager(viewpager_club, bgColor)
        }

        // tab layout

        tabs_club.setupWithViewPager(viewpager_club)
        tabs_club.setBackgroundColor(bgColor)

        if (fgColor == -0x1) {
            tabs_club.setTabTextColors(-0x242425, fgColor)
        } else {
            tabs_club.setTabTextColors(-0xa1a1a2, fgColor)
        }

        // recent apps system UI

        val title = getString(R.string.app_name)
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        setTaskDescription(ActivityManager.TaskDescription(title, icon, bgColor))
    }

    private fun setupViewPager(viewPager: ViewPager, swipeColor: Int) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val postsFragment = PostsFragment()
        val bundle1 = Bundle()
        bundle1.putInt("layout", R.layout.post)
        bundle1.putString("filter_club_id", club!!.id)
        bundle1.putInt("swipe_color", swipeColor)
        postsFragment.arguments = bundle1
        adapter.addFragment(postsFragment, resources.getString(R.string.posts))

        val eventsClubFragment = EventsClubFragment()
        val bundle2 = Bundle()
        bundle2.putInt("layout", R.layout.row_event)
        bundle2.putString("filter_club_id", club!!.id)
        bundle2.putInt("swipe_color", swipeColor)
        bundle2.putParcelable("club", club)
        eventsClubFragment.arguments = bundle2
        adapter.addFragment(eventsClubFragment, resources.getString(R.string.events))

        viewPager.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)

        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(club!!.email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}