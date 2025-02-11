package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper instance = new AuthUserEntityRowMapper();

    @Override
    public @Nonnull AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setId(rs.getObject("id", UUID.class));
        authUserEntity.setUsername(rs.getString("username"));
        authUserEntity.setPassword(rs.getString("password"));
        authUserEntity.setEnabled(rs.getBoolean("enabled"));
        authUserEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        authUserEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        authUserEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return authUserEntity;
    }
}
