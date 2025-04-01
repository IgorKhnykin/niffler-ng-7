package guru.qa.niffler.api;


import guru.qa.niffler.model.rest.*;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface GatewayApi {

    @POST("/api/spends/add")
    Call<SpendJson> addSpend(@Header("Authorization") String token,
                             @Body SpendJson spend);

    @PATCH("/api/spends/edit")
    Call<SpendJson> editSpend(@Header("Authorization") String token,
                              @Body SpendJson spendJson);

    @GET("/api/spends/{id}")
    Call<SpendJson> getSpend(@Header("Authorization") String token,
                             @Path("id") String id);

    @GET("/api/spends/all")
    Call<List<SpendJson>> getSpends(@Header("Authorization") String token,
                                    @Query("filterPeriod") @Nullable DataFilterValues filterPeriod,
                                    @Query("filterCurrency") @Nullable CurrencyValues filterCurrency);

    @DELETE("/api/spends/remove")
    Call<Void> deleteSpends(@Header("Authorization") String token,
                            @Query("ids") List<String> ids);

    @POST("/api/categories/add")
    Call<CategoryJson> createCategory(@Header("Authorization") String token,
                                      @Body CategoryJson categoryJson);

    @PATCH("/api/categories/update")
    Call<CategoryJson> updateCategory(@Header("Authorization") String token,
                                      @Body CategoryJson categoryJson);

    @GET("/api/categories/all")
    Call<List<CategoryJson>> getCategories(@Header("Authorization") String token,
                                           @Query("excludeArchived") boolean excludeArchived);

    @GET("/api/users/current")
    Call<UserJson> getCurrentUser(@Header("Authorization") String token);

    @GET("/api/users/all")
    Call<List<UserJson>> getAllUsers(@Header("Authorization") String token,
                                     @Query("searchQuery") @Nullable String searchQuery);

    @POST("/api/users/update")
    Call<UserJson> updateUser(@Header("Authorization") String token,
                              @Body UserJson user);

    @POST("/api/invitations/send")
    Call<UserJson> sendInvitation(@Header("Authorization") String token,
                                  @Body UserJson username);

    @POST("/api/invitations/accept")
    Call<UserJson> acceptInvitation(@Header("Authorization") String token,
                                    @Body UserJson username);

    @POST("/api/invitations/decline")
    Call<UserJson> declineInvitation(@Header("Authorization") String token,
                                     @Body UserJson username);

    @GET("/api/friends/all")
    Call<List<UserJson>> getAllFriends(@Header("Authorization") String token,
                                       @Query("searchQuery") @Nullable String searchQuery);

    @DELETE("/api/friends/remove")
    Call<Void> removeFriend(@Header("Authorization") String token,
                            @Query("username") String targetUsername);
}
