package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by thomas on 18/07/2017.
 * Kotlin rewrite on 30/08/2019.
 */

@Parcelize
data class NotificationUser(
    @SerializedName("ID") val id: String?,
    @SerializedName("userid") val userId: String,
    @SerializedName("token") val token: String,
    @SerializedName("os") val os: String
): Parcelable
