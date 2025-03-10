package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

  @POST("/internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);

  @PATCH("/internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spendJson);

  @GET("/internal/spends/{id}")
  Call<SpendJson> getSpend(@Path("id") String id, @Query("username") String username);

  @GET("/internal/spends/all")
  Call<List<SpendJson>> getSpends(@Query("username") String username);

  @DELETE("/internal/spends/remove")
  Call<Void> deleteSpends(@Query("username") String username, @Query("ids") List<String> ids);

  @POST("/internal/categories/add")
  Call<CategoryJson> createCategory(@Body CategoryJson categoryJson);

  @PATCH("/internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson categoryJson);

  @GET("/internal/categories/all")
  Call<List<CategoryJson>> getCategories(@Query("username") String username, @Query("excludeArchived") boolean excludeArchived);
}
