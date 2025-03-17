package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient {

    private static final Config CFG = Config.getInstance();

    private final GatewayApi gatewayApi = new RestClient.EmptyRestClient(CFG.gatewayUrl()).create(GatewayApi.class);

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Step("Создание траты через API")
    public @Nullable SpendJson createSpend(SpendJson spendJson, String token) {
        final Response<SpendJson> response;
        try {
            response = gatewayApi.addSpend("Bearer "+ token, spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code(), "Не удалось создать трату");
        return response.body();
    }
    
    @Step("Найти трату через API")
    public @Nullable SpendJson findSpend(SpendJson spendJson, String token) {
        final Response<SpendJson> response;
        try {
            response = gatewayApi.getSpend("Bearer "+ token, String.valueOf(spendJson.id())).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось найти трату");
        return response.body();
    }
    
    @Step("Найти траты по имени {username} и описанию {description} через API")
    public @Nonnull List<SpendJson> findSpendByUsernameAndDescription(String description, String token) {
        final Response<List<SpendJson>> response;
        try {
            response = gatewayApi.getSpends("Bearer "+ token, null, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить траты");
        return response.body() == null ? Collections.emptyList() : response.body().stream()
                .filter(spend -> spend.description().contains(description))
                .toList();
    }
    
    @Step("Удалить трату через API")
    public void deleteSpend(SpendJson spendJson, String token) {
        final Response<Void> response;
        try {
            response = gatewayApi.deleteSpends("Bearer "+ token, List.of(String.valueOf(spendJson.id())))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(202, response.code(), "Не удалось удалить трату");
    }
    
    @Step("Создание категории через API")
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson, String token) {
        final Response<CategoryJson> response;
        try {
            response = gatewayApi.createCategory("Bearer "+ token, categoryJson)
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

        return categoryJson.archived() ? updateCategory(createdCategory, "Bearer "+ token) : response.body();
    }
    
    @Step("Поиск категории по имени пользователя {username} и названию {categoryName} категории через API")
    public @Nullable CategoryJson findCategoryByUsernameAndCategoryName(String categoryName, String token) {
        final Response<List<CategoryJson>> response;
        try {
            response = gatewayApi.getCategories("Bearer "+ token, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить категории");
        return response.body() == null ? null : response.body().stream()
                .filter(category -> category.name().contains(categoryName))
                .findFirst().orElseGet(null);
    }
    
    @Step("Удаление категории через API")
    public void deleteCategory(CategoryJson categoryJson, String token) {
        throw new UnsupportedOperationException();
    }
    
    @Step("Обновить данные траты через API")
    public @Nullable SpendJson updateSpend(SpendJson spendJson, String token) {
        final Response<SpendJson> response;
        try {
            response = gatewayApi.editSpend("Bearer "+ token, spendJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось изменить трату");
        return response.body();
    }
    
    @Step("Обновить данные категории через API")
    public @Nullable CategoryJson updateCategory(CategoryJson categoryJson, String token) {
        final Response<CategoryJson> response;
        try {
            response = gatewayApi.updateCategory("Bearer "+ token, categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }
    
    @Step("Получить все траты по имени пользователя")
    public @Nullable List<SpendJson> getAllSpendsByUsername(String username, String token) {
        final Response<List<SpendJson>> response;
        try {
            response = gatewayApi.getSpends("Bearer "+ token, null, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }
    
    @Step("Получить все траты по имени пользователя")
    public @Nullable List<CategoryJson> getAllActiveCategoriesByUsername(String token) {
        final Response<List<CategoryJson>> response;
        try {
            response = gatewayApi.getCategories("Bearer "+ token, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }

    @Step("Удалить пользователя {username} через API")
    public void deleteUser() {
        throw new UnsupportedOperationException();
    }

    
    @Step("Отправить приглашение о дружбе пользователю через API")
    public @Nonnull List<String> sendInvitation(UserJson targetUser, int count, String token) {
        List<String> outcomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                final Response<UserJson> response;
                try {
                    response = gatewayApi.sendInvitation("Bearer " + token, targetUser.username()).execute();
                    outcomeRequests.add(username);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                Assertions.assertEquals(200, response.code(), "Не удалось отправить приглашение о дружбе");
            }
        }
        targetUser.testData().outcomeRequests().addAll(outcomeRequests);
        return outcomeRequests;
    }

    @Step("Добавить в друзья пользователя через API")
    public @Nonnull List<String> addFriend(UserJson targetUser, int count, String token) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UserJson> response;
                try {
                    String username = sendInvitation(targetUser, 1, "Bearer " + token).get(0);
                    targetUser.testData().outcomeRequests().clear();
                    String createdUserToken = authApiClient.login(targetUser.username(), targetUser.testData().password());
                    response = gatewayApi.acceptInvitation(createdUserToken, username).execute();
                    friends.add(username);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                Assertions.assertEquals(200, response.code(), "Не удалось добавить пользователя в друзья");
            }
        }

        targetUser.testData().friends().addAll(friends);
        return friends;
    }

    @Step("Обновить пользователя через API")
    public @Nullable UserJson updateUser(UserJson user, String token) {
        Response<UserJson> response;
        try {
            response = gatewayApi.updateUser("Bearer " + token, user).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось обновить данные пользователя");
        return response.body();
    }

    @Step("Обновить права пользователя {username} через API")
    public @Nullable AuthUserEntity updateAuthUser() {
        throw new UnsupportedOperationException();
    }

    @Step("Найти пользователя по id {id} через API")
    public @Nullable UserJson findById(UUID id, String token) {
        Response<List<UserJson>> response;
        try {
            response = gatewayApi.getAllUsers("Bearer " + token, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ?
                null :
                response.body().stream()
                        .filter(user -> user.id().equals(id))
                        .findFirst()
                        .orElseGet(null);
    }

    @Step("Найти всех пользователей через API")
    public @Nonnull List<UserJson> findAllUsers(String token) {
        Response<List<UserJson>> response;
        try {
            response = gatewayApi.getAllUsers("Bearer " + token, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }

    
    @Step("Найти всех друзей через API")
    public @Nonnull List<UserJson> findAllFriends(String token) {
        Response<List<UserJson>> response;
        try {
            response = gatewayApi.getAllFriends("Bearer " + token, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }
}
