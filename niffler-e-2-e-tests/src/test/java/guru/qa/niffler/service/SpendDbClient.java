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
import io.qameta.allure.Step;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.spendJdbcUrl());

    private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();

    private static final Config CFG = Config.getInstance();

    @Override
    @Step("Создание траты через базу данных")
    public @Nullable SpendJson createSpend(SpendJson spendJson) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
        });
    }

    @Override
    @Step("Найти трату через базу данных")
    public @Nullable SpendJson findSpend(SpendJson spendJson) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            Optional<SpendEntity> spendFromDb = (spendRepository.findSpendById(spendEntity.getId()));
            return spendFromDb.map(SpendJson::fromEntity).orElse(null);
        });
    }

    @Override
    @Step("Найти трату по имени пользователя {username} и описанию {description} через базу данных")
    public @Nullable List<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        return xaTransactionTemplate.execute(() -> {
            List<SpendEntity> spendFromDb = (spendRepository.findByUsernameAndSpendDescription(username, description));
            return spendFromDb.stream().map(SpendJson::fromEntity).toList();
        });
    }

    @Override
    @Step("Удалить трату через базу данных")
    public void deleteSpend(SpendJson spendJson) {
        xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            spendRepository.removeSpend(spendEntity);
            return null;
        });
    }

    @Override
    @Step("Создать категорию через базу данных")
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson) {
        return xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
        });
    }

    @Override
    @Step("Найти категорию по имени пользователя {username} и имени категории {categoryName} через базу данных")
    public @Nullable CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return xaTransactionTemplate.execute(() -> {
            Optional<CategoryEntity> categoryFromDb = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
            return categoryFromDb.map(CategoryJson::fromEntity).orElse(null);
        });
    }

    @Override
    @Step("Удалить категорию через базу данных")
    public void deleteCategory(CategoryJson categoryJson) {
        xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            spendRepository.removeCategory(categoryEntity);
            return null;
        });
    }

    @Override
    @Step("Обновить данные траты через базу данных")
    public @Nullable SpendJson updateSpend(SpendJson spendJson) {
        return xaTransactionTemplate.execute(
                () -> SpendJson.fromEntity(spendRepository.updateSpend(SpendEntity.fromJson(spendJson))));
    }

    @Override
    @Step("Обновить данные категории через базу данных")
    public @Nullable CategoryJson updateCategory(CategoryJson categoryJson) {
        return xaTransactionTemplate.execute(
                () -> CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(categoryJson))));
    }
}
