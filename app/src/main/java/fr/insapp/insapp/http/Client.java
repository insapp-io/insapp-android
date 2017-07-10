package fr.insapp.insapp.http;

import java.util.List;

import fr.insapp.insapp.models.Club;
import fr.insapp.insapp.models.Comment;
import fr.insapp.insapp.models.Post;
import fr.insapp.insapp.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by thomas on 07/07/2017.
 */

public interface Client {

    String ROOT_URL = "https://dev.insapp.fr/api/v1/";

    /*
     * PUBLIC
     */
    @POST("signin/user/{ticket}")
    Call<?> signUser(@Path("ticket") String ticket);

    /*
     * ASSOCIATIONS
     */

    @GET("association/{id}")
    Call<Club> getClubFromId(@Path("id") String id, @Query("token") String token);

    /*
     * POSTS
     */

    @GET("post/{id}")
    Call<Post> getPostFromId(@Path("id") String id, @Query("token") String token);

    @GET("post")
    Call<List<Post>> getLatestPosts(@Query("token") String token);

    @POST("post/{id}/comment")
    Call<Post> commentPost(@Path("id") String id, @Body Comment comment, @Query("token") String token);

    /*
     * USER
     */
    @GET("user/{id}")
    Call<User> getUser(@Path("id") String id, @Query("token") String token);
}