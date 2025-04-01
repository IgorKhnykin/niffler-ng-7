package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendApi spendApi = new RestClient.EmptyRestClient(CFG.spendUrl()).create(SpendApi.class);

    @Override
    @Step("Создание траты через API")
    public @Nullable SpendJson createSpend(SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code(), "Не удалось создать трату");
        return response.body();
    }

    @Override
    @Step("Найти трату через API")
    public @Nullable SpendJson findSpend(SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(String.valueOf(spendJson.id()), spendJson.username()).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось найти трату");
        return response.body();
    }

    @Override
    @Step("Найти траты по имени {username} и описанию {description} через API")
    public @Nonnull List<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getSpends(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить траты");
        return response.body() == null ? Collections.emptyList() : response.body().stream()
                .filter(spend -> spend.description().contains(description))
                .toList();
    }

    @Override
    @Step("Удалить трату через API")
    public void deleteSpend(SpendJson spendJson) {
        final Response<Void> response;
        try {
            response = spendApi.deleteSpends(spendJson.username(), List.of(String.valueOf(spendJson.id())))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(202, response.code(), "Не удалось удалить трату");
    }

    @Override
    @Step("Создание категории через API")
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.createCategory(categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось создать категорию");

        CategoryJson result = Objects.requireNonNull(response.body());

        CategoryJson createdCategory = new CategoryJson(result.id(),
                result.name(),
                result.username(),
                true);

        return categoryJson.archived() ? updateCategory(createdCategory) : response.body();
    }

    @Override
    @Step("Поиск категории по имени пользователя {username} и названию {categoryName} категории через API")
    public @Nullable CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getCategories(username, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить категории");
        return response.body() == null ? null : response.body().stream()
                .filter(category -> category.name().contains(categoryName))
                .findFirst().orElseGet(null);
    }

    @Override
    @Step("Удаление категории через API")
    public void deleteCategory(CategoryJson categoryJson) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Обновить данные траты через API")
    public @Nullable SpendJson updateSpend(SpendJson spendJson) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось изменить трату");
        return response.body();
    }

    @Override
    @Step("Обновить данные категории через API")
    public @Nullable CategoryJson updateCategory(CategoryJson categoryJson) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }

    @Override
    @Step("Получить все траты по имени пользователя")
    public @Nullable List<SpendJson> getAllSpendsByUsername(String username) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getSpends(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }

    @Override
    @Step("Получить все траты по имени пользователя")
    public @Nullable List<CategoryJson> getAllActiveCategoriesByUsername(String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getCategories(username, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }
}
