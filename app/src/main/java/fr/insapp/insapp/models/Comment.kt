package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by thomas on 17/11/16.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class Comment(
    @SerializedName("ID") val id: String?,
    @SerializedName("user") val user: String,
    @SerializedName("content") val content: String,
    @SerializedName("date") val date: Date?,
    @SerializedName("tags") val tags: List<Tag>
): Parcelable
