package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

public interface AuthAuthorityDao {

    AuthAuthorityEntity addAuthority(AuthAuthorityEntity authorityEntity);

    void deleteAuthority(AuthAuthorityEntity AuthAuthorityEntity);
}
