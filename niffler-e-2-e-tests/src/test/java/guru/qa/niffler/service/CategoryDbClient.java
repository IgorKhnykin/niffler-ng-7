package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.DataBases.transaction;

public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();

    public CategoryJson createCategory(CategoryJson category) {

        return transaction(connection -> {
            CategoryEntity ce = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(ce));
        }, CFG.spendJdbcUrl(), 2);
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return transaction(connection -> {
            Optional<CategoryEntity> ce = new CategoryDaoJdbc(connection).findCategoryById(id);
            return ce.map(CategoryJson::fromEntity);
        }, CFG.spendJdbcUrl(), 2);
    }

    public List<CategoryJson> findAllByUsername(String username) {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).findAllByUsername(username)
                    .stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        }, CFG.spendJdbcUrl(), 2);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(connection -> {
            Optional<CategoryEntity> ce = new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName);
            return ce.map(CategoryJson::fromEntity);
        }, CFG.spendJdbcUrl(), 2);
    }

    public void deleteCategory(CategoryJson categoryJson) {
        transaction(connection -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            new CategoryDaoJdbc(connection).deleteCategory(categoryEntity);
        }, CFG.spendJdbcUrl(), 2);


    }
}
