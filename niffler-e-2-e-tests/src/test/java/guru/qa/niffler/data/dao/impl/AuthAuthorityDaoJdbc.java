package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.sql.*;
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
}
