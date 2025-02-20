package guru.qa.niffler.service;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UserApiClient;
import guru.qa.niffler.service.impl.UserDbClient;

import java.util.List;
import java.util.UUID;

public interface UserClient {

    static UserClient getInstance() {
        return "api".equals(System.getProperty("client.impl", "api")) ? new UserApiClient() : new UserDbClient();
    }

    UserJson createUser(String username, String password);

    void deleteUser(String username);

    List<String> sendInvitation(UserJson targetUser, int count);

    List<String> getInvitation(UserJson targetUser, int count);

    List<String> addFriend(UserJson targetUser, int count);

    UserJson updateUser(UserJson user);

    AuthUserEntity updateAuthUser(String username);

    UserJson findById(UUID id);

    List<UserJson> findAllUsers();
}
