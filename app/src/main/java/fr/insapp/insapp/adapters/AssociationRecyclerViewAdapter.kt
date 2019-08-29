package fr.insapp.insapp.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.ClubActivity
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Association
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.association_thumb.view.*

/**
 * Created by thomas on 30/10/2016.
 * Kotlin rewrite on 28/08/2019.
 */

class AssociationRecyclerViewAdapter(
        var associations: MutableList<Association>,
        private val requestManager: RequestManager,
        private val matchParent: Boolean
) : RecyclerView.Adapter<AssociationRecyclerViewAdapter.AssociationViewHolder>() {

    fun addItem(club: Association) {
        this.associations.add(club)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AssociationViewHolder(parent.inflate(R.layout.association_thumb), requestManager, matchParent)

    override fun onBindViewHolder(holder: AssociationViewHolder, position: Int) {
        val association = associations[position]
        holder.bindAssociation(association)
    }

    override fun getItemCount() = associations.size

    class AssociationViewHolder(
            private val view: View,
            private val requestManager: RequestManager,
            matchParent: Boolean
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var association: Association? = null

        init {
            view.setOnClickListener(this)

            if (matchParent)
                view.club_thumb_layout.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        }

        fun bindAssociation(association: Association) {
            this.association = association

            view.association_name.text = association.name

            requestManager
                .load(ServiceGenerator.CDN_URL + association.profilePicture)
                .apply(RequestOptions.circleCropTransform())
                .transition(withCrossFade())
                .into(view.association_avatar)
        }

        override fun onClick(v: View) {
            val context = itemView.context
            context.startActivity(Intent(context, ClubActivity::class.java).putExtra("club", association))
        }
    }
}