package guru.qa.niffler.service;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserApiClient implements UserClient {

    final guru.qa.niffler.api.UserApiClient userApiClient = new guru.qa.niffler.api.UserApiClient();

    final guru.qa.niffler.api.AuthApiClient authApiClient = new guru.qa.niffler.api.AuthApiClient();

    @Override
    public UserJson createUser(String username, String password) {
        authApiClient.register(username, password);
        return new UserJson(null,
                username,
                null,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                null);
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> sendInvitation(UserJson targetUser, int count) {
        List<String> outcomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, "1234");
                userApiClient.sendInvitation(targetUser.username(), username);
                outcomeRequests.add(username);
            }
        }
        return outcomeRequests;
    }

    @Override
    public List<String> getInvitation(UserJson targetUser, int count) {
        List<String> incomeRequests = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, "1234");
                userApiClient.sendInvitation(username, targetUser.username());
                incomeRequests.add(username);
            }
        }
        return incomeRequests;
    }

    @Override
    public List<String> addFriend(UserJson targetUser, int count) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.randomUsername();
                authApiClient.register(username, "1234");
                userApiClient.acceptInvitation(targetUser.username(), username);
                friends.add(username);
            }
        }
        return friends;
    }

    @Override
    public UserJson updateUser(UserJson user) {
        return userApiClient.updateUser(user);
    }

    @Override
    public AuthUserEntity updateAuthUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserJson findById(UUID id) {
        return userApiClient.getAllUsers("username,ASC").stream().filter(user -> user.id().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<UserJson> findAllUsers() {
        return userApiClient.getAllUsers("username,ASC");
    }
}
