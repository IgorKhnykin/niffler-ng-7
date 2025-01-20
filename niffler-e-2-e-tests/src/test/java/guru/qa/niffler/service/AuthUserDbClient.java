package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;

import java.util.List;

import static guru.qa.niffler.data.DataBases.xaTransaction;

public class AuthUserDbClient {

    public static final Config CFG = Config.getInstance();

    private static final int TRANSACTION_READ_COMMITTED = 2;

    public void createUser(AuthAuthorityJson authorityJson) {
        xaTransaction(TRANSACTION_READ_COMMITTED,

                new DataBases.XaConsumer(connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authorityJson.userJson());
                    new AuthUserDaoJdbc(connection).create(authUserEntity);
                    AuthAuthorityEntity authorityEntity = AuthAuthorityEntity.fromJson(authorityJson);
                    new AuthAuthorityDaoJdbc(connection).addAuthority(authorityEntity);
                }, CFG.authJdbcUrl()));
    }

    public List<AuthUserJson> findAll() {
        AuthUserDaoSpringJdbc authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc(DataBases.dataSource(CFG.authJdbcUrl()));
        return authUserDaoSpringJdbc.findAll().stream().map(AuthUserJson::fromEntity).toList();
    }
}
