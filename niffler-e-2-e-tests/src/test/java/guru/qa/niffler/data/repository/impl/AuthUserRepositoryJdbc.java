package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity authUser) {  //+
        authUserDao.create(authUser);
        authAuthorityDao.addAuthority(authUser.getAuthorities().toArray(new AuthAuthorityEntity[0]));
        return authUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity authUser) { //+
        authUserDao.update(authUser);
        return authUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "select a.id as authority_id, " +
                        "authority," +
                        "user_id as id, " +
                        "u.username, " +
                        "u.password, " +
                        "u.enabled, " +
                        "u.account_non_expired, " +
                        "u.account_non_locked, " +
                        "u.credentials_non_expired " +
                        "from \"user\" u join authority a on u.id = a.user_id where u.id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                return Optional.ofNullable(AuthUserEntityExtractor.instance.extractData(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserByUsername(String username) {  //+
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "select a.id as authority_id, " +
                        "authority," +
                        "user_id as id, " +
                        "u.username, " +
                        "u.password, " +
                        "u.enabled, " +
                        "u.account_non_expired, " +
                        "u.account_non_locked, " +
                        "u.credentials_non_expired " +
                        "from \"user\" u join authority a on u.id = a.user_id where username = ?")) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                return Optional.ofNullable(AuthUserEntityExtractor.instance.extractData(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthUserEntity authUser) {  //+
        authAuthorityDao.deleteAuthority(authUser);
        authUserDao.deleteUser(authUser);
    }
}
