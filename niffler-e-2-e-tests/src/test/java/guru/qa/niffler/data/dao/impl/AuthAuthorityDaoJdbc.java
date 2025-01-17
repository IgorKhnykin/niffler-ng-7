package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthAuthorityEntity addAuthority(AuthAuthorityEntity authorityEntity) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, authorityEntity.getUser().getId());
            ps.setString(2, authorityEntity.getAuthority().name());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
                authorityEntity.setId(generatedKey);
                return authorityEntity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthority(AuthAuthorityEntity authorityEntity) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM authority WHERE id = ?")) {
            ps.setObject(1, authorityEntity.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
