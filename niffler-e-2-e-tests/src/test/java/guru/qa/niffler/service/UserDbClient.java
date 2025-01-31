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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UserDbClient implements UserClient{

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());

    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();

    private final UserRepository userRepository = new UserRepositorySpringJdbc();

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {

            AuthUserEntity authUserEntity = authUserEntity(username, password);

            authUserRepository.create(authUserEntity);

            return UserJson.fromEntity(userRepository.createUser(userEntity(username)));
        });
    }

    @Override
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
    public void sendInvitation(UserJson targetUser, int count) {
        xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> requester = userRepository.findByUsername(targetUser.username());
            if (requester.isPresent()) {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "1234");

                    authUserRepository.create(authUserEntity);

                    UserEntity assignee = userRepository.createUser(userEntity(username));

                    userRepository.sendInvitation(requester.get(), assignee);
                }
            }
            return null;
        });
    }

    @Override
    public void addFriend(UserJson targetUser, int count) {
        xaTransactionTemplate.execute(() -> {
            Optional<UserEntity> requester = userRepository.findByUsername(targetUser.username());
            if (requester.isPresent()) {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "1234");

                    authUserRepository.create(authUserEntity);

                    UserEntity assignee = userRepository.createUser(userEntity(username));

                    userRepository.addFriend(requester.get(), assignee);
                }
            }
            return null;
        });
    }

    @Override
    public UserJson updateUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            return UserJson.fromEntity(userRepository.updateUser(userEntity));
        });
    }

    @Override
    public AuthUserEntity updateAuthUser(String username) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity userEntity = authUserRepository.findUserByUsername(username).get();
            AuthUserEntity userEntityUpdated = authUserRepository.update(userEntity);
            return userEntityUpdated;
        });
    }

    @Override
    public UserJson findById(UUID id) {
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
    public List<UserJson> findAllUsers() {
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
        authUserEntity.setPassword(password);
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
