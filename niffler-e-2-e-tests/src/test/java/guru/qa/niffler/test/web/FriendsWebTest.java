package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.UserApiClient;
import guru.qa.niffler.service.impl.UserDbClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@WebTest
public class FriendsWebTest {

    @Test
    @User(withFriend = 2)
    @ApiLogin()
    @DisplayName("Проверка присутствия друга в таблице друзей")
    void friendShouldBePresentInFriendsTableTest(UserJson user) {
            FriendsPage.open()
                    .checkFriendExist(user.testData().friends());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Проверка пустой таблицы друзей для пустого пользователя")
    void friendSTableShouldBeEmptyForNewUserTest(UserJson user) {
        FriendsPage.open()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @ApiLogin
    @User(incomeRequest = 1)
    @DisplayName("Проверка присутствия входящего запроса в друзья")
    void incomeInvitationBePresentInFriendsTableTest(UserJson user) {
        FriendsPage.open()
                .checkIncomeRequestExist(user.testData().incomeRequests());
    }

    @Test
    @ApiLogin
    @User(outcomeRequest = 1)
    @DisplayName("Проверка присутствия исходящего запроса в друзья")
    void outcomeInvitationBePresentInAllPeoplesTableTest(UserJson user) {
        AllPeoplePage.open()
                .checkOutcomeInvitationInPeopleList(user.testData().outcomeRequests());
    }

    @Test
    @ApiLogin
    @User(incomeRequest = 1)
    @DisplayName("Проверка возможности приема запроса в друзья")
    void acceptIncomeRequestTableTest(UserJson user) {
        FriendsPage.open()
                .acceptIncomeRequest(user.testData().incomeRequests().get(0))
                .checkNotification("Invitation of %s accepted".formatted(user.testData().incomeRequests().get(0)));
    }

    @Test
    @ApiLogin
    @User(incomeRequest = 1)
    @DisplayName("Проверка возможности отклонения запроса в друзья")
    void declineIncomeRequestTableTest(UserJson user) {
        FriendsPage.open()
                .declineIncomeRequest(user.testData().incomeRequests().get(0))
                .checkNotification("Invitation of %s is declined".formatted(user.testData().incomeRequests().get(0)));
    }
}
