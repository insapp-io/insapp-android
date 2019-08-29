package fr.insapp.insapp.models;

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class Event (
    @SerializedName("ID") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("association") val association: String,
    @SerializedName("description") val description: String,
    @SerializedName("participants") val attendees: MutableList<String>?,
    @SerializedName("maybe") val maybe: MutableList<String>?,
    @SerializedName("notgoing") val notgoing: MutableList<String>?,
    @SerializedName("comments") val comments: MutableList<Comment>,
    @SerializedName("status") val status: String,
    @SerializedName("dateStart") val dateStart: Date,
    @SerializedName("dateEnd") val dateEnd: Date,
    @SerializedName("image") val image: String,
    @SerializedName("promotions") val promotions: MutableList<String>?,
    @SerializedName("plateforms") val plateforms: MutableList<String>?,
    @SerializedName("bgColor") val bgColor: String,
    @SerializedName("fgColor") val fgColor: String
): Parcelable

enum class AttendanceStatus {
    YES,
    MAYBE,
    NO,
    UNDEFINED
}