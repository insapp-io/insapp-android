package fr.insapp.insapp.models.credentials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import fr.insapp.insapp.models.SessionToken
import fr.insapp.insapp.models.User
import kotlinx.android.parcel.Parcelize

/**
 * Created by thomas on 11/07/2017.
 * Kotlin rewrite on 30/08/2019.
 */

@Parcelize
data class SessionCredentials(
        @SerializedName("credentials") var loginCredentials: LoginCredentials?,
        @SerializedName("sessionToken") var sessionToken: SessionToken?,
        @SerializedName("user") var user: User?
): Parcelable
