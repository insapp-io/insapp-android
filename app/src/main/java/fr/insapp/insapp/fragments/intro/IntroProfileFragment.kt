package fr.insapp.insapp.fragments.intro

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.insapp.insapp.R

/**
 * Created by thomas on 03/12/2016.
 */

class IntroProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_profile, container, false)
    }
}