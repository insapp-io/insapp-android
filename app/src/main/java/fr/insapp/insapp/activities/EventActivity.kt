package fr.insapp.insapp.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.android.material.appbar.AppBarLayout
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.ViewPagerAdapter
import fr.insapp.insapp.fragments.AboutFragment
import fr.insapp.insapp.fragments.CommentsEventFragment
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.*
import fr.insapp.insapp.utility.DarkenTransformation
import fr.insapp.insapp.utility.GlideApp
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thomas on 05/12/2016.
 */

class EventActivity : AppCompatActivity() {

    private lateinit var event: Event

    private var status: AttendanceStatus = AttendanceStatus.UNDEFINED

    private var bgColor: Int = 0
    private var fgColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Utils.user

        // event

        if (intent.getParcelableExtra<Event>("event") != null) {
            // coming from navigation

            this.event = intent.getParcelableExtra("event")

            generateActivity()

            // mark notification as seen

            if (intent.getParcelableExtra<Notification>("notification") != null) {
                val notification = intent.getParcelableExtra<Notification>("notification")

                if (user != null) {
                    val call = ServiceGenerator.create().markNotificationAsSeen(user.id, notification.id)
                    call.enqueue(object : Callback<Notifications> {
                        override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                            if (!response.isSuccessful) {
                                Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Notifications>, t: Throwable) {
                            Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.d(TAG, "Couldn't mark the notification as seen: user is null")
                }
            }
        } else {
            // coming from notification
            setContentView(R.layout.loading)

            val call = ServiceGenerator.create().getEventFromId(intent.getStringExtra("ID"))
            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        event = result
                        generateActivity()
                    } else {
                        Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Event>, t: Throwable) {
                    Toast.makeText(this@EventActivity, R.string.check_internet_connection, Toast.LENGTH_LONG).show()

                    // Open the application
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            })
        }
    }

    private fun generateActivity() {
        setContentView(R.layout.activity_event)

        // toolbar

        setSupportActionBar(toolbar_event)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = Utils.user

        // Answers

        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentId(event.id)
                .putContentName(event.name)
                .putContentType("Event")
                .putCustomAttribute("Attendees count", event.attendees?.size ?: 0)
                .putCustomAttribute("Interested count", event.maybe?.size ?: 0))

        event_participants_layout.setOnClickListener {
            val newIntent = Intent(this@EventActivity, AttendeesActivity::class.java)

            newIntent.putExtra("attendees", event.attendees as? ArrayList<String>)
            newIntent.putExtra("maybe", event.maybe as? ArrayList<String>)

            startActivity(newIntent)
        }

        // dynamic color

        this.bgColor = Color.parseColor("#" + event.bgColor)
        this.fgColor = Color.parseColor("#" + event.fgColor)

        // view pager

        setupViewPager(viewpager_event)

        // tab layout

        tabs_event.setupWithViewPager(viewpager_event)
        tabs_event.setBackgroundColor(bgColor)

        if (fgColor == -0x1) {
            tabs_event.setTabTextColors(-0x242425, fgColor)
        } else {
            tabs_event.setTabTextColors(-0xa1a1a2, fgColor)
        }

        // floating action menu

        user?.let {
            this.status = event.getStatusForUser(user.id)
        }

        // fab style

        fab_participate_event?.isIconAnimated = false
        setFloatingActionMenuTheme(status)

        // hide fab is event is past

        val atm = Calendar.getInstance().time
        if (event.dateEnd.time < atm.time) {
            fab_participate_event?.visibility = View.GONE
        }

        generateEvent()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val aboutFragment = AboutFragment()
        val bundle1 = Bundle()
        bundle1.putParcelable("event", event)
        bundle1.putInt("bg_color", bgColor)
        bundle1.putInt("fg_color", fgColor)
        aboutFragment.arguments = bundle1
        adapter.addFragment(aboutFragment, resources.getString(R.string.about))

        val commentsEventFragment = CommentsEventFragment()
        val bundle2 = Bundle()
        bundle2.putParcelable("event", event)
        commentsEventFragment.arguments = bundle2
        adapter.addFragment(commentsEventFragment, String.format(resources.getString(R.string.x_comments), event.comments.size))

        viewPager.adapter = adapter
    }

    private fun setFloatingActionMenuTheme(status: AttendanceStatus) {
        when (status) {
            AttendanceStatus.UNDEFINED -> {
                fab_participate_event?.menuButtonColorNormal = bgColor
                fab_participate_event?.menuButtonColorPressed = bgColor
                fab_participate_event?.menuIconView?.setColorFilter(fgColor)
            }

            AttendanceStatus.NO -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_close_black_24dp))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabRed))
            }

            AttendanceStatus.MAYBE -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_question_mark_black))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabOrange))
            }

            AttendanceStatus.YES -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_check_black_24dp))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabGreen))
            }
        }

    }

    private fun generateEvent() {
        val user = Utils.user

        // fab 1: participate

        fab_item_1_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_1_event?.setLabelTextColor(fgColor)

        val doubleTick = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_check_black_24dp)
        doubleTick?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabGreen), PorterDuff.Mode.SRC_ATOP)
        fab_item_1_event?.setImageDrawable(doubleTick)

        fab_item_1_event?.setOnClickListener {
            when (status) {
                AttendanceStatus.NO, AttendanceStatus.MAYBE, AttendanceStatus.UNDEFINED -> {
                    if (user != null) {
                        val call = ServiceGenerator.create().addAttendee(event.id, user.id, "going")
                        call.enqueue(object : Callback<EventInteraction> {
                            override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                                val result = response.body()
                                if (response.isSuccessful && result != null) {
                                    status = AttendanceStatus.YES

                                    fab_participate_event?.close(true)
                                    setFloatingActionMenuTheme(status)
                                    refreshFloatingActionButtons()

                                    event = result.event
                                    refreshAttendeesTextView()

                                    // if first time user join an event

                                    val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
                                    if (defaultSharedPreferences.getBoolean("calendar", false)) {
                                        val alertDialogBuilder = AlertDialog.Builder(this@EventActivity)

                                        // set title
                                        alertDialogBuilder.setTitle(resources.getString(R.string.add_to_calendar_action))

                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage(resources.getString(R.string.add_to_calendar_are_you_sure))
                                                .setCancelable(false)
                                                .setPositiveButton(R.string.positive_button) { _, _ -> addEventToCalendar() }
                                                .setNegativeButton(R.string.negative_button) { dialogAlert, _ -> dialogAlert.cancel() }

                                        // create alert dialog

                                        val alertDialog = alertDialogBuilder.create()
                                        alertDialog.show()
                                    }
                                } else {
                                    Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                                Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Log.d(TAG, "Couldn't update the attendance status: user is null")
                    }
                }

                AttendanceStatus.YES -> fab_participate_event?.close(true)
            }
        }

        // fab 2: maybe

        fab_item_2_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_2_event?.setLabelTextColor(fgColor)

        val tick = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_question_mark_black)
        tick?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabOrange), PorterDuff.Mode.SRC_ATOP)
        fab_item_2_event?.setImageDrawable(tick)

        fab_item_2_event?.setOnClickListener {
            when (status) {
                AttendanceStatus.NO, AttendanceStatus.YES, AttendanceStatus.UNDEFINED -> {
                    if (user != null) {
                        val call = ServiceGenerator.create().addAttendee(event.id, user.id, "maybe")
                        call.enqueue(object : Callback<EventInteraction> {
                            override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                                val result = response.body()
                                if (response.isSuccessful && result != null) {
                                    status = AttendanceStatus.MAYBE

                                    fab_participate_event?.close(true)
                                    setFloatingActionMenuTheme(status)
                                    refreshFloatingActionButtons()

                                    event = result.event
                                    refreshAttendeesTextView()
                                } else {
                                    Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                                Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Log.d(TAG, "Couldn't update the attendance status: user is null")
                    }
                }

                AttendanceStatus.MAYBE -> fab_participate_event?.close(true)
            }
        }

        // fab 3: notgoing

        fab_item_3_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_3_event?.setLabelTextColor(fgColor)

        val close = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_close_black_24dp)
        close?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabRed), PorterDuff.Mode.SRC_ATOP)
        fab_item_3_event?.setImageDrawable(close)

        fab_item_3_event?.setOnClickListener {
            when (status) {
                AttendanceStatus.YES, AttendanceStatus.MAYBE, AttendanceStatus.UNDEFINED -> {
                    if (user != null) {
                        val call = ServiceGenerator.create().addAttendee(event.id, user.id, "notgoing")
                        call.enqueue(object : Callback<EventInteraction> {
                            override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                                val result = response.body()
                                if (response.isSuccessful && result != null) {
                                    status = AttendanceStatus.NO

                                    fab_participate_event?.close(true)
                                    setFloatingActionMenuTheme(status)
                                    refreshFloatingActionButtons()

                                    event = result.event
                                    refreshAttendeesTextView()
                                } else {
                                    Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                                Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Log.d(TAG, "Couldn't update the attendance status: user is null")
                    }
                }

                AttendanceStatus.NO -> fab_participate_event?.close(true)
            }
        }

        refreshFloatingActionButtons()

        GlideApp
            .with(this)
            .load(ServiceGenerator.CDN_URL + event.image)
            .transform(DarkenTransformation())
            .into(header_image_event)

        event_info?.setBackgroundColor(bgColor)

        // collapsing toolbar

        appbar_event.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                if (scrollRange + verticalOffset == 0) {
                    isShow = true

                    val upArrow = ContextCompat.getDrawable(this@EventActivity, R.drawable.abc_ic_ab_back_material)
                    upArrow?.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP)
                    supportActionBar?.setHomeAsUpIndicator(upArrow)
                } else if (isShow) {
                    isShow = false

                    val upArrow = ContextCompat.getDrawable(this@EventActivity, R.drawable.abc_ic_ab_back_material)
                    upArrow?.setColorFilter(-0x1, PorterDuff.Mode.SRC_ATOP)
                    supportActionBar?.setHomeAsUpIndicator(upArrow)
                }
            }
        })

        collapsing_toolbar_event.title = event.name

        collapsing_toolbar_event.setCollapsedTitleTextColor(fgColor)
        collapsing_toolbar_event.setContentScrimColor(bgColor)
        collapsing_toolbar_event.setStatusBarScrimColor(bgColor)

        // club

        event_club_icon?.setColorFilter(fgColor)

        val call = ServiceGenerator.create().getAssociationFromId(event.association)
        call.enqueue(object : Callback<Association> {
            override fun onResponse(call: Call<Association>, response: Response<Association>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    event_club_text?.text = result.name
                } else {
                    Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Association>, t: Throwable) {
                Toast.makeText(this@EventActivity, TAG, Toast.LENGTH_LONG).show()
            }
        })

        event_club_text?.setTextColor(fgColor)

        // participants

        event_participants_icon?.setColorFilter(fgColor)
        event_participants_text?.setTextColor(fgColor)

        refreshAttendeesTextView()

        // dateTextView

        event_date_icon?.setColorFilter(fgColor)

        val format = SimpleDateFormat("EEEE dd/MM", Locale.FRANCE)
        val formatHoursMinutes = SimpleDateFormat("HH:mm", Locale.FRANCE)
        val diffInDays = ((event.dateEnd.time - event.dateStart.time) / (1000 * 60 * 60 * 24)).toInt()

        val calendar = Calendar.getInstance()
        calendar.time = event.dateStart
        val dateStartMonth = calendar.get(Calendar.MONTH)
        calendar.time = event.dateStart
        val dateEndMonth = calendar.get(Calendar.MONTH)

        if (diffInDays < 1 && dateStartMonth == dateEndMonth) {
            val day = format.format(event.dateStart)

            event_date_text?.text = String.format(resources.getString(R.string.event_date_inf),
                    day.replaceFirst(".".toRegex(), (day[0] + "").toUpperCase(Locale.FRANCE)),
                    formatHoursMinutes.format(event.dateStart),
                    formatHoursMinutes.format(event.dateEnd))
        } else {
            val start = String.format(resources.getString(R.string.event_date_sup_start_end),
                    format.format(event.dateStart),
                    formatHoursMinutes.format(event.dateStart))

            val end = String.format(resources.getString(R.string.event_date_sup_start_end),
                    format.format(event.dateEnd),
                    formatHoursMinutes.format(event.dateEnd))

            event_date_text?.text = String.format(resources.getString(R.string.event_date_sup),
                    start.replaceFirst(".".toRegex(), (start[0] + "").toUpperCase(Locale.FRANCE)),
                    end.replaceFirst(".".toRegex(), (end[0] + "").toUpperCase(Locale.FRANCE)))
        }

        event_date_text?.setTextColor(fgColor)

        // recent apps system UI

        val title = getString(R.string.app_name)
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        setTaskDescription(ActivityManager.TaskDescription(title, icon, bgColor))
    }

    override fun finish() {
        if (::event.isInitialized) {
            val sendIntent = Intent()
            sendIntent.putExtra("event", event)
            setResult(Activity.RESULT_OK, sendIntent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }

        super.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isTaskRoot) {
                    startActivity(Intent(this@EventActivity, MainActivity::class.java))
                } else {
                    finish()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun refreshAttendeesTextView() {
        val attendees = event.attendees?.size ?: 0
        val unsure = event.maybe?.size ?: 0

        when (attendees) {
            0 -> when (unsure) {
                0 -> event_participants_text?.text = resources.getString(R.string.no_attendees_no_interested)
                1 -> event_participants_text?.text = resources.getString(R.string.no_attendees_one_interested)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.no_attendees_x_interested), unsure)
            }
            1 -> when (unsure) {
                0 -> event_participants_text?.text = resources.getString(R.string.one_attendee_no_interested)
                1 -> event_participants_text?.text = resources.getString(R.string.one_attendee_one_interested)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.one_attendee_x_interested), unsure)
            }
            else -> when (unsure) {
                0 -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_no_interested), attendees)
                1 -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_one_interested), attendees)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_x_interested), attendees, unsure)
            }
        }
    }

    private fun refreshFloatingActionButtons() {
        val handler = Handler()
        handler.postDelayed({
            when (status) {
                AttendanceStatus.YES -> {
                    fab_item_1_event?.visibility = View.GONE
                    fab_item_2_event?.visibility = View.VISIBLE
                    fab_item_3_event?.visibility = View.VISIBLE
                }

                AttendanceStatus.MAYBE -> {
                    fab_item_1_event?.visibility = View.VISIBLE
                    fab_item_2_event?.visibility = View.GONE
                    fab_item_3_event?.visibility = View.VISIBLE
                }

                AttendanceStatus.NO -> {
                    fab_item_1_event?.visibility = View.VISIBLE
                    fab_item_2_event?.visibility = View.VISIBLE
                    fab_item_3_event?.visibility = View.GONE
                }

                AttendanceStatus.UNDEFINED -> {
                }
            }
        }, 500)
    }

    private fun addEventToCalendar() {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.dateStart.time)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.dateEnd.time)
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        intent.putExtra(CalendarContract.Events.TITLE, event.name)
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.description)

        startActivity(intent)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus

            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    companion object {

        const val TAG = "EventActivity"
    }
}

fun Event.getStatusForUser(userId: String): AttendanceStatus {
    notgoing?.let {
        for (id in notgoing) {
            if (userId == id) {
                return AttendanceStatus.NO
            }
        }
    }

    maybe?.let {
        for (id in maybe) {
            if (userId == id) {
                return AttendanceStatus.MAYBE
            }
        }
    }

    attendees?.let {
        for (id in attendees) {
            if (userId == id) {
                return AttendanceStatus.YES
            }
        }
    }

    return AttendanceStatus.UNDEFINED
}