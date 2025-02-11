package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;

public class SpendApiClient implements SpendClient {

    final guru.qa.niffler.api.SpendApiClient spendApi = new guru.qa.niffler.api.SpendApiClient();

    @Override
    public SpendJson createSpend(SpendJson spendJson) {
        return spendApi.createSpend(spendJson);
    }

    @Override
    public SpendJson findSpend(SpendJson spendJson) {
        return spendApi.getSpend(spendJson.id().toString(), spendJson.username());
    }

    @Override
    public List<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        return spendApi.getSpends(username).stream()
                .filter(spend -> spend.description().contains(description))
                .toList();
    }

    @Override
    public void deleteSpend(SpendJson spendJson) {
        spendApi.deleteSpends(spendJson.username(), List.of(spendJson.id().toString()));
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return spendApi.createCategory(categoryJson);
    }

    @Override
    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return spendApi.getCategories(username, false).stream()
                .filter(category -> category.name().contains(categoryName))
                .findFirst().orElse(null);
    }

    @Override
    public void deleteCategory(CategoryJson categoryJson) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return spendApi.editSpend(spendJson);
    }

    @Override
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        return spendApi.updateCategory(categoryJson);
    }
}
