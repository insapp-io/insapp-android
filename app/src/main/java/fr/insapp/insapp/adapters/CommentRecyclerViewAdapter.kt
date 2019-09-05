package fr.insapp.insapp.adapters

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import fr.insapp.insapp.R
import fr.insapp.insapp.activities.ProfileActivity
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.Comment
import fr.insapp.insapp.models.User
import fr.insapp.insapp.utility.Utils
import fr.insapp.insapp.utility.inflate
import kotlinx.android.synthetic.main.row_comment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 18/11/2016.
 * Kotlin rewrite on 28/08/2019.
 */

class CommentRecyclerViewAdapter(
        private var comments: MutableList<Comment>,
        private val requestManager: RequestManager
) : RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>() {

    private var onLongClickListener: OnCommentItemLongClickListener? = null

    interface OnCommentItemLongClickListener {
        fun onCommentItemLongClick(comment: Comment)
    }

    fun addComment(comment: Comment) {
        comments.add(comment)
        notifyDataSetChanged()
    }

    fun setComments(comments: MutableList<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    fun setOnCommentItemLongClickListener(onLongClickListener: OnCommentItemLongClickListener) {
        this.onLongClickListener = onLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(parent.inflate(R.layout.row_comment), requestManager, onLongClickListener)

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bindComment(comment)
    }

    override fun getItemCount() = comments.size

    class CommentViewHolder(
            private val view: View,
            private val requestManager: RequestManager,
            private val onLongClickListener: OnCommentItemLongClickListener?
    ) : RecyclerView.ViewHolder(view), View.OnLongClickListener {

        private var comment: Comment? = null

        fun bindComment(comment: Comment) {
            this.comment = comment

            val context = itemView.context

            // tagging

            val str = comment.content
            val spannableString = SpannableString(str)

            for (tag in comment.tags) {
                var posStart = str.indexOf(tag.name)

                if (posStart < 0) {
                    posStart = 0
                }

                val posEnd = posStart + tag.name.length

                val span = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        val call = ServiceGenerator.client.getUserFromId(tag.user)
                        call.enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                val user = response.body()
                                if (response.isSuccessful && user != null) {
                                    context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("user", user))
                                } else {
                                    Toast.makeText(context, "PostCommentRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                Toast.makeText(context, "PostCommentRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                            }
                        })
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }

                spannableString.setSpan(span, posStart, posEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            view.comment_text.text = spannableString
            view.comment_text.movementMethod = LinkMovementMethod.getInstance()
            view.comment_text.isEnabled = true

            if (comment.date != null) {
                view.comment_date.text = Utils.displayedDate(comment.date)
            }

            // user

            val call = ServiceGenerator.client.getUserFromId(comment.user)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val user = response.body()
                    if (response.isSuccessful && user != null) {
                        val id = context.resources.getIdentifier(Utils.drawableProfileName(user.promotion, user.gender), "drawable", context.packageName)
                        requestManager
                            .load(id)
                            .transition(withCrossFade())
                            .apply(RequestOptions.circleCropTransform())
                            .into(view.comment_avatar)

                        view.comment_username.text = String.format(context.resources.getString(R.string.tag), user.username)

                        val listener = View.OnClickListener { context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("user", user)) }

                        view.comment_avatar.setOnClickListener(listener)
                        view.comment_username.setOnClickListener(listener)
                    } else {
                        Toast.makeText(context, "PostCommentRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, "PostCommentRecyclerViewAdapter", Toast.LENGTH_LONG).show()
                }
            })

            view.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View): Boolean {
            if (comment != null) {
                onLongClickListener?.onCommentItemLongClick(comment!!)
            }
            return true
        }
    }
}
