package fr.insapp.insapp.http;

import java.util.List;

import fr.insapp.insapp.models.Association;
import fr.insapp.insapp.models.AssociationSearchResults;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.EventInteraction;
import fr.insapp.insapp.models.EventSearchResults;
import fr.insapp.insapp.models.Notification;
import fr.insapp.insapp.models.NotificationUser;
import fr.insapp.insapp.models.Notifications;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.models.PostSearchResults;
import fr.insapp.insapp.models.UniversalSearchResults;
import fr.insapp.insapp.models.UserSearchResults;
import fr.insapp.insapp.models.SearchTerms;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.models.credentials.SigninCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by thomas on 07/07/2017.
 */

public interface Client {

    /*
     * PUBLIC
     */

    @POST("signin/user/{ticket}")
    Call<LoginCredentials> signUser(@Path("ticket") String ticket, @Body SigninCredentials signinCredentials);

    @POST("login/user")
    Call<SessionCredentials> logUser(@Body LoginCredentials loginCredentials);

    /*
     * ASSOCIATIONS
     */

    @GET("associations")
    Call<List<Association>> getClubs();

    @GET("associations/{id}")
    Call<Association> getClubFromId(@Path("id") String id);

    /*
     * EVENTS
     */

    @GET("events")
    Call<List<Event>> getFutureEvents();

    @GET("associations/{associationId}/events")
    Call<List<Event>> getEventsForAssociation(@Path("associationId") String associationId);

    @GET("events/{id}")
    Call<Event> getEventFromId(@Path("id") String id);

    @POST("events/{id}/attend/{userId}/status/{status}")
    Call<EventInteraction> addAttendee(@Path("id") String id, @Path("userId") String userId, @Path("status") String status);

    @POST("events/{id}/comment")
    Call<Event> commentEvent(@Path("id") String id, @Body Comment comment);

    @DELETE("events/{id}/comment/{commentId}")
    Call<Event> uncommentEvent(@Path("id") String id, @Path("commentId") String commentId);

    /*
     * POSTS
     */

    @GET("posts")
    Call<List<Post>> getLatestPosts();

    @GET("associations/{associationId}/posts")
    Call<List<Post>> getPostsForAssociation(@Path("associationId") String id);

    @GET("posts/{id}")
    Call<Post> getPostFromId(@Path("id") String id);

    @POST("posts/{id}/like/{userId}")
    Call<PostInteraction> likePost(@Path("id") String id, @Path("userId") String userId);

    @DELETE("posts/{id}/like/{userId}")
    Call<PostInteraction> dislikePost(@Path("id") String id, @Path("userId") String userId);

    @POST("posts/{id}/comment")
    Call<Post> commentPost(@Path("id") String id, @Body Comment comment);

    @DELETE("posts/{id}/comment/{commentId}")
    Call<Post> uncommentPost(@Path("id") String id, @Path("commentId") String commentId);

    /*
     * COMMENTS
     */

    @PUT("report/{id}/comment/{commentId}")
    Call<Void> reportComment(@Path("id") String id, @Path("commentId") String commentId);

    /*
     * USERS
     */

    @GET("users/{id}")
    Call<User> getUserFromId(@Path("id") String id);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    @PUT("report/user/{id}")
    Call<User> reportUser(@Path("id") String id);

    /*
     * NOTIFICATION
     */

    @POST("notifications")
    Call<NotificationUser> registerNotification(@Body NotificationUser notificationUser);

    @GET("notifications/{userId}")
    Call<Notifications> getNotificationsForUser(@Path("userId") String userId);

    @DELETE("notifications/{userId}/{id}")
    Call<Notifications> markNotificationAsSeen(@Path("userId") String userId, @Path("id") String id);

    /*
     * SEARCH
     */

    @POST("search/users")
    Call<UserSearchResults> searchUsers(@Body SearchTerms terms);

    @POST("search/associations")
    Call<AssociationSearchResults> searchClubs(@Body SearchTerms terms);

    @POST("search/events")
    Call<EventSearchResults> searchEvents(@Body SearchTerms terms);

    @POST("search/posts")
    Call<PostSearchResults> searchPosts(@Body SearchTerms terms);

    @POST("search")
    Call<UniversalSearchResults> universalSearch(@Body SearchTerms terms);
}