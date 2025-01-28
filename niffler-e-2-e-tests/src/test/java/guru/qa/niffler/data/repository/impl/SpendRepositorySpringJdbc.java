package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
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
    public SpendEntity create(SpendEntity spend) {
        Optional<CategoryEntity> category = categoryDao.findCategoryByUsernameAndCategoryName(spend.getUsername(), spend.getCategory().getName());
        if (category.isPresent()) {
            return spendDao.create(spend);
        }
        else {
            categoryDao.create(spend.getCategory());
            return spendDao.create(spend);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        SpendEntity spendEntity = jdbcTemplate.query("SELECT * FROM spend s join category c on s.category_id = c.id where s.id = ?", SpendEntityExtractor.instance, id);
        return Optional.ofNullable(spendEntity);
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query("SELECT * FROM spend s join category c on s.category_id = c.id where s.username = ?", SpendEntityListExtractor.instance, username);
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        spendDao.deleteSpend(spend);
        categoryDao.deleteCategory(spend.getCategory());
    }

    @Override
    public List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query("SELECT * FROM spend s join category c on s.category_id = c.id", SpendEntityListExtractor.instance);
    }
}
