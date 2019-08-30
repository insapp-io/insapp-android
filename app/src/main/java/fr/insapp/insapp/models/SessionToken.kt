package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by thomas on 11/07/2017.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class SessionToken(
    @SerializedName("ExpireAt") var expireAt: Date?,
    @SerializedName("Token") var token: String?,
    @SerializedName("Id") var id: String?
): Parcelable
