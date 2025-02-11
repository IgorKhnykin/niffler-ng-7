package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.UserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class UserDbClient implements UserClient{

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());

    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();

    private final UserRepository userRepository = new UserRepositorySpringJdbc();

    @Override
    @Step("Создать пользователя {username} через базу данных")
    public @Nullable UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {

            AuthUserEntity authUserEntity = authUserEntity(username, password);

            authUserRepository.create(authUserEntity);

            return UserJson.fromEntity(userRepository.createUser(userEntity(username)));
        });
    }

    @Override
    @Step("Удалить пользователя {username} через базу данных")
    public void deleteUser(String username) {
        xaTransactionTemplate.execute(() -> {

            Optional<AuthUserEntity> authUserEntity = authUserRepository.findUserByUsername(username);

            Optional<UserEntity> userEntity = userRepository.findByUsername(username);

            if (authUserEntity.isPresent() && userEntity.isPresent()) {
                authUserRepository.remove(authUserEntity.get());

                userRepository.remove(userEntity.get());
            }
            return null;
        });
    }

    @Override
    @Step("Отправить приглашение о дружбе пользователю через базу данных")
    public @Nullable List<String> sendInvitation(UserJson targetUser, int count) {
        return xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> requester = userRepository.findByUsername(targetUser.username());
            List<String> outcomeRequests = new ArrayList<>();
            if (requester.isPresent()) {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "1234");

                    authUserRepository.create(authUserEntity);

                    UserEntity assignee = userRepository.createUser(userEntity(username));

                    userRepository.sendInvitation(requester.get(), assignee);

                    outcomeRequests.add(assignee.getUsername());
                }
            }
            return outcomeRequests;
        });
    }

    @Override
    @Step("СПолучить приглашение о дружбе от пользователя через базу данных")
    public @Nullable List<String> getInvitation(UserJson targetUser, int count) {
        return xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> assignee = userRepository.findByUsername(targetUser.username());
            List<String> incomeRequests = new ArrayList<>();
            if (assignee.isPresent()) {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "1234");

                    authUserRepository.create(authUserEntity);

                    UserEntity requester = userRepository.createUser(userEntity(username));

                    userRepository.sendInvitation(requester, assignee.get());
                    incomeRequests.add(requester.getUsername());
                }
            }
            return incomeRequests;
        });

    }

    @Override
    @Step("Добавить в друзья пользователя через базу данных")
    public @Nullable List<String> addFriend(UserJson targetUser, int count) {
        return xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> requester = userRepository.findByUsername(targetUser.username());
            List<String> friends = new ArrayList<>();
            if (requester.isPresent()) {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "1234");

                    authUserRepository.create(authUserEntity);

                    UserEntity assignee = userRepository.createUser(userEntity(username));

                    userRepository.addFriend(requester.get(), assignee);

                    friends.add(assignee.getUsername());
                }
            }
            return friends;
        });
    }

    @Override
    @Step("Обновить пользователя через базу данных")
    public @Nullable UserJson updateUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            return UserJson.fromEntity(userRepository.updateUser(userEntity));
        });
    }

    @Override
    @Step("Обновить права пользователя {username} через базу данных")
    public @Nullable AuthUserEntity updateAuthUser(String username) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity userEntity = authUserRepository.findUserByUsername(username).get();
            return authUserRepository.update(userEntity);
        });
    }

    @Override
    @Step("Найти пользователя по id {id} через базу данных")
    public @Nullable UserJson findById(UUID id) {
        return xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            if (userEntity.isPresent()) {
                return UserJson.fromEntity(userEntity.get());
            }
            else {
                return null;
            }
        });
    }

    @Override
    @Step("Найти всех пользователей через базу данных")
    public @Nullable List<UserJson> findAllUsers() {
        return xaTransactionTemplate.execute(() -> userRepository.findAll()
                .stream()
                .map(UserJson::fromEntity)
                .toList());
    }

    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(username);
        authUserEntity.setPassword(pe.encode(password));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        authUserEntity.setAuthorities(Arrays.stream(Authority.values()).map(e -> {
            AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
            authorityEntity.setUser(authUserEntity);
            authorityEntity.setAuthority(e);
            return authorityEntity;
        }).toList());
        return authUserEntity;
    }
}
