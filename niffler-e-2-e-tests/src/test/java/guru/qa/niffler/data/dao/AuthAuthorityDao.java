package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {

    void addAuthority(AuthAuthorityEntity... authorityEntity);

    @Nonnull
    List<AuthAuthorityEntity> findAll();

    void deleteAuthority(AuthUserEntity authUserEntity);

}
