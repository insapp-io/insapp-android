package fr.insapp.insapp.http.retrofit;

import java.util.List;

import fr.insapp.insapp.MainActivity;
import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.PostInteraction;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.models.credentials.LoginCredentials;
import fr.insapp.insapp.models.credentials.SessionCredentials;
import fr.insapp.insapp.models.credentials.SigninCredentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("association/{id}")
    Call<Club> getClubFromId(@Path("id") String id);

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
}