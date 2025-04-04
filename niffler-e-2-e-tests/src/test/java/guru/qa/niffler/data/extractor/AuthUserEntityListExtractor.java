package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.model.rest.Authority;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class AuthUserEntityListExtractor implements ResultSetExtractor<List<AuthUserEntity>> {

    public static final AuthUserEntityListExtractor instance = new AuthUserEntityListExtractor();

    @Override
    public @Nonnull List<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId;
        List<AuthUserEntity> authUserEntityList = new ArrayList<>();
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    return AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
            authorityEntity.setId(rs.getObject("authority_id", UUID.class));
            authorityEntity.setUser(user);
            authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
            user.getAuthorities().add(authorityEntity);

            if (!authUserEntityList.contains(user)) {
                authUserEntityList.add(user);
            }
        }
        return authUserEntityList;
    }
}
