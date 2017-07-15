package fr.insapp.insapp.http;

import java.util.List;

import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Event;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.models.SearchResults;
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

    @GET("association")
    Call<List<Club>> getClubs();

    @GET("association/{id}")
    Call<Club> getClubFromId(@Path("id") String id);

    /*
     * EVENTS
     */

    @GET("event")
    Call<List<Event>> getFutureEvents();

    @GET("event/{id}")
    Call<Event> getEventFromId(@Path("id") String id);

    @POST("event/{id}/participant/{userId}/status/{status}")
    Call<Event> addParticipant(@Path("id") String id, @Path("userId") String userId, @Path("status") String status);

    /*
     * POSTS
     */

    @GET("post/{id}")
    Call<Post> getPostFromId(@Path("id") String id);

    @GET("post")
    Call<List<Post>> getLatestPosts();

    @POST("post/{id}/like/{userId}")
    Call<PostInteraction> likePost(@Path("id") String id, @Path("userId") String userId);

    @DELETE("post/{id}/like/{userId}")
    Call<PostInteraction> dislikePost(@Path("id") String id, @Path("userId") String userId);

    @POST("post/{id}/comment")
    Call<Post> commentPost(@Path("id") String id, @Body Comment comment);

    @DELETE("post/{id}/comment/{commentId}")
    Call<Post> uncommentPost(@Path("id") String id, @Path("commentId") String commentId);

    @PUT("report/{id}/comment/{commentId}")
    Call<Post> reportComment(@Path("id") String id, @Path("commentId") String commentId);

    /*
     * USER
     */

    @GET("user/{id}")
    Call<User> getUserFromId(@Path("id") String id);

    @DELETE("user/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    @PUT("report/user/{id}")
    Call<User> reportUser(@Path("id") String id);

    /*
     * SEARCH
     */

    @POST("search/users")
    Call<SearchResults> searchUsers(@Body SearchTerms terms);
}