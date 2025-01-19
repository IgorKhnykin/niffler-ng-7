package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private final DataSource dataSource;

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
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
    public Optional<AuthUserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        AuthUserEntity authUser = jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE id = ?",
                AuthUserEntityRowMapper.instance,
                id);
        return Optional.ofNullable(authUser);
    }

    @Override
    public Optional<AuthUserEntity> findUserByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        AuthUserEntity authUser = jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE name = ?",
                AuthUserEntityRowMapper.instance,
                username);
        return Optional.ofNullable(authUser);
    }

    @Override
    public void deleteUser(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM \"user\" WHERE id = ?");
            ps.setObject(1, authUser.getId());
            return ps;
        });
    }
}
