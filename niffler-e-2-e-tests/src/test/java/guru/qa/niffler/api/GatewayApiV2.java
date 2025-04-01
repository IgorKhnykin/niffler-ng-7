package guru.qa.niffler.api;


import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.DataFilterValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GatewayApiV2 {

    @GET("/api/v2/spends/all")
    Call<RestResponsePage<SpendJson>> getSpends(@Header("Authorization") String token,
                                                @Query("page") int page,
                                                @Query("sort") @Nullable String sort,
                                                @Query("filterPeriod") @Nullable DataFilterValues filterPeriod,
                                                @Query("filterCurrency") @Nullable CurrencyValues filterCurrency);

    @GET("/api/v2/users/all")
    Call<RestResponsePage<UserJson>> getAllUsers(@Header("Authorization") String token,
                                     @Query("page") int page,
                                     @Query("sort") @Nullable String sort,
                                     @Query("searchQuery") @Nullable String searchQuery);

    @GET("/api/v2/friends/all")
    Call<RestResponsePage<UserJson>> getAllFriends(@Header("Authorization") String token,
                                       @Query("page") int page,
                                       @Query("sort") @Nullable String sort,
                                       @Query("searchQuery") @Nullable String searchQuery);
}
