package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.SpendRepositorySpringJdbc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    static SpendRepository getInstance() {
        return switch (System.getProperty("repository.impl", "sjdbc")) {
            case "jpa" -> new SpendRepositoryHibernate();
            case "sjdbc" -> new SpendRepositorySpringJdbc();
            case "jdbc" -> new SpendRepositoryJdbc();
            default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
        };
    }

    SpendEntity createSpend(SpendEntity spend);

    SpendEntity updateSpend(SpendEntity spend);

    CategoryEntity updateCategory(CategoryEntity category);

    CategoryEntity createCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription);

    void removeSpend(SpendEntity spend);

    void removeCategory(CategoryEntity category);

    List<SpendEntity> findAllSpendsByUsername(String username);

    List<CategoryEntity> findAllCategoriesByUsername(String username);

}
