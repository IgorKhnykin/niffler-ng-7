package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.rest.Authority;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void addAuthority(AuthAuthorityEntity... authorityEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
            for (AuthAuthorityEntity auth : authorityEntity) {
                ps.setObject(1, auth.getUser().getId());
                ps.setString(2, auth.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM authority")) {
            ps.execute();

            List<AuthAuthorityEntity> authAuthorityEntities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthAuthorityEntity au = new AuthAuthorityEntity();
                    au.setId(rs.getObject(1, UUID.class));
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setId(rs.getObject(2, UUID.class));
                    au.setUser(authUser);
                    au.setAuthority(Authority.valueOf(rs.getString(3)));
                    authAuthorityEntities.add(au);
                }
                return authAuthorityEntities;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthority(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM authority WHERE user_id = ?")){
            ps.setObject(1, authUserEntity.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
