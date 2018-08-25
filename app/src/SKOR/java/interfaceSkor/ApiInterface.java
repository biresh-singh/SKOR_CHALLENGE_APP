package interfaceSkor;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import model.GetChallengesResponse;
import model.GetParticipantsResponse;
import model.MyChallengesResponse;
import model.UserActivityResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public interface ApiInterface {


    @GET("api/user_challenges/")
        Call<GetChallengesResponse> GetChallenges(@Header("User-Agent") String userAgent, @Header("Authorization") String userToken);

    @GET("api/participants/")
    Call<GetParticipantsResponse> GetParticipants(
            @Query("challenge_id") int challenge_id,
            @Header("User-Agent") String userAgent, @Header("Authorization") String userToken);


    @POST("api/user_activity/")
    Call<UserActivityResponse> ProceedChallenge(@Body JsonObject jsonBody,
                                                @Header("User-Agent") String userAgent,
                                                @Header("Authorization") String userToken,
                                                @Header("Content-Type") String contentType);

    @GET("api/user_activity/")
    Call<MyChallengesResponse> GetMyChallenges(@Header("User-Agent") String userAgent, @Header("Authorization") String userToken);

    @GET("api/invitations/")
    Call<GetParticipantsResponse> Getinvitations(@Query("challenge_id") int challenge_id,@Header("User-Agent") String userAgent, @Header("Authorization") String userToken);


}
