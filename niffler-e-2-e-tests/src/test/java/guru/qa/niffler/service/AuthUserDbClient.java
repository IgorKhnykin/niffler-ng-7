package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;

import static guru.qa.niffler.data.DataBases.transaction;
import static guru.qa.niffler.data.DataBases.xaTransaction;

public class AuthUserDbClient {

    public static final Config CFG = Config.getInstance();

    public AuthUserJson createClient1(AuthUserJson json) {
        return transaction(connection -> {
            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(json);
            return AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).create(authUserEntity));
        }, CFG.authJdbcUrl(), 2);
    }

    public AuthAuthorityJson addAuthority(AuthAuthorityJson authorityJson) {
        return transaction(connection -> {
            AuthAuthorityEntity authorityEntity = AuthAuthorityEntity.fromJson(authorityJson);
            return AuthAuthorityJson.fromEntity(new AuthAuthorityDaoJdbc(connection).addAuthority(authorityEntity));
        }, CFG.authJdbcUrl(), 2);
    }

    public void createClient(AuthAuthorityJson authorityJson) {
        xaTransaction(2,

                new DataBases.XaConsumer(connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authorityJson.userJson());
                    new AuthUserDaoJdbc(connection).create(authUserEntity);
                }, CFG.authJdbcUrl()),

                new DataBases.XaConsumer(connection -> {
                    AuthAuthorityEntity authorityEntity = AuthAuthorityEntity.fromJson(authorityJson);
                    new AuthAuthorityDaoJdbc(connection).addAuthority(authorityEntity);
                }, CFG.authJdbcUrl()));
    }
}
