package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDbClient {

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private static final Config CFG = Config.getInstance();

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
            CategoryEntity ce = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(categoryDao.create(ce));
        });
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> ce = categoryDao.findCategoryById(id);
            return ce.map(CategoryJson::fromEntity);
        });
    }

    public List<CategoryJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() ->
            categoryDao.findAllByUsername(username)
                    .stream()
                    .map(CategoryJson::fromEntity)
                    .toList()
        );
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> ce = categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
            return ce.map(CategoryJson::fromEntity);
        });
    }

    public void deleteCategory(CategoryJson categoryJson) {
        jdbcTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            categoryDao.deleteCategory(categoryEntity);
            return null;
        });
    }

    public List<CategoryJson> findAll() {
        return jdbcTxTemplate.execute(() ->
            categoryDao.findAll().stream()
                    .map(CategoryJson::fromEntity)
                    .toList()
        );

    }
}
