package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Antoine on 19/09/2016.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class User(
    @SerializedName("ID") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("description") val description: String,
    @SerializedName("email") val email: String,
    @SerializedName("emailpublic") val isEmailPublic: Boolean,
    @SerializedName("promotion") val promotion: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("events") val events: MutableList<String>?,
    @SerializedName("postsliked") val postsLiked: MutableList<String>?
): Parcelable