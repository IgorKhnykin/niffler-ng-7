package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;

public interface SpendClient {

    SpendJson createSpend(SpendJson spendJson);

    SpendJson findSpend(SpendJson spendJson);

    List<SpendJson> findSpendByUsernameAndDescription(String username, String description);

    void deleteSpend(SpendJson spendJson);

    CategoryJson createCategory(CategoryJson categoryJson);

    CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName);

    void deleteCategory(CategoryJson categoryJson);

    SpendJson updateSpend(SpendJson spendJson);

    CategoryJson updateCategory(CategoryJson categoryJson);
}
