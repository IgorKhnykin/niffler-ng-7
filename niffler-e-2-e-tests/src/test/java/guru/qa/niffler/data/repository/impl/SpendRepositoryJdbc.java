package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.extractor.SpendEntityListExtractor;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    SpendDao spendDao = new SpendDaoJdbc();

    CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendEntity createSpend(SpendEntity spend) { //+
        Optional<CategoryEntity> category = categoryDao.findCategoryByUsernameAndCategoryName(spend.getUsername(), spend.getCategory().getName());
        if (category.isPresent()) {
            spend.setCategory(category.get());
            return spendDao.create(spend);
        }
        else {
            categoryDao.create(spend.getCategory());
            return spendDao.create(spend);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        spendDao.update(spend);
        categoryDao.update(spend.getCategory());
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) { //+
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) { //+
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) { //+
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s join category c on s.category_id = c.id where s.id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                return Optional.ofNullable(SpendEntityExtractor.instance.extractData(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription) { //+
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s join category c on s.category_id = c.id where s.username = ? and description = ?")) {
            ps.setString(1, username);
            ps.setString(2, spendDescription);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                return SpendEntityListExtractor.instance.extractData(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) { //+
        spendDao.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) { //+
        List<SpendEntity> spendEntityList;
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s join category c on s.category_id = c.id where c.name = ?")) {
            ps.setString(1, category.getName());
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                spendEntityList = SpendEntityListExtractor.instance.extractData(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        spendEntityList.forEach(spendDao::deleteSpend);
        categoryDao.deleteCategory(category);
    }
}
