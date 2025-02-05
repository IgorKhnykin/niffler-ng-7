package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                                    "VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, authUser.getUsername());
                    ps.setString(2, authUser.getPassword());
                    ps.setBoolean(3, authUser.getEnabled());
                    ps.setBoolean(4, authUser.getAccountNonExpired());
                    ps.setBoolean(5, authUser.getAccountNonLocked());
                    ps.setBoolean(6, authUser.getCredentialsNonExpired());
                    return ps;
                }, keyHolder
        );
        final UUID generatedId = (UUID) keyHolder.getKeys().get("id");
        authUser.setId(generatedId);
        return authUser;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "update \"user\" set username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? where id = ?");
                ps.setString(1, authUser.getUsername());
                ps.setString(2, authUser.getPassword());
                ps.setBoolean(3, authUser.getEnabled());
                ps.setBoolean(4, authUser.getAccountNonExpired());
                ps.setBoolean(5, authUser.getAccountNonLocked());
                ps.setBoolean(6, authUser.getCredentialsNonExpired());
                ps.setObject(7, authUser.getId());
            return ps;
        });
        return authUser;
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        AuthUserEntity authUser = jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE id = ?",
                AuthUserEntityRowMapper.instance,
                id);
        return Optional.ofNullable(authUser);
    }

    @Override
    public Optional<AuthUserEntity> findUserByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        AuthUserEntity authUser = jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE username = ?",
                AuthUserEntityRowMapper.instance,
                username);
        return Optional.ofNullable(authUser);
    }

    @Override
    public void deleteUser(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM \"user\" WHERE id = ?");
            ps.setObject(1, authUser.getId());
            return ps;
        });
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query("SELECT * FROM \"user\"", AuthUserEntityRowMapper.instance);
    }
}
