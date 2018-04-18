package fr.insapp.insapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.insapp.insapp.R
import fr.insapp.insapp.models.Event
import fr.insapp.insapp.utility.Utils
import kotlinx.android.synthetic.main.fragment_event_about.*

/**
 * Created by thomas on 25/02/2017.
 */

class AboutFragment : Fragment() {

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            this.event = bundle.getParcelable("event")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_about, container, false)

        event_description.text = event!!.description
        Linkify.addLinks(event_description, Linkify.ALL)
        Utils.convertToLinkSpan(context, event_description)

        return view
    }
}
