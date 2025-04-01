package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.*;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayApiClientV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@RestTest
public class ApiTests {

    private final AuthApiClient authApiClient = new AuthApiClient();

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
    void getUsers(@Token String token) {
        gatewayApiClientV2.findAllUsers(token, 0, null, null);
    }

    @Test
    @ApiLogin
    @User
    void getSpends(@Token String token) {
        gatewayApiClientV2.getAllSpends(token,0, null);
    }

//    @Test
//    @ApiLogin
//    @User(withFriend = 2, incomeRequest = 2)
//    void getAllFriends(@Token String token) {
//        gatewayApiClientV2.findAllFriends(token, 0, null, null);
//    }

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

    @Test
    @ApiLogin(username = "gabriel.koss", password = "1234")
    void sendInvitation(@Token String token) {
        final UserJson userMain = new UserJson("Igor", new TestData("1234"));
        gatewayApiClient.sendInvitation(token, userMain);
    }

    @Test
    @ApiLogin(username = "Igor", password = "1234")
    void addFriend(@Token String token) {
        final UserJson userMain = new UserJson("allen.davis", new TestData("1234"));
        gatewayApiClient.sendInvitation(token, userMain);
    }

    @Test
    @ApiLogin
    @User(username = "Igor", withFriend = 1)
    @DisplayName("Проверка удаления друга")
    void deleteFriendTest(@Token String token, UserJson user) {
        final String addedFriend = user.testData().friends().getFirst();
        final List<UserJson> friendsAdded = gatewayApiClient.findAllFriends(token).stream()
                .filter(friend -> friend.username().equals(addedFriend))
                .toList();
        Assertions.assertEquals(1, friendsAdded.size());
        gatewayApiClient.removeFriend(token,  addedFriend);
        final List<UserJson> friendsRemoved = gatewayApiClient.findAllFriends(token).stream()
                .filter(friend -> friend.username().equals(addedFriend))
                .toList();
        Assertions.assertEquals(0, friendsRemoved.size());
    }

    @Test
    @ApiLogin
    @User(username = "Igor")
    @DisplayName("Проверка отклонения входящего запроса на дружбу")
    void declineInvitationTest(@Token String token, UserJson igorUser) {
        final UserJson userMain = new UserJson("allen.davis", new TestData("1234"));
        gatewayApiClient.sendInvitation(token, userMain);

        final String alenToken = authApiClient.login("allen.davis", "1234");
        final List<UserJson> invite = gatewayApiClient.findAllFriends(alenToken).stream()
                .filter(inv -> inv.username().equals(igorUser.username()))
                .toList();
        Assertions.assertEquals(1, invite.size());
        gatewayApiClient.declineFriendship(alenToken, igorUser);
        final List<UserJson> inviteDecl = gatewayApiClient.findAllFriends(alenToken).stream()
                .filter(inv -> inv.username().equals(igorUser.username()))
                .toList();
        Assertions.assertEquals(0, inviteDecl.size());
    }

    @Test
    @ApiLogin
    @User(username = "Igor", incomeRequest = 1)
    @DisplayName("Проверка принятия входящего запроса на дружбу")
    void acceptInvitationTest(@Token String token, UserJson user) {
        final String incomeReq = user.testData().incomeRequests().getFirst();
        final List<UserJson> incomeUser = gatewayApiClient.findAllFriends(token).stream()
                .filter(inc -> inc.username().equals(incomeReq))
                .toList();
        Assertions.assertEquals(1, incomeUser.size());
        gatewayApiClient.addFriend(token,  incomeUser.getFirst());
        final List<UserJson> friendsAdded = gatewayApiClient.findAllFriends(token).stream()
                .filter(friend -> friend.username().equals(incomeReq))
                .toList();
        Assertions.assertEquals(1, friendsAdded.size());
    }

    @Test
    @ApiLogin
    @User(outcomeRequest = 3, withFriend = 1, incomeRequest = 1)
    @DisplayName("Проверка получения списка пользователей с исходящими запросами о дружбе")
    void checkGetUserListWithIncomeFriendshipRequest(UserJson userCreated, @Token String token) {
        final List<UserJson> inviteSentUsers = gatewayApiClientV2.findAllUsers(token, 0, null, null).stream()
                .filter(user -> Objects.nonNull(user.friendshipStatus()))
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.INVITE_SENT))
                .toList();
        Assertions.assertEquals(3, inviteSentUsers.size());
    }

    @Test
    @ApiLogin
    @User(outcomeRequest = 1)
    @DisplayName("Проверка создания исходящего запроса и входящего запроса при отправке приглашения дружить")
    void checkIncomeAndOutcomeRequest(UserJson userCreated, @Token String token) {
        final List<UserJson> targetUser = gatewayApiClientV2.findAllUsers(token, 0, null, null).stream()
                .filter(user -> Objects.nonNull(user.friendshipStatus()))
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.INVITE_SENT))
                .toList();
        Assertions.assertEquals(1, targetUser.size());
        final String targetUserToken = authApiClient.login(targetUser.getFirst().username(), "1234");
        final List<UserJson> inviteReceivedUser = gatewayApiClientV2.findAllFriends(targetUserToken, 0, null, null).stream()
                .filter(user -> Objects.nonNull(user.friendshipStatus()))
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.INVITE_RECEIVED))
                .toList();
        Assertions.assertEquals(1, inviteReceivedUser.size());
    }
}
