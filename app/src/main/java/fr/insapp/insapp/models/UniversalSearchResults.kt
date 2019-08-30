package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
/**
 * Created by thomas on 15/07/2017.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class UniversalSearchResults(
    @SerializedName("associations") val associations: MutableList<Association>?,
    @SerializedName("posts") val posts: MutableList<Post>?,
    @SerializedName("events") val events: MutableList<Event>?,
    @SerializedName("users") val users: MutableList<User>?
): Parcelable
