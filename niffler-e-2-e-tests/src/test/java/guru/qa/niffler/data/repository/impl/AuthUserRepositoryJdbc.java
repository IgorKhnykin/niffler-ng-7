package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.model.Authority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        try (PreparedStatement psAuthUser = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psAuthAuthority = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
            psAuthUser.setString(1, authUser.getUsername());
            psAuthUser.setString(2, pe.encode(authUser.getPassword())); // password to service
            psAuthUser.setBoolean(3, authUser.getEnabled());
            psAuthUser.setBoolean(4, authUser.getAccountNonExpired());
            psAuthUser.setBoolean(5, authUser.getAccountNonLocked());
            psAuthUser.setBoolean(6, authUser.getCredentialsNonExpired());
            psAuthUser.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = psAuthUser.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authUser.setId(generatedKey);

            for (AuthAuthorityEntity authorityEntity : authUser.getAuthorities()) {
                psAuthAuthority.setObject(1, generatedKey);
                psAuthAuthority.setString(2, authorityEntity.getAuthority().name());
                psAuthAuthority.addBatch();
                psAuthAuthority.clearParameters();
            }
            psAuthAuthority.executeBatch();
            return authUser;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
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
                List<AuthAuthorityEntity> authorityEntityList = new ArrayList<>();
                AuthUserEntity authUser = null;

                while (rs.next()) {
                    if (authUser == null) {
                        authUser = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
                    authorityEntity.setId(rs.getObject("authority_id", UUID.class));
                    authorityEntity.setUser(authUser);
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

                    authorityEntityList.add(authorityEntity);
                }
                if (authUser == null) {
                    return Optional.empty();
                } else {
                    authUser.setAuthorities(authorityEntityList);
                    return Optional.of(authUser);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findUserByUsername(String username) {
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
                List<AuthAuthorityEntity> authorityEntityList = new ArrayList<>();
                AuthUserEntity authUser = null;
                while (rs.next()) {
                    if (authUser == null) {
                        authUser = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
                    authorityEntity.setId(rs.getObject("authority_id", UUID.class));
                    authorityEntity.setUser(authUser);
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

                    authorityEntityList.add(authorityEntity);

                }
                if (authUser == null) {
                    return Optional.empty();
                } else {
                    authUser.setAuthorities(authorityEntityList);
                    return Optional.of(authUser);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity authUser) {
        try (PreparedStatement psAuthUser = holder(CFG.authJdbcUrl()).connection().prepareStatement("DELETE FROM \"user\" WHERE id = ?");
             PreparedStatement psAuthAuthority = holder(CFG.authJdbcUrl()).connection().prepareStatement("DELETE FROM authority WHERE user_id = ?")) {
            psAuthUser.setObject(1, authUser.getId());
            psAuthUser.execute();

            psAuthAuthority.setObject(1, authUser.getId());
            psAuthAuthority.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
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
                        "from \"user\" u join authority a on u.id = a.user_id")) {
            ps.execute();

            List<AuthUserEntity> authUserEntityList = new ArrayList<>();

            AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
            AuthUserEntity authUser = null;
            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {
                    UUID userId = rs.getObject("id", UUID.class);
                    Optional<AuthUserEntity> user = authUserEntityList.stream()
                            .filter(x -> x.getId().equals(userId)).findFirst();

                    if (user.isEmpty()) {
                        authUser = AuthUserEntityRowMapper.instance.mapRow(rs, 1);

                        authorityEntity.setId(rs.getObject("authority_id", UUID.class));
                        authorityEntity.setUser(authUser);
                        authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
                        authUser.setAuthorities(List.of(authorityEntity));
                        authUserEntityList.add(authUser);
                    } else {
                        authorityEntity.setId(rs.getObject("authority_id", UUID.class));
                        authorityEntity.setUser(user.get());
                        authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
                        authUser.setAuthorities(List.of(authorityEntity));
                    }
                }
                return authUserEntityList;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
