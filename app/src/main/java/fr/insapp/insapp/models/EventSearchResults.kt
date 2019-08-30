package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by thomas on 15/07/2017.
 * Kotlin rewrite on 30/08/2019.
 */

@Parcelize
data class EventSearchResults(
    @SerializedName("events") val events: MutableList<Event>
): Parcelable
