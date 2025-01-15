package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDbClient {

    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity ce = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(categoryDao.create(ce));
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        Optional<CategoryEntity> ce = categoryDao.findCategoryById(id);
        return ce.map(CategoryJson::fromEntity);
    }

    public List<CategoryJson> findAllByUsername(String username) {
        return categoryDao.findAllByUsername(username)
                .stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        Optional<CategoryEntity> ce = categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
        return ce.map(CategoryJson::fromEntity);
    }

    public void deleteCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        categoryDao.deleteCategory(categoryEntity);
    }
}
