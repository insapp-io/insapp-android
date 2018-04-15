package fr.insapp.insapp.notifications

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import fr.insapp.insapp.App
import fr.insapp.insapp.http.ServiceGenerator
import fr.insapp.insapp.models.NotificationUser
import fr.insapp.insapp.utility.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by thomas on 18/07/2017.
 */

class FirebaseService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        val firebaseCredentialsPreferences = App.getAppContext().getSharedPreferences("FirebaseCredentials", Context.MODE_PRIVATE)
        firebaseCredentialsPreferences.edit().putString("token", refreshedToken).apply()

        Log.d(FirebaseService.TAG, "Refreshed token: " + refreshedToken!!)

        FirebaseService.SHOULD_REGISTER_TOKEN = true
    }

    companion object {

        const val TAG = "FA"

        var SHOULD_REGISTER_TOKEN = false

        fun registerToken(token: String) {
            val user = Utils.getUser()

            val notificationUser = NotificationUser(null, user.id, token, "android")

            val call = ServiceGenerator.create().registerNotification(notificationUser)
            call.enqueue(object : Callback<NotificationUser> {
                override fun onResponse(call: Call<NotificationUser>, response: Response<NotificationUser>) {
                    if (response.isSuccessful) {
                        Log.d(FirebaseService.TAG, "Firebase token successfully registered on server: $token")
                    } else {
                        Toast.makeText(App.getAppContext(), "FirebaseService", Toast.LENGTH_LONG).show()

                        FirebaseService.SHOULD_REGISTER_TOKEN = true
                    }
                }

                override fun onFailure(call: Call<NotificationUser>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseService", Toast.LENGTH_LONG).show()

                    FirebaseService.SHOULD_REGISTER_TOKEN = true
                }
            })
        }
    }
}