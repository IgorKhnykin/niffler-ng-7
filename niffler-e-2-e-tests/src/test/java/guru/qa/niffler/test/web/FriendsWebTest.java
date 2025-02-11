package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

    @Test
    @User(withFriend = 1)
    @DisplayName("Проверка присутствия друга в таблице друзей")
    void friendShouldBePresentInFriendsTable(UserJson user) {
            LoginPage.open()
                    .inputUsernameAndPassword(user.username(), user.testData().password())
                    .clickLoginBtn()
                    .checkMainPageEssentialInfo()
                    .openFriends()
                    .checkFriendExist(user.testData().friends());
    }

    @Test
    @User
    @DisplayName("Проверка пустой таблицы друзей для пустого пользователя")
    void friendSTableShouldBeEmptyForNewUser(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openFriends()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @User(incomeRequest = 1)
    @DisplayName("Проверка присутствия входящего запроса в друзья")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openFriends()
                .checkIncomeRequestExist(user.testData().incomeRequests());
    }

    @Test
    @User(outcomeRequest = 1)
    @DisplayName("Проверка присутствия исходящего запроса в друзья")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openAllPeople()
                .checkOutcomeInvitationInPeopleList(user.testData().outcomeRequests());
    }
}
