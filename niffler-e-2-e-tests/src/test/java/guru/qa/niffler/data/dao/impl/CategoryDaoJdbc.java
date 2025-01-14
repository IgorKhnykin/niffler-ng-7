package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO category (name, username, archived)" +
                            " VALUES(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getUsername());
                ps.setBoolean(3, category.isArchived());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject(1, UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                category.setId(generatedKey);
                return category;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM category where id = ?")) {
                ps.setObject(1, id);

                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setName(rs.getString("name"));
                        ce.setArchived(rs.getBoolean("archived"));

                        return Optional.of(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM category where username = ? and name = ?")) {
                ps.setObject(1, username);
                ps.setObject(2, categoryName);

                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setName(rs.getString("name"));
                        ce.setArchived(rs.getBoolean("archived"));

                        return Optional.of(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM category WHERE username = ?")) {
                ps.setObject(1, username);
                ps.execute();
                List<CategoryEntity> categoryEntities = new ArrayList<>();
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setName(rs.getString("name"));
                        ce.setUsername(rs.getString("username"));
                        ce.setArchived(rs.getBoolean("archived"));
                        categoryEntities.add(ce);
                    }
                }
                return categoryEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        try (Connection connection = DataBases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM category WHERE id = ?")) {
                ps.setObject(1, category.getId());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
