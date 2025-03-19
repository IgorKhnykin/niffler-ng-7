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
import guru.qa.niffler.service.impl.GatewayApiClientV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Date;

@RestTest
public class ApiTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExt = ApiLoginExtension.restApiLoginExtension();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
    private final GatewayApiClientV2 gatewayApiClientV2 = new GatewayApiClientV2();

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
    @DisplayName("Запрос на дружбу должен отправляться")
    void addFriend(@Token String token, UserJson user) {
        gatewayApiClient.addFriend(user,1,  token);
    }

    @Test
    @ApiLogin
    @User
    void getUsers(@Token String token) {
        gatewayApiClientV2.findAllUsers(token, 0, null, null);
    }

    @Test
    @ApiLogin
    @User
    void getSpends(@Token String token) {
        gatewayApiClientV2.getAllSpends(token,0, null);
    }

    @Test
    @ApiLogin
    @User(withFriend = 2, incomeRequest = 2)
    void getAllFriends(@Token String token) {
        gatewayApiClientV2.findAllFriends(token, 0, null, null);
    }

    @Test
    @ApiLogin
    @User(withFriend = 1, incomeRequest = 1)
    void findFriends(UserJson user, @Token String token) {
        final String expectedFriend = user.testData().friends().getFirst();
        final String expectedIncomeRequest = user.testData().incomeRequests().getFirst();
        final String actualFriend = gatewayApiClient.findAllFriends(token).getLast().username();
        final String actualIncomeRequest = gatewayApiClient.findAllFriends(token).getFirst().username();
        Assertions.assertEquals(expectedFriend, actualFriend);
        Assertions.assertEquals(expectedIncomeRequest, actualIncomeRequest);
    }
}
