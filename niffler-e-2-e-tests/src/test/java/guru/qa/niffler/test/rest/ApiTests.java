package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Date;

@RestTest
public class ApiTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExt = ApiLoginExtension.restApiLoginExtension();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @ApiLogin
    @User(categories = @Category(
            categoryName = "new category",
            archived = false)
    )
    void createSpend(@Token String token) {
        SpendJson spendJson = gatewayApiClient.createSpend(new SpendJson(
                null,
                new Date(),
                gatewayApiClient.findCategoryByCategoryName("new category", token),
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
    void sendInvitation(@Token String token, UserJson user) {
        gatewayApiClient.sendInvitation(user,1,  token);
    }

    @Test
    @ApiLogin
    @User
    void addFriend(@Token String token, UserJson user) {
        gatewayApiClient.addFriend(user,1,  token);
    }

    @Test
    @ApiLogin
    @User
    void getUsers(@Token String token) {
        gatewayApiClient.findAllUsers(token);
    }


}
