package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<JsonNode> getToken(
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId
    );

    @POST("/internal/register")
    Call<Void> register(@Body ObjectNode jsonObject);
}
