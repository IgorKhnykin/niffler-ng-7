package guru.qa.niffler.service;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserApiClient implements UserClient {

    private static final String PASSWORD = "1234";

    final guru.qa.niffler.api.UserApiClient userApiClient = new guru.qa.niffler.api.UserApiClient();

    final guru.qa.niffler.api.AuthApiClient authApiClient = new guru.qa.niffler.api.AuthApiClient();

    @Override
    @Step("Создать пользователя {username} через API")
    public @Nonnull UserJson createUser(String username, String password) {
        authApiClient.register(username, password);
        return new UserJson(null,
                username,
                null,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                null,
                null);
    }

    @Override
    @Step("Удалить пользователя {username} через API")
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Отправить приглашение о дружбе пользователю через API")
    public  @Nonnull List<String> sendInvitation(UserJson targetUser, int count) {
        List<String> outcomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, PASSWORD);
                userApiClient.sendInvitation(targetUser.username(), username);
                outcomeRequests.add(username);
            }
        }
        return outcomeRequests;
    }

    @Override
    @Step("Получить приглашение о дружбе от пользователя через API")
    public @Nonnull List<String> getInvitation(UserJson targetUser, int count) {
        List<String> incomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, PASSWORD);
                userApiClient.sendInvitation(username, targetUser.username());
                incomeRequests.add(username);
            }
        }
        return incomeRequests;
    }

    @Override
    @Step("Добавить в друзья пользователя через API")
    public @Nonnull List<String> addFriend(UserJson targetUser, int count) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, PASSWORD);
                userApiClient.sendInvitation(username, targetUser.username());
                userApiClient.acceptInvitation(targetUser.username(), username);
                friends.add(username);
            }
        }
        return friends;
    }

    @Override
    @Step("Обновить пользователя через API")
    public @Nullable UserJson updateUser(UserJson user) {
        return userApiClient.updateUser(user);
    }

    @Override
    @Step("Обновить права пользователя {username} через API")
    public @Nullable AuthUserEntity updateAuthUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Найти пользователя по id {id} через API")
    public @Nullable UserJson findById(UUID id) {
        return userApiClient.getAllUsers("username,ASC").stream().filter(user -> user.id().equals(id)).findFirst().orElseGet(null);
    }

    @Override
    @Step("Найти всех пользователей через API")
    public @Nonnull List<UserJson> findAllUsers() {
        return userApiClient.getAllUsers("username,ASC");
    }
}
