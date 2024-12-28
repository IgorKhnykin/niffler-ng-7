package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendsWebTest {

    @Test
    @DisplayName("Проверка присутствия друга в таблице друзей")
    void friendShouldBePresentInFriendsTable(@UserType(UserType.Type.WITH_FRIEND)UserQueueExtension.StaticUser user) {
            LoginPage.open()
                    .inputUsernameAndPassword(user.username(), user.password())
                    .clickLoginBtn()
                    .checkMainPageEssentialInfo()
                    .openFriends()
                    .checkFriendExist(user.friend());
    }

    @Test
    @DisplayName("Проверка пустой таблицы друзей для пустого пользователя")
    void friendSTableShouldBeEmptyForNewUser(@UserType(UserType.Type.EMPTY)UserQueueExtension.StaticUser user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openFriends()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @DisplayName("Проверка присутствия входящего запроса в друзья")
    void incomeInvitationBePresentInFriendsTable(@UserType(UserType.Type.WITH_INCOME_REQUEST)UserQueueExtension.StaticUser user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openFriends()
                .checkIncomeRequestExist(user.income());
    }

    @Test
    @DisplayName("Проверка присутствия исходящего запроса в друзья")
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(UserType.Type.WITH_OUTCOME_REQUEST)UserQueueExtension.StaticUser user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openAllPeople()
                .checkOutcomeInvitationInPeopleList(user.outcome());
    }

}
