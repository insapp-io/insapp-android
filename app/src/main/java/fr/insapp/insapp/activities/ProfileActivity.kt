package fr.insapp.insapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.EventRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.models.User
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by thomas on 15/12/2016.
 */

class ProfileActivity : AppCompatActivity() {

    private var adapter: EventRecyclerViewAdapter? = null

    private var user: User? = null
    private var isOwner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // user

        val intent = intent
        this.user = intent.getParcelableExtra("user")

        if (this.user == null) {
            this.user = Utils.user
            this.isOwner = true
        } else if (this.user!!.id == Utils.user?.id) {
            this.isOwner = true
        }

        // Answers

        Answers.getInstance().logContentView(ContentViewEvent()
            .putContentId(user!!.id)
            .putContentName(user!!.username)
            .putContentType("User"))

        // toolbar

        setSupportActionBar(toolbar_profile)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        // adapter

        this.adapter = EventRecyclerViewAdapter(this, Glide.with(this), false, R.layout.row_event_with_avatars)
        adapter!!.setOnItemClickListener { event -> startActivityForResult(Intent(baseContext, EventActivity::class.java).putExtra("event", event), EVENT_REQUEST) }

        // recycler view

        recyclerview_events_participate.setHasFixedSize(true)
        recyclerview_events_participate.isNestedScrollingEnabled = false
        recyclerview_events_participate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview_events_participate.adapter = adapter

        // fill the main layout

        val id = resources.getIdentifier(Utils.drawableProfileName(user!!.promotion, user!!.gender), "drawable", packageName)
        val drawable = ContextCompat.getDrawable(this@ProfileActivity, id)

        profile_avatar.setImageDrawable(drawable)
        profile_username.text = user!!.username
        profile_name.text = user!!.name
        profile_email.text = user!!.email
        profile_class.text = user!!.promotion
        profile_description.text = user!!.description

        if (user!!.name.isEmpty()) {
            profile_name.visibility = View.GONE
        }

        if (user!!.email.isEmpty()) {
            profile_email.visibility = View.GONE
        }

        if (user!!.promotion.isEmpty()) {
            profile_class.visibility = View.GONE
        }

        // links

        Linkify.addLinks(profile_email, Linkify.EMAIL_ADDRESSES)
        profile_email.setLinkTextColor(Color.parseColor("#ffffff"))

        // data generation

        generateBarcode()
        generateEvents()
    }

    private fun generateBarcode() {
        if (isOwner) {
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
            val barcodeData = defaultSharedPreferences.getString("barcode", "")

            if (barcodeData != "") {
                barcode_value.text = barcodeData

                val bitmap: Bitmap?

                try {
                    bitmap = encodeAsBitmap(barcodeData, BarcodeFormat.CODE_128, 700, 300)
                    barcode_image.setImageBitmap(bitmap)
                } catch (ex: WriterException) {
                    ex.printStackTrace()
                }

            } else {
                barcode_amicaliste_title.visibility = View.GONE
                barcode.visibility = View.GONE
            }
        } else {
            barcode_amicaliste_title.visibility = View.GONE
            barcode.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)

        if (isOwner) {
            menu.getItem(0).setTitle(R.string.delete_account)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_report -> {
                val alertDialogBuilder = AlertDialog.Builder(this@ProfileActivity)

                if (!isOwner) {
                    alertDialogBuilder.setTitle(getString(R.string.report_user_action))
                    alertDialogBuilder
                            .setMessage(R.string.report_user_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                                val call = ServiceGenerator.create().reportUser(user!!.id)
                                call.enqueue(object : Callback<User> {
                                    override fun onResponse(call: Call<User>, response: Response<User>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(this@ProfileActivity, getString(R.string.report_user_success), Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<User>, t: Throwable) {
                                        Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                            .setNegativeButton(getString(R.string.negative_button)) { dialogAlert, _ -> dialogAlert.cancel() }
                } else {
                    alertDialogBuilder.setTitle(getString(R.string.delete_account_action))
                    alertDialogBuilder
                            .setMessage(R.string.delete_account_are_you_sure)
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                                val call = ServiceGenerator.create().deleteUser(Utils.user?.id)
                                call.enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            val activity = Intent(this@ProfileActivity, IntroActivity::class.java)
                                            activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(activity)

                                            Toast.makeText(this@ProfileActivity, R.string.delete_account_success, Toast.LENGTH_SHORT).show()
                                            finish()
                                        } else {
                                            Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                            .setNegativeButton(getString(R.string.negative_button)) { dialogAlert, _ -> dialogAlert.cancel() }
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }

            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun clearEvents() {
        adapter!!.events.clear()
        adapter!!.notifyDataSetChanged()
    }

    private fun generateEvents() {
        clearEvents()

        if (user!!.events != null) {
            for (eventId in user!!.events) {
                val call = ServiceGenerator.create().getEventFromId(eventId)
                call.enqueue(object : Callback<Event> {
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        if (response.isSuccessful) {
                            addEventToAdapter(response.body()!!)
                        } else {
                            Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "ProfileActivity", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private fun addEventToAdapter(event: Event) {
        val atm = Calendar.getInstance().time

        if (event.dateEnd.time > atm.time) {
            adapter!!.addItem(event)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == EVENT_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val event = intent.getParcelableExtra<Event>("event")

                    for (i in 0 until adapter!!.itemCount) {
                        if (adapter!!.events[i].id == event.id) {
                            val user = Utils.user

                            if (user?.events != null) {
                                for (eventId in user.events) {
                                    if (eventId == event.id) {
                                        adapter!!.updateEvent(i, event)
                                        break
                                    }
                                }
                            }

                            adapter!!.removeItem(event.id)
                        }
                    }
                }

                Activity.RESULT_CANCELED -> {
                }
                else -> {
                }
            }
        }
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(content: String?, format: BarcodeFormat, img_width: Int, img_height: Int): Bitmap? {
        if (content == null) {
            return null
        }

        var hints: MutableMap<EncodeHintType, Any>? = null
        val encoding = guessAppropriateEncoding(content)

        if (encoding != null) {
            hints = EnumMap(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = encoding
        }

        val writer = MultiFormatWriter()
        val result: BitMatrix

        try {
            result = writer.encode(content, format, img_width, img_height, hints)
        } catch (ex: IllegalArgumentException) {
            return null
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    }

    companion object {

        const val EVENT_REQUEST = 2

        /*
        * http://code.google.com/p/zxing/
        * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
        * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
        */

        const val WHITE = 0x00FFFFFF
        const val BLACK = -0x1000000

        private fun guessAppropriateEncoding(contents: CharSequence): String? {
            for (i in 0 until contents.length) {
                if (contents[i].toInt() > 0xFF) {
                    return "UTF-8"
                }
            }

            return null
        }
    }
}
