package fr.insapp.insapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by thomas on 15/07/2017.
 * Kotlin rewrite on 29/08/2019.
 */

@Parcelize
data class AssociationSearchResults (
    @SerializedName("associations") val clubs: MutableList<Association>
): Parcelable