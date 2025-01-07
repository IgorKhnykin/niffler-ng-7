package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code(), "Не удалось создать трату");
        return response.body();
    }

    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(response.isSuccessful(), "Не удалось изменить трату");
        return response.body();
    }

    public SpendJson getSpend(List<String> ids) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить трату");
        return response.body();
    }

    public List<SpendJson> getSpends() {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getSpends()
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить траты");
        return response.body();
    }

    public Void removeSpend(List<String> spendIds) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpend(spendIds)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось удалить трату");
        return response.body();
    }

    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.createCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось создать категорию");
        return response.body();
    }

    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }

    public List<CategoryJson> getCategories() {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getCategories()
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить категории");
        return response.body();
    }
}
