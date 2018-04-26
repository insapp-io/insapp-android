package fr.insapp.insapp.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import fr.insapp.insapp.App
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.ClubActivity
import fr.insapp.insapp.adapters.ClubRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Club
import kotlinx.android.synthetic.main.fragment_clubs.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 27/10/2016.
 */

class ClubsFragment : Fragment() {

    private var adapter: ClubRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.adapter = ClubRecyclerViewAdapter(context, Glide.with(this), true)
        adapter!!.setOnItemClickListener { club -> context!!.startActivity(Intent(context, ClubActivity::class.java).putExtra("club", club)) }

        generateClubs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_clubs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_clubs.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(context, 3)
        recyclerview_clubs.layoutManager = layoutManager
        recyclerview_clubs.adapter = adapter
    }

    private fun generateClubs() {
        val call = ServiceGenerator.create().clubs
        call.enqueue(object : Callback<List<Club>> {
            override fun onResponse(call: Call<List<Club>>, response: Response<List<Club>>) {
                if (response.isSuccessful) {
                    val clubs = response.body()

                    for (club in clubs!!) {
                        if (!club.profilePicture.isEmpty() && !club.cover.isEmpty()) {
                            adapter!!.addItem(club)
                        }
                    }
                } else {
                    Toast.makeText(App.getAppContext(), "ClubsFragment", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Club>>, t: Throwable) {
                Toast.makeText(App.getAppContext(), "ClubsFragment", Toast.LENGTH_LONG).show()
            }
        })
    }
}