package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHebirnate;
import guru.qa.niffler.data.repository.impl.UserRepositoryJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHebirnate();

    private final UserRepository userRepository = new UserRepositoryJdbc();

    public UserJson createUserRepositoryJdbc(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username, password);

            AuthUserEntity userEntity = authUserRepository.create(authUserEntity);

            return UserJson.fromEntity(userRepository.createUser(userEntity(username)));
        });
    }


    //todo попробовать написать метод, который будет принимать юзера и количество друзейб который он хочет добавить
    //ищем пользователя в бд и потом через цикл создаем нужное кол-во пользователей и добавляем их в друзья

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
