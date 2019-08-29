package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by thomas on 29/10/2016.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class Post (
        @SerializedName("ID") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("association") val association: String,
        @SerializedName("description") val description: String,
        @SerializedName("date") val date: Date,
        @SerializedName("likes") val likes: MutableList<String>,
        @SerializedName("comments") val comments: MutableList<Comment>,
        @SerializedName("promotions") val promotions: MutableList<String>,
        @SerializedName("plateforms") val plateforms: MutableList<String>,
        @SerializedName("image") val image: String,
        @SerializedName("imageSize") val imageSize: ImageSize,
        @SerializedName("nonotification") val noNotification: Boolean
): Parcelable
