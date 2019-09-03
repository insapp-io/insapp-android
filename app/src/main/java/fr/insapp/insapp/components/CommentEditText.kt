package fr.insapp.insapp.components

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import fr.insapp.insapp.R
import fr.insapp.insapp.adapters.AutoCompleterAdapter
import fr.insapp.insapp.models.Tag
import fr.insapp.insapp.utility.TagTokenizer
import java.util.*

/**
 * Created by thomas on 27/02/2017.
 * Kotlin rewrite on 03/09/2019.
 */

class CommentEditText : AppCompatMultiAutoCompleteTextView {

    private lateinit var adapter: AutoCompleterAdapter

    val tags = ArrayList<Tag>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setupComponent() {
        threshold = 1
        setTokenizer(TagTokenizer())

        this.adapter = AutoCompleterAdapter(context, R.id.comment_event_input)
        setAdapter<AutoCompleterAdapter>(adapter)

        onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val itemString = (view.findViewById<TextView>(R.id.dropdown_textview)).text.toString()

            var userId = ""
            for ((id, _, username1) in adapter.filteredUsers) {
                val username = "@$username1"
                if (username == itemString) {
                    userId = id

                    break
                }
            }

            tags.add(Tag(null, userId, itemString))
        }
    }
}
