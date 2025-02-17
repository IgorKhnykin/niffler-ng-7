package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    public static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity authUser) {
        authUserDao.create(authUser);
        authAuthorityDao.addAuthority(authUser.getAuthorities().toArray(new AuthAuthorityEntity[0]));
        return authUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity authUser) {
        return authUserDao.update(authUser);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        AuthUserEntity authUser = jdbcTemplate.query(
                "select a.id as authority_id, " +
                        "authority," +
                        "user_id as id, " +
                        "u.username, " +
                        "u.password, " +
                        "u.enabled, " +
                        "u.account_non_expired, " +
                        "u.account_non_locked, " +
                        "u.credentials_non_expired " +
                        "from \"user\" u join authority a on u.id = a.user_id where u.id = ?", AuthUserEntityExtractor.instance, id);

        return Optional.ofNullable(authUser);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        AuthUserEntity authUser = jdbcTemplate.query(
                "select a.id as authority_id, " +
                        "authority," +
                        "user_id as id, " +
                        "u.username, " +
                        "u.password, " +
                        "u.enabled, " +
                        "u.account_non_expired, " +
                        "u.account_non_locked, " +
                        "u.credentials_non_expired " +
                        "from \"user\" u join authority a on u.id = a.user_id where u.username = ?", AuthUserEntityExtractor.instance, username);

        return Optional.ofNullable(authUser);
    }

    @Override
    public void remove(AuthUserEntity authUser) {
        authAuthorityDao.deleteAuthority(authUser);
        authUserDao.deleteUser(authUser);
    }
}
