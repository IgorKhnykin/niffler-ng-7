package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.extractor.AuthUserEntityListExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
    public static final Config CFG = Config.getInstance();
    private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        authUserDaoSpring.create(authUser);
        authAuthorityDaoSpring.addAuthority(authUser.getAuthorities().toArray(new AuthAuthorityEntity[0]));
        return authUser;
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
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
    public Optional<AuthUserEntity> findUserByUsername(String username) {
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
    public void deleteUser(AuthUserEntity authUser) {
        authUserDaoSpring.deleteUser(authUser);
        authUser.getAuthorities().forEach(authAuthorityEntity -> {
            authAuthorityDaoSpring.deleteAuthority(authUser);
        });
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        List<AuthUserEntity> authUser = jdbcTemplate.query(
                "select a.id as authority_id, " +
                        "authority," +
                        "user_id as id, " +
                        "u.username, " +
                        "u.password, " +
                        "u.enabled, " +
                        "u.account_non_expired, " +
                        "u.account_non_locked, " +
                        "u.credentials_non_expired " +
                        "from \"user\" u join authority a on u.id = a.user_id", AuthUserEntityListExtractor.instance);

        return authUser;
    }
}
