package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;

public class SpendDbClient implements SpendClient {

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.spendJdbcUrl());

    private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendJson createSpend(SpendJson spendJson) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
        });
    }

    @Override
    public SpendJson findSpend(SpendJson spendJson) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            Optional<SpendEntity> spendFromDb = (spendRepository.findSpendById(spendEntity.getId()));
            return spendFromDb.map(SpendJson::fromEntity).orElse(null);
        });
    }

    @Override
    public List<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        return xaTransactionTemplate.execute(() -> {
            List<SpendEntity> spendFromDb = (spendRepository.findByUsernameAndSpendDescription(username, description));
            return spendFromDb.stream().map(SpendJson::fromEntity).toList();
        });
    }

    @Override
    public void deleteSpend(SpendJson spendJson) {
        xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            spendRepository.removeSpend(spendEntity);
            return null;
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
        });
    }

    @Override
    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return xaTransactionTemplate.execute(() -> {
            Optional<CategoryEntity> categoryFromDb = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
            return categoryFromDb.map(CategoryJson::fromEntity).orElse(null);
        });
    }

    @Override
    public void deleteCategory(CategoryJson categoryJson) {
        xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            spendRepository.removeCategory(categoryEntity);
            return null;
        });
    }

    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return SpendJson.fromEntity(xaTransactionTemplate.execute(
                () -> spendRepository.updateSpend(SpendEntity.fromJson(spendJson))));
    }

    @Override
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        return CategoryJson.fromEntity(xaTransactionTemplate.execute(
                () -> spendRepository.updateCategory(CategoryEntity.fromJson(categoryJson))));
    }
}
