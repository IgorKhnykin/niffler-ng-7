package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UserClient;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static guru.qa.niffler.utils.RandomDataUtils.passwordMain;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient {

    private static final Config CFG = Config.getInstance();

    private final GatewayApi gatewayApi = new RestClient.EmptyRestClient(CFG.gatewayUrl()).create(GatewayApi.class);

    private final UserClient userClient = new UserApiClient();

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Step("Создание траты через API")
    public @Nullable SpendJson createSpend(SpendJson spendJson, String token) {
        final Response<SpendJson> response;
        try {
            response = gatewayApi.addSpend(token, spendJson)
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
            response = gatewayApi.getSpend(token, String.valueOf(spendJson.id())).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось найти трату");
        return response.body();
    }
    
    @Step("Найти траты по описанию {description} через API")
    public @Nonnull List<SpendJson> findSpendByUsernameAndDescription(String description, String token) {
        final Response<List<SpendJson>> response;
        try {
            response = gatewayApi.getSpends(token, null, null)
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
            response = gatewayApi.deleteSpends(token, List.of(String.valueOf(spendJson.id())))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось удалить трату");
    }
    
    @Step("Создание категории через API")
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson, String token) {
        final Response<CategoryJson> response;
        try {
            response = gatewayApi.createCategory(token, categoryJson)
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

        return categoryJson.archived() ? updateCategory(createdCategory, token) : response.body();
    }
    
    @Step("Поиск категории по названию {categoryName} категории через API")
    public @Nullable CategoryJson findCategoryByCategoryName(String categoryName, String token) {
        final Response<List<CategoryJson>> response;
        try {
            response = gatewayApi.getCategories(token, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось найти категорию по названию");
        return response.body() == null ? null : response.body().stream()
                .filter(category -> category.name().contains(categoryName))
                .findFirst().orElseGet(() -> new CategoryJson(null, null, null, false));
    }
    
    @Step("Удаление категории через API")
    public void deleteCategory(CategoryJson categoryJson, String token) {
        throw new UnsupportedOperationException();
    }
    
    @Step("Обновить данные траты через API")
    public @Nullable SpendJson updateSpend(SpendJson spendJson, String token) {
        final Response<SpendJson> response;
        try {
            response = gatewayApi.editSpend(token, spendJson)
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
            response = gatewayApi.updateCategory(token, categoryJson)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось обновить категорию");
        return response.body();
    }
    
    @Step("Получить все траты пользователя")
    public @Nullable List<SpendJson> getAllSpends(String token) {
        final Response<List<SpendJson>> response;
        try {
            response = gatewayApi.getSpends(token, null, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить все категории");
        return response.body();
    }
    
    @Step("Получить все категории пользователя")
    public @Nullable List<CategoryJson> getAllActiveCategories(String token) {
        final Response<List<CategoryJson>> response;
        try {
            response = gatewayApi.getCategories(token, true)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Не удалось получить все активные категории");
        return response.body();
    }

    @Step("Удалить пользователя через API")
    public void deleteUser() {
        throw new UnsupportedOperationException();
    }

    
    @Step("Отправить приглашение о дружбе пользователю через API")
    public @Nonnull List<String> sendInvitation(UserJson targetUser, int count, String token) {
        List<String> outcomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                try {
                    UserJson createdUser = userClient.createUser(username, passwordMain);
                    response = gatewayApi.sendInvitation(token, createdUser).execute();
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

    @Step("Получить приглашение о дружбе через API")
    public @Nonnull List<String> getInvitation(UserJson targetUser, int count) {
        List<String> incomeRequest = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                try {
                    userClient.createUser(username, passwordMain);
                    String createdUserToken = authApiClient.login(username, passwordMain);
                    response = gatewayApi.sendInvitation(createdUserToken, targetUser).execute();
                    incomeRequest.add(username);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                Assertions.assertEquals(200, response.code(), "Не удалось получить приглашение о дружбе");
            }
        }
        targetUser.testData().incomeRequests().addAll(incomeRequest);
        return incomeRequest;
    }

    @Step("Добавить в друзья пользователя через API")
    public @Nonnull List<String> addFriend(UserJson targetUser, int count, String token) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UserJson> response;
                try {
                    String username = sendInvitation(targetUser, 1, token).get(0);
                    targetUser.testData().outcomeRequests().remove(username); //todo протестить
                    String createdUserToken = authApiClient.login(username, passwordMain);
                    UserJson createdUser = Objects.requireNonNull(gatewayApi.getCurrentUser(createdUserToken).execute().body());
                    response = gatewayApi.acceptInvitation(createdUserToken, createdUser).execute();
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

//    @Step("Отклонить запрос о дружбе")
//    public @Nonnull List<String> declineFriendshipInvitation(UserJson targetUser, int count, String token) {
//        List<String> friends = new ArrayList<>();
//        if (count > 0) {
//            for (int i = 0; i < count; i++) {
//                final Response<UserJson> response;
//                try {
//                    getInvitation(targetUser, 1);
//                    String createdUserToken = authApiClient.login(username, passwordMain);
//                } catch (IOException e) {
//                    throw new AssertionError(e);
//                }
//                Assertions.assertEquals(200, response.code(), "Не удалось добавить пользователя в друзья");
//            }
//        }
//
//        targetUser.testData().friends().addAll(friends);
//        return friends;
//    }

    @Step("Обновить пользователя через API")
    public @Nullable UserJson updateUser(UserJson user, String token) {
        Response<UserJson> response;
        try {
            response = gatewayApi.updateUser(token, user).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось обновить данные пользователя");
        return response.body();
    }

    @Step("Обновить права пользователя через API")
    public @Nullable AuthUserEntity updateAuthUser() {
        throw new UnsupportedOperationException();
    }

    @Step("Найти пользователя по id {id} через API")
    public @Nullable UserJson findById(UUID id, String token) {
        Response<List<UserJson>> response;
        try {
            response = gatewayApi.getAllUsers(token, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить пользователя по id");
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
            response = gatewayApi.getAllUsers(token, null).execute();
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
            response = gatewayApi.getAllFriends(token, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех друзей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }
}
