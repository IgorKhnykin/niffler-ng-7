package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    final guru.qa.niffler.api.SpendApiClient spendApi = new guru.qa.niffler.api.SpendApiClient();

    @Override
    @Step("Создание траты через API")
    public @Nullable SpendJson createSpend(SpendJson spendJson) {
        return spendApi.createSpend(spendJson);
    }

    @Override
    @Step("Найти трату через API")
    public @Nullable SpendJson findSpend(SpendJson spendJson) {
        return spendApi.getSpend(spendJson.id().toString(), spendJson.username());
    }

    @Override
    @Step("Найти трату по имени {username} и описанию {description} через API")
    public @Nonnull List<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        return spendApi.getSpends(username).stream()
                .filter(spend -> spend.description().contains(description))
                .toList();
    }

    @Override
    @Step("Удалить трату через API")
    public void deleteSpend(SpendJson spendJson) {
        spendApi.deleteSpends(spendJson.username(), List.of(spendJson.id().toString()));
    }

    @Override
    @Step("Создание категории через API")
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson) {
        return spendApi.createCategory(categoryJson);
    }

    @Override
    @Step("Поиск категории по имени пользователя {username} и названию {categoryName} категории через API")
    public @Nullable CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return spendApi.getCategories(username, false).stream()
                .filter(category -> category.name().contains(categoryName))
                .findFirst().orElseGet(null);
    }

    @Override
    @Step("Удаление категории через API")
    public void deleteCategory(CategoryJson categoryJson) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Step("Обновить данные траты через API")
    public @Nullable SpendJson updateSpend(SpendJson spendJson) {
        return spendApi.editSpend(spendJson);
    }

    @Override
    @Step("Обновить данные категории через API")
    public @Nullable CategoryJson updateCategory(CategoryJson categoryJson) {
        return spendApi.updateCategory(categoryJson);
    }
}
