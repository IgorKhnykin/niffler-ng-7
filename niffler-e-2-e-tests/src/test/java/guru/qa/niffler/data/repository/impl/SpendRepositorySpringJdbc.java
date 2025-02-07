package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.extractor.SpendEntityListExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final SpendDao spendDao = new SpendDaoSpringJdbc();

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
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
    public SpendEntity updateSpend(SpendEntity spend) {
        spendDao.update(spend);
        categoryDao.update(spend.getCategory());
        return spend;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        SpendEntity spendEntity = jdbcTemplate.query("SELECT * FROM spend s join category c on s.category_id = c.id where s.id = ?", SpendEntityExtractor.instance, id);
        return Optional.ofNullable(spendEntity);
    }

    @Override
    public List<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<SpendEntity> spendEntityList = jdbcTemplate.query(
                "SELECT * FROM spend s join category c on s.category_id = c.id where s.username = ? and s.description = ?",
                SpendEntityListExtractor.instance, username, spendDescription);
        return spendEntityList;
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<SpendEntity> spendEntityList = jdbcTemplate.query(
                "SELECT * FROM spend s join category c on s.category_id = c.id where c.name = ?",
                SpendEntityListExtractor.instance, category.getName());

        spendEntityList.forEach(spendDao::deleteSpend);
        categoryDao.deleteCategory(category);
    }
}
