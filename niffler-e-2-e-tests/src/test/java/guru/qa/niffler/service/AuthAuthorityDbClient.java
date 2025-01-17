package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.model.AuthAuthorityJson;

import static guru.qa.niffler.data.DataBases.transaction;

public class AuthAuthorityDbClient {

    public static final Config CFG = Config.getInstance();

    public AuthAuthorityJson addAuthority(AuthAuthorityJson authorityJson) {
        return transaction(connection -> {
            AuthAuthorityEntity authorityEntity = AuthAuthorityEntity.fromJson(authorityJson);
            return AuthAuthorityJson.fromEntity(new AuthAuthorityDaoJdbc(connection).addAuthority(authorityEntity));
        }, CFG.authJdbcUrl(), 2);
    }
}
