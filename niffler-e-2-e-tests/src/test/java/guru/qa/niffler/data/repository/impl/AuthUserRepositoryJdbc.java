package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
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

            for (AuthAuthorityEntity authorityEntity: authUser.getAuthorities()) {
                psAuthAuthority.setObject(1, authorityEntity.getUser().getId());
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
                "select * from \"user\" u join authority a on u.id = a.user_id where u.id = ?")) {
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
                    authorityEntity.setId(rs.getObject("a.id", UUID.class));
                    authorityEntity.setUser(authUser);
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

                    authorityEntityList.add(authorityEntity);

                    authUser.setId(rs.getObject("id", UUID.class)); //todo кажется код лишний
                    authUser.setUsername(rs.getString("username"));
                    authUser.setPassword(rs.getString("password"));
                    authUser.setEnabled(rs.getBoolean("enabled"));
                    authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                }
                if (authUser == null) {
                    return Optional.empty();
                }
                else {
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
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM \"user\" WHERE username = ?")) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity se = new AuthUserEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setPassword(rs.getString("password"));
                    se.setEnabled(rs.getBoolean("enabled"));
                    se.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    se.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    se.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity authUser) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("DELETE FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, authUser.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM \"user\"")) {
            ps.execute();

            List<AuthUserEntity> authUserEntityList = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity au = new AuthUserEntity();
                    au.setId(rs.getObject("id", UUID.class));
                    au.setUsername(rs.getString("username"));
                    au.setPassword(rs.getString("password"));
                    au.setEnabled(rs.getBoolean("enabled"));
                    au.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    au.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    au.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserEntityList.add(au);
                }
                return authUserEntityList;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
