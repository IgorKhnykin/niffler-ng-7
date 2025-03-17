package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    @FormUrlEncoded
    @POST("/register")
    Call<Void> register(@Field("username") String username,
                        @Field("password") String password,
                        @Field("passwordSubmit") String passwordSubmit,
                        @Field("_csrf") String _csrf);

    @GET("/register")
    Call<Void> requestRegisterForm();

    @GET("/oauth2/authorize")
    Call<Void> authorize(
            @Query(value = "response_type") String responseType,
            @Query(value = "client_id") String clientId,
            @Query(value = "scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query(value = "code_challenge") String codeChallenge,
            @Query(value = "code_challenge_method") String codeChallengeMethod);

    @FormUrlEncoded
    @POST("/login")
    Call<Void> login(@Field("username") String username,
                     @Field("password") String password,
                     @Field("_csrf") String _csrf);

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<JsonNode> getToken(
            @Field("code") String code,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId
    );
}
