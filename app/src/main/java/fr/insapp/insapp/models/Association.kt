package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class Association (
    @SerializedName("ID") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("description") val description: String,
    @SerializedName("events") val events: MutableList<String>,
    @SerializedName("posts") val posts: MutableList<String>,
    @SerializedName("profile") val profilePicture: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("bgcolor") val bgColor: String,
    @SerializedName("fgcolor") val fgColor: String
): Parcelable