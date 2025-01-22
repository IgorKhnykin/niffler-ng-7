package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> categoryEntity = categoryDao.findCategoryByUsernameAndCategoryName(spend.username(), spend.category().name());
            CategoryEntity ce = categoryEntity.orElseGet(() -> categoryDao.create(CategoryEntity.fromJson(spend.category())));
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            spendEntity.setCategory(ce);
            return SpendJson.fromEntity(spendDao.create(spendEntity));
        });
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
            Optional<SpendEntity> se = spendDao.findSpendById(id);
            return se.map(SpendJson::fromEntity);
        });
    }

    public List<SpendJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() -> spendDao.findAllByUsername(username).stream()
                .map(SpendJson::fromEntity)
                .toList()
        );
    }

    public void deleteSpend(SpendJson spendJson) {
        jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            spendDao.deleteSpend(spendEntity);
            return null;
        });
    }

    public List<SpendJson> findAll() {
        return jdbcTxTemplate.execute(() -> spendDao.findAll().stream()
                .map(SpendJson::fromEntity)
                .toList());
    }
}
