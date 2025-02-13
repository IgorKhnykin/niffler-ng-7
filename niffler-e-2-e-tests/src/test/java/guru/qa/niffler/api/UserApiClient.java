package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class UserApiClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = retrofit.create(UserApi.class);

    public @Nullable UserJson getCurrentUser(String username) {
        Response<UserJson> response;
        try {
            response = userApi.getCurrentUser(username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить пользователя");
        return response.body();
    }

    public @Nonnull List<UserJson> getAllUsers(String username) {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllUsers(username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }

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

    public @Nullable UserJson sendInvitation(String username, String targetUser) {
        Response<UserJson> response;
        try {
            response = userApi.sendInvitation(username, targetUser).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось отправить приглашение о дружбе");
        return response.body();
    }

    public @Nullable UserJson acceptInvitation(String username, String targetUser) {
        Response<UserJson> response;
        try {
            response = userApi.acceptInvitation(username, targetUser).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось принять приглашение о дружбе");
        return response.body();
    }

    public @Nullable UserJson declineInvitation(String username, String targetUser) {
        Response<UserJson> response;
        try {
            response = userApi.declineInvitation(username, targetUser).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось отклонить приглашение о дружбе");
        return response.body();
    }

    public @Nonnull List<UserJson> getAllFriends(String username) {
        Response<List<UserJson>> response;
        try {
            response = userApi.getAllFriends(username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось получить всех пользователей");
        return response.body() == null ? Collections.emptyList() : response.body();
    }

    public void removeFriend(String username, String targetUser) {
        Response<Void> response;
        try {
            response = userApi.removeFriend(username, targetUser).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code(), "Не удалось удалить друга");
    }
}
