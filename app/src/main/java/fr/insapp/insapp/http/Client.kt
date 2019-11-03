package fr.insapp.insapp.http

import fr.insapp.insapp.models.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by thomas on 07/07/2017.
 * Kotlin rewrite on 30/08/2019.
 */

interface Client {

    /*
     * PUBLIC
     */

    @POST("login/user/{ticket}")
    fun logUser(@Path("ticket") ticket: String): Call<User>

    /*
     * ASSOCIATIONS
     */

    @get:GET("associations")
    val associations: Call<List<Association>>

    @GET("associations/{id}")
    fun getAssociationFromId(@Path("id") id: String): Call<Association>

    @GET("associations/{associationId}/posts")
    fun getPostsForAssociation(@Path("associationId") id: String): Call<List<Post>>

    @GET("associations/{associationId}/events")
    fun getEventsForAssociation(@Path("associationId") associationId: String): Call<List<Event>>

    /*
 * POSTS
 */

    @get:GET("posts")
    val latestPosts: Call<List<Post>>

    @GET("posts/{id}")
    fun getPostFromId(@Path("id") id: String): Call<Post>

    @POST("posts/{id}/like/{userId}")
    fun likePost(@Path("id") id: String, @Path("userId") userId: String): Call<PostInteraction>

    @DELETE("posts/{id}/like/{userId}")
    fun dislikePost(@Path("id") id: String, @Path("userId") userId: String): Call<PostInteraction>

    @POST("posts/{id}/comment")
    fun commentPost(@Path("id") id: String, @Body comment: Comment): Call<Post>

    @DELETE("posts/{id}/comment/{commentId}")
    fun uncommentPost(@Path("id") id: String, @Path("commentId") commentId: String): Call<Post>

    /*
     * EVENTS
     */

    @get:GET("events")
    val futureEvents: Call<List<Event>>

    @GET("events/{id}")
    fun getEventFromId(@Path("id") id: String): Call<Event>

    @POST("events/{id}/attend/{userId}/status/{status}")
    fun addAttendee(@Path("id") id: String, @Path("userId") userId: String, @Path("status") status: String): Call<EventInteraction>

    @POST("events/{id}/comment")
    fun commentEvent(@Path("id") id: String, @Body comment: Comment): Call<Event>

    @DELETE("events/{id}/comment/{commentId}")
    fun uncommentEvent(@Path("id") id: String, @Path("commentId") commentId: String): Call<Event>

    /*
     * COMMENTS
     */

    @PUT("report/{id}/comment/{commentId}")
    fun reportComment(@Path("id") id: String, @Path("commentId") commentId: String): Call<Void>

    /*
     * USERS
     */

    @GET("users/{id}")
    fun getUserFromId(@Path("id") id: String): Call<User>

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: String, @Body user: User): Call<User>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: String): Call<Void>

    @PUT("report/user/{id}")
    fun reportUser(@Path("id") id: String): Call<User>

    /*
     * NOTIFICATION
     */

    @POST("notifications")
    fun registerNotification(@Body notificationUser: NotificationUser): Call<NotificationUser>

    @GET("notifications/{userId}")
    fun getNotificationsForUser(@Path("userId") userId: String): Call<Notifications>

    @DELETE("notifications/{userId}/{id}")
    fun markNotificationAsSeen(@Path("userId") userId: String, @Path("id") id: String): Call<Notifications>

    /*
     * SEARCH
     */

    @POST("search/users")
    fun searchUsers(@Body terms: SearchTerms): Call<UserSearchResults>

    @POST("search/associations")
    fun searchClubs(@Body terms: SearchTerms): Call<AssociationSearchResults>

    @POST("search/events")
    fun searchEvents(@Body terms: SearchTerms): Call<EventSearchResults>

    @POST("search/posts")
    fun searchPosts(@Body terms: SearchTerms): Call<PostSearchResults>

    @POST("search")
    fun universalSearch(@Body terms: SearchTerms): Call<UniversalSearchResults>
}