package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.Authority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addAuthority(AuthAuthorityEntity... authorityEntity) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
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
    public List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM authority")) {
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
}
