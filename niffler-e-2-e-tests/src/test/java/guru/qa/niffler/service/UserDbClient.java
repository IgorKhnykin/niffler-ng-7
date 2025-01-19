package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.DataBases.transaction;

public class UserDbClient {

    private static final int TRANSACTION_READ_COMMITTED = 2;

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        return transaction(connection -> {
            UserEntity ue = new UserdataUserDaoJdbc(connection).createUser(UserEntity.fromJson(user));
            return UserJson.fromEntity(ue);
        }, CFG.userdataJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public Optional<UserJson> findUserById(UUID id) {
        return transaction(connection -> {
            Optional<UserEntity> se = new UserdataUserDaoJdbc(connection).findById(id);
            return se.map(UserJson::fromEntity);
        }, CFG.userdataJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public List<UserJson> findAllByUsername(String username) {
        return transaction(connection -> {
            return new UserdataUserDaoJdbc(connection).findByUsername(username)
                    .stream()
                    .map(UserJson::fromEntity)
                    .toList();
        }, CFG.userdataJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public void deleteUser(UserJson UserJson) {
        transaction(connection -> {
            UserEntity spendEntity = UserEntity.fromJson(UserJson);
            new UserdataUserDaoJdbc(connection).delete(spendEntity);
        }, CFG.userdataJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(pe.encode("1234"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(DataBases.dataSource(CFG.authJdbcUrl()))
                .create(authUserEntity);

        AuthAuthorityEntity[] authAuthorityEntities = Arrays.stream(Authority.values()).map(e -> {
            AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
            authorityEntity.setUser(createdAuthUser);
            authorityEntity.setAuthority(e);
            return authorityEntity;
        }).toArray(AuthAuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(DataBases.dataSource(CFG.authJdbcUrl()))
                .addAuthority(authAuthorityEntities);

        return UserJson.fromEntity(new UserdataUserDaoSpringJdbc(DataBases.dataSource(CFG.userdataJdbcUrl()))
                .createUser(UserEntity.fromJson(user)));

    }
}
