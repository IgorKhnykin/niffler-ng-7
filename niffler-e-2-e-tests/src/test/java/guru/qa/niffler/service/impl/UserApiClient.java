package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.*;

@ParametersAreNonnullByDefault
public class UserApiClient implements UserClient {

    private static final String PASSWORD = "1234";

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyRestClient(CFG.authUrl(), true).create(AuthApi.class);
    private final UserApi userApi = new RestClient.EmptyRestClient(CFG.userdataUrl(), true).create(UserApi.class);

    @Override
    @Step("Создать пользователя {username} через API")
    public @Nonnull UserJson createUser(String username, String password) {
        final Response<Void> response;
        UserJson createdUser;
        try {
            authApi.requestRegisterForm().execute();

            response = authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.xsrfCookieValue())
                    .execute();

            createdUser = Objects.requireNonNull(userApi.getCurrentUser(username).execute().body())
                    .addTestData(new TestData(password));

        } catch (IOException e) {
            throw new AssertionError(e);
        }

        Assertions.assertEquals(201, response.code(), "Не удалось зарегистрировать пользователя");
        return createdUser;
    }

    @Override
    @Step("Удалить пользователя {username} через API")
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Отправить приглашение о дружбе пользователю через API")
    public @Nonnull List<String> sendInvitation(UserJson targetUser, int count) {
        List<String> outcomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                final Response<UserJson> response;
                try {
                    createUser(username, PASSWORD);
                    response = userApi.sendInvitation(targetUser.username(), username).execute();
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

    @Override
    @Step("Получить приглашение о дружбе от пользователя через API")
    public @Nonnull List<String> getInvitation(UserJson targetUser, int count) {
        List<String> incomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                final Response<UserJson> response;
                try {
                    createUser(username, PASSWORD);
                    response = userApi.sendInvitation(username, targetUser.username()).execute();
                    incomeRequests.add(username);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                Assertions.assertEquals(200, response.code(), "Не удалось получить приглашение о дружбе");
            }
        }
        targetUser.testData().incomeRequests().addAll(incomeRequests);
        return incomeRequests;
    }

    @Override
    @Step("Добавить в друзья пользователя через API")
    public @Nonnull List<String> addFriend(UserJson targetUser, int count) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UserJson> response;
                try {
                    String username = getInvitation(targetUser, 1).get(0);
                    targetUser.testData().incomeRequests().clear();
                    response = userApi.acceptInvitation(targetUser.username(), username).execute();
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

    @Override
    @Step("Обновить пользователя через API")
    public @Nullable UserJson updateUser(UserJson user) {
        Response<UserJson> response;
        try {
            response = userApi.updateUser(user).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось обновить данные пользователя");
        return response.body();
    }

    @Override
    @Step("Обновить права пользователя {username} через API")
    public @Nullable AuthUserEntity updateAuthUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Найти пользователя по id {id} через API")
    public @Nullable UserJson findById(UUID id) {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllUsers("Igor").execute();
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

    @Override
    @Step("Найти всех пользователей через API")
    public @Nonnull List<UserJson> findAllUsers(String username) {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllUsers(username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }

    @Override
    @Step("Найти всех друзей через API")
    public @Nonnull List<UserJson> findAllFriends(String username) {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllFriends(username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех друзей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }
}
