package guru.qa.niffler.test.web;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UserApiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

@Disabled
public class ApiTest {
    UserClient userApiClient = new UserApiClient();
    SpendClient spendClient = new SpendApiClient();

    @Test
    void test1() {
        UserJson userJson = userApiClient.createUser("api userTes6", "1234");
        userApiClient.addFriend(userJson, 1);
        System.out.println(userJson);
    }

    @Test
    void createSpend() {
        SpendJson spendJson = spendClient.createSpend(new SpendJson(
                null,
                new Date(),
                spendClient.findCategoryByUsernameAndCategoryName("igorKhn", "new category"),
                CurrencyValues.EUR,
                322.0,
                "test spend",
                "igorKhn"
        ));
        spendClient.deleteSpend(spendJson);
    }

    @Test
    void deleteSpend() {
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
        CategoryJson category = spendClient.createCategory(new CategoryJson(null,
                "new category11",
                "igorKhn",
                false));
        spendClient.updateCategory(new CategoryJson(category.id(), category.name(), category.username(), true));
    }

    @Test
    void getUsers() {
        userApiClient.findAllUsers("Igor");
    }
}
