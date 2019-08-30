package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Antoine on 12/10/2016.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class Tag(
    @SerializedName("ID") val id: String?,
    @SerializedName("user") val user: String,
    @SerializedName("name") val name: String
): Parcelable
