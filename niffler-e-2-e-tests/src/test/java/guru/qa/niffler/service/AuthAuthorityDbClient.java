package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.Authority;

import java.util.Arrays;
import java.util.List;

public class AuthAuthorityDbClient {

    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    public void addAuthority(AuthAuthorityJson authorityJson) {
        jdbcTxTemplate.execute(() -> {
            Arrays.stream(Authority.values()).forEach(e -> {
                AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
                authorityEntity.setUser(AuthUserEntity.fromJson(authorityJson.userJson()));
                authorityEntity.setAuthority(e);
                authAuthorityDao.addAuthority(authorityEntity);
            });
            return null;
        });
    }

    public List<AuthAuthorityJson> findAll() {
        return jdbcTxTemplate.execute(() -> authAuthorityDao.findAll().stream()
                .map(AuthAuthorityJson::fromEntity)
                .toList());
    }
}
