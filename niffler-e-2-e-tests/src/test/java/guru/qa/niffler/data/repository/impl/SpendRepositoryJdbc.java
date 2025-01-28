package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement CreateSpendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

             PreparedStatement findCategoryPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                     "SELECT * FROM category where name = ?");

             PreparedStatement CreateCategoryPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO category (name, username, archived) VALUES(?, ?, ?)"
        )) {

            findCategoryPs.setObject(1, spend.getCategory().getName());
            findCategoryPs.execute();
            UUID categoryId = null;
            try(ResultSet rsFindCategory = findCategoryPs.getResultSet()) {
                if (rsFindCategory.next()) {
                    categoryId = rsFindCategory.getObject(1, UUID.class);
                }
                else {
                    CreateCategoryPs.setString(1, spend.getCategory().getName());
                    CreateCategoryPs.setString(2, spend.getCategory().getUsername());
                    CreateCategoryPs.setBoolean(3, spend.getCategory().isArchived());
                    CreateCategoryPs.executeUpdate();
                    ResultSet rs = CreateCategoryPs.getResultSet();
                    if (rs.next()) {
                        categoryId = rs.getObject(1, UUID.class);
                    }
                }
            }

            CreateSpendPs.setString(1, spend.getUsername());
            CreateSpendPs.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            CreateSpendPs.setString(3, spend.getCurrency().name());
            CreateSpendPs.setDouble(4, spend.getAmount());
            CreateSpendPs.setString(5, spend.getDescription());
            CreateSpendPs.setObject(6, categoryId);
            CreateSpendPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = CreateSpendPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
                spend.setId(generatedKey);
                return spend;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s join category c on s.category_id = c.id where s.id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity ce = new CategoryEntity();
                    ce.setName(rs.getString("name"));
                    ce.setId(rs.getObject("id", UUID.class));
                    se.setCategory(ce);
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s join category c on s.category_id = c.id  WHERE s.username = ?")) {
            ps.setObject(1, username);
            ps.execute();
            List<SpendEntity> spendEntities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity ce = new CategoryEntity();
                    ce.setName(rs.getString("name"));
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setCategory(ce);
                    spendEntities.add(se);
                }
            }
            return spendEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement("DELETE FROM spend WHERE id = ?")) {
            ps.setObject(1, spend.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement("SELECT * FROM spend s join category c on s.category_id = c.id  ")) {
            ps.execute();

            List<SpendEntity> spendEntities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity ce = new CategoryEntity();
                    ce.setName(rs.getString("name"));
                    ce.setId(rs.getObject("category_id", UUID.class));
                    se.setCategory(ce);
                    spendEntities.add(se);
                }
            }
            return spendEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
