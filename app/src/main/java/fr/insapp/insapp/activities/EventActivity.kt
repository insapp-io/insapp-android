package fr.insapp.insapp.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.ViewPagerAdapter
import fr.insapp.insapp.fragments.AboutFragment
import fr.insapp.insapp.fragments.CommentsEventFragment
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.*
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

    private var status: Event.PARTICIPATE = Event.PARTICIPATE.UNDEFINED

    private var bgColor: Int = 0
    private var fgColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val user = Utils.getUser()

        // toolbar

        setSupportActionBar(toolbar_event)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // event

        if (intent.getParcelableExtra<Event>("event") != null) {
            // coming from navigation

            this.event = intent.getParcelableExtra("event")
            generateActivity()

            // mark notification as seen

            if (intent.getParcelableExtra<Notification>("notification") != null) {
                val notification = intent.getParcelableExtra<Notification>("notification")

                val call = ServiceGenerator.create().markNotificationAsSeen(user.id, notification.id)
                call.enqueue(object : Callback<Notifications> {
                    override fun onResponse(call: Call<Notifications>, response: Response<Notifications>) {
                        if (!response.isSuccessful) {
                            Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Notifications>, t: Throwable) {
                        Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                    }
                })
            }
        } else {
            // coming from notification

            val call = ServiceGenerator.create().getEventFromId(intent.getStringExtra("ID"))
            call.enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.isSuccessful) {
                        event = response.body()!!
                        generateActivity()
                    } else
                        Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<Event>, t: Throwable) {
                    Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun generateActivity() {
        val user = Utils.getUser()

        // Answers

        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentId(event.id)
                .putContentName(event.name)
                .putContentType("Event")
                .putCustomAttribute("Attendees count", if (event.attendees == null) 0 else event.attendees.size)
                .putCustomAttribute("Interested count", if (event.maybe == null) 0 else event.maybe.size))

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

        if (fgColor != -0x1) {
            setupViewPager(viewpager_event)
        } else {
            setupViewPager(viewpager_event)
        }

        // tab layout

        tabs_event.setupWithViewPager(viewpager_event)
        tabs_event.setBackgroundColor(bgColor)

        if (fgColor == -0x1) {
            tabs_event.setTabTextColors(-0x242425, fgColor)
        } else {
            tabs_event.setTabTextColors(-0xa1a1a2, fgColor)
        }

        // floating action menu

        this.status = event.getStatusForUser(user.id)

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

    private fun setFloatingActionMenuTheme(status: Event.PARTICIPATE) {
        when (status) {
            Event.PARTICIPATE.UNDEFINED -> {
                fab_participate_event?.menuButtonColorNormal = bgColor
                fab_participate_event?.menuButtonColorPressed = bgColor
                fab_participate_event?.menuIconView?.setColorFilter(fgColor)
            }

            Event.PARTICIPATE.NO -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_close_black_24dp))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabRed))
            }

            Event.PARTICIPATE.MAYBE -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_question_mark_black))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabOrange))
            }

            Event.PARTICIPATE.YES -> {
                fab_participate_event?.menuButtonColorNormal = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuButtonColorPressed = ContextCompat.getColor(applicationContext, R.color.white)
                fab_participate_event?.menuIconView?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_check_black_24dp))
                fab_participate_event?.menuIconView?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabGreen))
            }
        }

    }

    private fun generateEvent() {
        val user = Utils.getUser()

        // fab 1: participate

        fab_item_1_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_1_event?.setLabelTextColor(fgColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val doubleTick = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_check_black_24dp)
            doubleTick!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabGreen), PorterDuff.Mode.SRC_ATOP)
            fab_item_1_event?.setImageDrawable(doubleTick)
        }

        fab_item_1_event?.setOnClickListener {
            when (status) {
                Event.PARTICIPATE.NO, Event.PARTICIPATE.MAYBE, Event.PARTICIPATE.UNDEFINED -> {
                    val call = ServiceGenerator.create().addParticipant(event.id, user.id, "going")
                    call.enqueue(object : Callback<EventInteraction> {
                        override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                            if (response.isSuccessful) {
                                status = Event.PARTICIPATE.YES

                                fab_participate_event?.close(true)
                                setFloatingActionMenuTheme(status)
                                refreshFloatingActionButtons()

                                event = response.body()!!.event
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
                                Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                            Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                Event.PARTICIPATE.YES -> fab_participate_event?.close(true)
            }
        }

        // fab 2: maybe

        fab_item_2_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_2_event?.setLabelTextColor(fgColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tick = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_question_mark_black)
            tick!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabOrange), PorterDuff.Mode.SRC_ATOP)
            fab_item_2_event?.setImageDrawable(tick)
        }

        fab_item_2_event?.setOnClickListener {
            when (status) {
                Event.PARTICIPATE.NO, Event.PARTICIPATE.YES, Event.PARTICIPATE.UNDEFINED -> {
                    val call = ServiceGenerator.create().addParticipant(event.id, user.id, "maybe")
                    call.enqueue(object : Callback<EventInteraction> {
                        override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                            if (response.isSuccessful) {
                                status = Event.PARTICIPATE.MAYBE

                                fab_participate_event?.close(true)
                                setFloatingActionMenuTheme(status)
                                refreshFloatingActionButtons()

                                event = response.body()!!.event
                                refreshAttendeesTextView()
                            } else {
                                Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                            Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                Event.PARTICIPATE.MAYBE -> fab_participate_event?.close(true)
            }
        }

        // fab 3: notgoing

        fab_item_3_event?.setLabelColors(bgColor, bgColor, -0x66000001)
        fab_item_3_event?.setLabelTextColor(fgColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val close = ContextCompat.getDrawable(this@EventActivity, R.drawable.ic_close_black_24dp)
            close!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.fabRed), PorterDuff.Mode.SRC_ATOP)
            fab_item_3_event?.setImageDrawable(close)
        }

        fab_item_3_event?.setOnClickListener {
            when (status) {
                Event.PARTICIPATE.YES, Event.PARTICIPATE.MAYBE, Event.PARTICIPATE.UNDEFINED -> {
                    val call = ServiceGenerator.create().addParticipant(event.id, user.id, "notgoing")
                    call.enqueue(object : Callback<EventInteraction> {
                        override fun onResponse(call: Call<EventInteraction>, response: Response<EventInteraction>) {
                            if (response.isSuccessful) {
                                status = Event.PARTICIPATE.NO

                                fab_participate_event?.close(true)
                                setFloatingActionMenuTheme(status)
                                refreshFloatingActionButtons()

                                event = response.body()!!.event
                                refreshAttendeesTextView()
                            } else {
                                Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<EventInteraction>, t: Throwable) {
                            Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                        }
                    })
                }

                Event.PARTICIPATE.NO -> fab_participate_event?.close(true)
            }
        }

        refreshFloatingActionButtons()

        /*
        Glide
            .with(this)
            .load(ServiceGenerator.CDN_URL + event.getImage())
            .asBitmap()
            .into(new BitmapImageViewTarget(headerImageView) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);

                    headerImageView.setImageBitmap(Utils.darkenBitmap(bitmap));
                }
            });
        */

        Glide
            .with(this)
            .load(ServiceGenerator.CDN_URL + event.image)
            .into(header_image_event)

        event_info?.setBackgroundColor(bgColor)

        // collapsing toolbar

        appbar_event.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = true
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsing_toolbar_event.title = event.name
                    isShow = true

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val upArrow = ContextCompat.getDrawable(this@EventActivity, R.drawable.abc_ic_ab_back_material)
                        upArrow!!.setColorFilter(fgColor, PorterDuff.Mode.SRC_ATOP)
                        supportActionBar?.setHomeAsUpIndicator(upArrow)
                    }
                } else if (isShow) {
                    collapsing_toolbar_event.title = " "
                    isShow = false

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val upArrow = ContextCompat.getDrawable(this@EventActivity, R.drawable.abc_ic_ab_back_material)
                        upArrow?.setColorFilter(-0x1, PorterDuff.Mode.SRC_ATOP)
                        supportActionBar?.setHomeAsUpIndicator(upArrow)
                    }
                }
            }
        })

        collapsing_toolbar_event.setCollapsedTitleTextColor(fgColor)
        collapsing_toolbar_event.setContentScrimColor(bgColor)
        collapsing_toolbar_event.setStatusBarScrimColor(bgColor)

        // club

        event_club_icon?.setColorFilter(fgColor)

        val call = ServiceGenerator.create().getClubFromId(event.association)
        call.enqueue(object : Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    event_club_text?.text = response.body()!!.name
                } else {
                    Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Club>, t: Throwable) {
                Toast.makeText(this@EventActivity, "EventActivity", Toast.LENGTH_LONG).show()
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
                    day.replaceFirst(".".toRegex(), (day[0] + "").toUpperCase()),
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
                    start.replaceFirst(".".toRegex(), (start[0] + "").toUpperCase()),
                    end.replaceFirst(".".toRegex(), (end[0] + "").toUpperCase()))
        }

        event_date_text?.setTextColor(fgColor)

        // transparent status bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparentBlack)
        }

        // recent apps system UI

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val title = getString(R.string.app_name)
            val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

            setTaskDescription(ActivityManager.TaskDescription(title, icon, bgColor))
        }
    }

    override fun finish() {
        val sendIntent = Intent()
        sendIntent.putExtra("event", event)

        setResult(Activity.RESULT_OK, sendIntent)

        super.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isTaskRoot) {
                    val i = Intent(this@EventActivity, MainActivity::class.java)
                    startActivity(i)
                } else {
                    finish()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun refreshAttendeesTextView() {
        val nbParticipants = if (event.attendees == null) 0 else event.attendees.size
        val nbInterested = if (event.maybe == null) 0 else event.maybe.size

        when (nbParticipants) {
            0 -> when (nbInterested) {
                0 -> event_participants_text?.text = resources.getString(R.string.no_attendees_no_interested)
                1 -> event_participants_text?.text = resources.getString(R.string.no_attendees_one_interested)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.no_attendees_x_interested), nbInterested)
            }
            1 -> when (nbInterested) {
                0 -> event_participants_text?.text = resources.getString(R.string.one_attendee_no_interested)
                1 -> event_participants_text?.text = resources.getString(R.string.one_attendee_one_interested)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.one_attendee_x_interested), nbInterested)
            }
            else -> when (nbInterested) {
                0 -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_no_interested), nbParticipants)
                1 -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_one_interested), nbParticipants)
                else -> event_participants_text?.text = String.format(resources.getString(R.string.x_attendees_x_interested), nbParticipants, nbInterested)
            }
        }
    }

    private fun refreshFloatingActionButtons() {
        val handler = Handler()
        handler.postDelayed({
            when (status) {
                Event.PARTICIPATE.YES -> {
                    fab_item_1_event!!.visibility = View.GONE
                    fab_item_2_event!!.visibility = View.VISIBLE
                    fab_item_3_event!!.visibility = View.VISIBLE
                }

                Event.PARTICIPATE.MAYBE -> {
                    fab_item_1_event!!.visibility = View.VISIBLE
                    fab_item_2_event!!.visibility = View.GONE
                    fab_item_3_event!!.visibility = View.VISIBLE
                }

                Event.PARTICIPATE.NO -> {
                    fab_item_1_event!!.visibility = View.VISIBLE
                    fab_item_2_event!!.visibility = View.VISIBLE
                    fab_item_3_event!!.visibility = View.GONE
                }

                Event.PARTICIPATE.UNDEFINED -> {
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
}
