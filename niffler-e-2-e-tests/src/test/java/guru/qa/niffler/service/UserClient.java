package guru.qa.niffler.service;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.UUID;

public interface UserClient {

    UserJson createUser(String username, String password);

    void deleteUser(String username);

    void sendInvitation(UserJson targetUser, int count);

    void addFriend(UserJson targetUser, int count);

    UserJson updateUser(UserJson user);

    AuthUserEntity updateAuthUser(String username);

    UserJson findById(UUID id);

    List<UserJson> findAllUsers();
}
