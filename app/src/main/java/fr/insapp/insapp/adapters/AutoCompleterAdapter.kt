package fr.insapp.insapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import fr.insapp.insapp.R
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.SearchTerms
import fr.insapp.insapp.models.User
import fr.insapp.insapp.utility.GlideApp
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.dropdown.view.*
import java.util.*
import kotlin.math.min

/**
 * Created by thomas on 27/02/2017.
 * Kotlin rewrite on 28/08/2019.
 */

class AutoCompleterAdapter(
        context: Context,
        resource: Int
) : ArrayAdapter<User>(context, resource), Filterable {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var filteredUsers: List<User> = ArrayList()

    override fun getCount() = filteredUsers.size

    override fun getItem(index: Int) = filteredUsers[index]

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.dropdown, parent, false)

        val user = getItem(position)
        val resources = context.resources

        view.dropdown_textview.text = String.format(resources.getString(R.string.tag), user.username)

        // get the drawable of avatar

        val id = resources.getIdentifier(Utils.drawableProfileName(user.promotion, user.gender), "drawable", context.packageName)
        GlideApp
                .with(context)
                .load(id)
                .into(view.dropdown_avatar)

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                // this method is called async

                val filterResults = FilterResults()

                if (constraint != null) {
                    val call = ServiceGenerator.client.searchUsers(SearchTerms(constraint.toString()))
                    val results = call.execute().body()
                    if (results != null) {
                        val users = results.users
                        val filteredUsers = ArrayList<User>()

                        for (i in 0 until min(users.size, 10)) {
                            filteredUsers.add(users[i])
                        }

                        filterResults.values = filteredUsers
                        filterResults.count = filteredUsers.size

                    }
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    filteredUsers = results.values as List<User>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(result: Any): CharSequence {
                return (result as User).username
            }
        }
    }
}