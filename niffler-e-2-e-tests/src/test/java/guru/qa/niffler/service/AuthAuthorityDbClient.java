package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.model.AuthAuthorityJson;

import java.util.List;

import static guru.qa.niffler.data.DataBases.transaction;

public class AuthAuthorityDbClient {

    private static final int TRANSACTION_READ_COMMITTED = 2;

    public static final Config CFG = Config.getInstance();

    public void addAuthority(AuthAuthorityJson authorityJson) {
        transaction(connection -> {
            AuthAuthorityEntity authorityEntity = AuthAuthorityEntity.fromJson(authorityJson);
            new AuthAuthorityDaoJdbc(connection).addAuthority(authorityEntity);
        }, CFG.authJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public List<AuthAuthorityJson> findAll() {
        AuthAuthorityDaoSpringJdbc authUserDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc(DataBases.dataSource(CFG.authJdbcUrl()));
        return authUserDaoSpringJdbc.findAll().stream().map(AuthAuthorityJson::fromEntity).toList();
    }
}
