package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserApiClient;
import guru.qa.niffler.service.UserClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class ApiTest {

    @Test
    void test1() {
        UserClient userApiClient = new UserApiClient();
        UserJson userJson = userApiClient.createUser("api user", "1234");
        System.out.println(userJson);
    }

    @Test
    void createSpend() {
        SpendClient spendClient = new SpendApiClient();
        SpendJson spendJson = spendClient.createSpend(new SpendJson(
                null,
                new Date(),
                spendClient.findCategoryByUsernameAndCategoryName("igorKhn", "new category"),
                CurrencyValues.EUR,
                220200000000.0,
                "test spend",
                "igorKhn"
        ));
        spendClient.deleteSpend(spendJson);
    }

    @Test
    void deleteSpend() {
        SpendClient spendClient = new SpendApiClient();
        SpendJson spendJson = spendClient.findSpend(new SpendJson(
                UUID.fromString("212fbd24-347d-4bcc-b6c7-219f4981a9f3"),
                new Date(),
                spendClient.findCategoryByUsernameAndCategoryName("igorKhn", "car"),
                CurrencyValues.EUR,
                220200000000.0,
                "test spend",
                "igorKhn"
        ));
        spendClient.deleteSpend(spendJson);
    }

    @Test
    void createCategory() {
        SpendClient spendClient = new SpendApiClient();
        CategoryJson category = spendClient.createCategory(new CategoryJson(null,
                "new category",
                "igorKhn",
                false));
        spendClient.updateCategory(new CategoryJson(category.id(), category.name(), category.username(), true));
    }

    @Test
    void getUsers() {
        UserClient userClient = new UserApiClient();
        userClient.findAllUsers();
    }
}
