package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

    @POST( "internal/spends/add")
    Call<SpendJson> createSpend(@Body SpendJson spendJson);

    @PATCH("internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spendJson);

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpend(@Query("ids") List<String> ids);

    @GET("internal/spends/all")
    Call<List<SpendJson>> getSpends();

    @DELETE("internal/spends/remove")
    Call<Void> removeSpend(@Body List<String> ids);

    @POST("internal/categories/add")
    Call<CategoryJson> createCategory(@Body CategoryJson categoryJson);

    @PATCH("internal/categories/update")
    Call<CategoryJson> updateCategory(@Body CategoryJson categoryJson);

    @GET("internal/categories/all")
    Call<List<CategoryJson>> getCategories();
}
