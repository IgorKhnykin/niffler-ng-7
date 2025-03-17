package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UserApiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class ApiTest {
    
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @ApiLogin
    @User
    void createSpend(@Token String token) {
        SpendJson spendJson = gatewayApiClient.createSpend(new SpendJson(
                null,
                new Date(),
                gatewayApiClient.findCategoryByUsernameAndCategoryName("igorKhn", "new category"),
                CurrencyValues.EUR,
                322.0,
                "test spend",
                "igorKhn"
        ), token);
        gatewayApiClient.deleteSpend(spendJson, token);
    }

    @Test
    @ApiLogin
    @User
    void createCategory(@Token String token) {
        CategoryJson category = gatewayApiClient.createCategory(new CategoryJson(null,
                "new category11",
                "igorKhn",
                false), token);
        gatewayApiClient.updateCategory(new CategoryJson(category.id(), category.name(), category.username(), true), token);
    }

    @Test
    @ApiLogin
    @User
    void getUsers(@Token String token) {
        gatewayApiClient.findAllUsers(token);
    }
}
