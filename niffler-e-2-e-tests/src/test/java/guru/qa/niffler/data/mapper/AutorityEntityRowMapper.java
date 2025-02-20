package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.Authority;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AutorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {

    public static final AutorityEntityRowMapper instance = new AutorityEntityRowMapper();

    @Override
    public @Nonnull AuthAuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthAuthorityEntity ae = new AuthAuthorityEntity();
        ae.setId(rs.getObject("id", UUID.class));
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setId(rs.getObject("user_id", UUID.class));
        ae.setUser(authUser);
        ae.setAuthority(Authority.valueOf(rs.getString("authority")));
        return ae;
    }
}
