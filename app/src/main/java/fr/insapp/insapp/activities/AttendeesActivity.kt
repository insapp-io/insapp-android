package fr.insapp.insapp.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.AttendeeRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.models.User
import kotlinx.android.synthetic.main.activity_attendees.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 10/12/2016.
 */

class AttendeesActivity : AppCompatActivity() {

    private lateinit var adapter: AttendeeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendees)

        val attendees = intent.getStringArrayListExtra("attendees")
        if (attendees != null) {
            generateUsers(attendees, Event.ATTENDANCE_STATUS.YES)
        }

        val maybe = intent.getStringArrayListExtra("maybe")
        if (maybe != null) {
            generateUsers(maybe, Event.ATTENDANCE_STATUS.MAYBE)
        }

        this.adapter = AttendeeRecyclerViewAdapter(mutableMapOf(), Glide.with(this), true)

        // toolbar

        setSupportActionBar(toolbar_users)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // recycler view

        recyclerview_users.setHasFixedSize(true)
        recyclerview_users.isNestedScrollingEnabled = false

        val layoutManager = GridLayoutManager(this, 3)
        recyclerview_users.layoutManager = layoutManager
        recyclerview_users.adapter = adapter
    }

    private fun generateUsers(users: List<String>, action: Event.ATTENDANCE_STATUS) {
        for (i in users.indices) {
            val call = ServiceGenerator.create().getUserFromId(users[i])
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.addItem(response.body() as User, action)
                    } else {
                        Toast.makeText(this@AttendeesActivity, "AttendeesActivity", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@AttendeesActivity, "AttendeesActivity", Toast.LENGTH_LONG).show()
                }
            })
        }
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
}
