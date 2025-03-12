package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.utils.OauthUtils;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;

@ParametersAreNonnullByDefault
public class UserApiClient implements UserClient {

    private static final String USERNAME = "Igor";
    private static final String PASSWORD = "1234";
    private static final String REDIRECT_URI = "http://127.0.0.1:3000/authorized";
    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CODE_CHALLENGE_METHOD = "S256";

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyRestClient(CFG.authUrl(), true).create(AuthApi.class);
    private final UserApi userApi = new RestClient.EmptyRestClient(CFG.userdataUrl(), true).create(UserApi.class);

    @Step("Авторизовать пользователя")
    public String loginUser() {
        final Response<Void> loginResponse;
        final Response<JsonNode> token;
        try {
            String codeVerifier = OauthUtils.generateCodeVerifier();
            authApi.authorize(RESPONSE_TYPE, CLIENT_ID, SCOPE,
                    REDIRECT_URI, generateCodeChallenge(codeVerifier), CODE_CHALLENGE_METHOD).execute();
            loginResponse = authApi.login(ThreadSafeCookieStore.INSTANCE.xsrfCookieValue(), USERNAME, PASSWORD).execute();
            String code = StringUtils.substringAfter(loginResponse.raw().priorResponse().headers("Location").get(0), "code=");

            token = authApi.getToken(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute();
        } catch (IOException | NoSuchAlgorithmException  e) {
            throw new AssertionError(e);
        }
        return token.body().get("id_token").asText();
    }

    @Override
    @Step("Создать пользователя {username} через API")
    public @Nonnull UserJson createUser(String username, String password) {
        final Response<Void> response;
        UserJson createdUser;
        try {
            authApi.requestRegisterForm().execute();

            response = authApi.register(ThreadSafeCookieStore.INSTANCE.xsrfCookieValue(), username, password, password)
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
            response = userApi.getAllUsers("username,ASC").execute();
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

    @Override
    @Step("Найти всех пользователей через API")
    public @Nonnull List<UserJson> findAllUsers() {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllUsers("username,ASC").execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }
}
