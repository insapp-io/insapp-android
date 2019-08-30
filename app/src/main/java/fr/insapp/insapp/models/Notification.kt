package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Antoine on 10/10/2016.
 * Kotlin rewrite on 30/08/2019.
 */

@Parcelize
data class Notification(
        @SerializedName("ID") val id: String?,
        @SerializedName("sender") val sender: String,
        @SerializedName("receiver") val receiver: String,
        @SerializedName("content") val content: String,
        @SerializedName("comment") val comment: Comment?,
        @SerializedName("message") val message: String,
        @SerializedName("seen") val seen: Boolean,
        @SerializedName("date") val date: Date,
        @SerializedName("type") val type: String
): Parcelable

