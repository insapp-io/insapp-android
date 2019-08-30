package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by thomas on 19/07/2017.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class EventInteraction(
    @SerializedName("event") val event: Event,
    @SerializedName("user") val user: User
): Parcelable
