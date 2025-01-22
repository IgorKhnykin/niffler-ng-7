package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());

    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoSpringJdbc();

    public AuthUserJson createUser(AuthUserJson user) {
        return jdbcTxTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserDaoJdbc.create(AuthUserEntity.fromJson(user));
            return AuthUserJson.fromEntity(authUserEntity);
        });
    }

    public Optional<AuthUserJson> findUserById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
            Optional<AuthUserEntity> se = authUserDaoJdbc.findUserById(id);
            return se.map(AuthUserJson::fromEntity);
        });
    }

    public Optional<AuthUserJson> findUserByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
            Optional<AuthUserEntity> se = authUserDaoJdbc.findUserByUsername(username);
            return se.map(AuthUserJson::fromEntity);
        });
    }

    public void deleteUser(AuthUserJson authUserJson) {
        jdbcTxTemplate.execute(() -> {
            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
            authUserDaoJdbc.deleteUser(authUserEntity);
            return null;
        });
    }

    public List<AuthUserJson> findAll() {
        return jdbcTxTemplate.execute(() -> authUserDaoJdbc.findAll().stream()
                .map(AuthUserJson::fromEntity)
                .toList());
    }
}
