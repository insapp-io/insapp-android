package fr.insapp.insapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.AssociationRecyclerViewAdapter
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Association
import kotlinx.android.synthetic.main.fragment_clubs.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 27/10/2016.
 */

class ClubsFragment : Fragment() {

    private lateinit var adapter: AssociationRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.adapter = AssociationRecyclerViewAdapter(mutableListOf(), Glide.with(this), true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        generateClubs()
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
        no_network?.visibility = View.GONE
        val call = ServiceGenerator.create().clubs
        call.enqueue(object : Callback<List<Association>> {
            override fun onResponse(call: Call<List<Association>>, response: Response<List<Association>>) {
                if (response.isSuccessful) {
                    val clubs = response.body()

                    if (clubs != null) {
                        for (club in clubs) {
                            if (club.profilePicture.isNotEmpty() && club.cover.isNotEmpty()) {
                                adapter.addItem(club)
                            }
                        }
                    }
                } else {
                    if (adapter.associations.isEmpty()) {
                        no_network?.visibility = View.VISIBLE
                    } else if (recyclerview_clubs != null){
                        Snackbar.make(recyclerview_clubs, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Association>>, t: Throwable) {
                if (adapter.associations.isEmpty()) {
                    no_network?.visibility = View.VISIBLE
                } else if (recyclerview_clubs != null){
                    Snackbar.make(recyclerview_clubs, R.string.connectivity_issue, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}