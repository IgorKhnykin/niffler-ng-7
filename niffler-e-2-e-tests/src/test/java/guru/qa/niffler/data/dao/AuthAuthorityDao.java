package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;

public interface AuthAuthorityDao {

    void addAuthority(AuthAuthorityEntity... authorityEntity);

    List<AuthAuthorityEntity> findAll();

    void deleteAuthority(AuthUserEntity authUserEntity);

}
