package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;

import java.util.List;

public interface SpendClient {

    static SpendClient getInstance() {
        return "api".equals(System.getProperty("client.impl", "api")) ? new SpendApiClient() : new SpendDbClient();
    }

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
