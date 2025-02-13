package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.passwordMain;

@WebTest
public class SpendingWebTest {

    @Test
    @DisplayName("Обновление описания траты")
    @User(spendings = @Spending(
                    category = "Обучение4",
                    description = "new description",
                    amount = 841114.0))
    void categoryDescriptionShouldBeEditedByCategoryActionTest(UserJson user) {
        final String newSpendingName = "Маши1111111а edited";
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .openEditSpendingPage(user.testData().spends().get(0).description())
                .editSpendingDescription(newSpendingName)
                .save();

        MainPage.initPage().checkThatTableContainsSpending(newSpendingName);
    }

    @Test
    @DisplayName("Проверка добавления нового спендинга через UI")
    @User()
    void addNewSpendingThroughUITest(UserJson user) {
        final String spendingDescription = "New spending";
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .createNewSpend()
                .editSpendingAmount("2000")
                .editSpendingCategory("New Category")
                .editSpendingDate(new Date())
                .editSpendingDescription(spendingDescription)
                .save();

        MainPage.initPage().checkThatTableContainsSpending(spendingDescription);
    }
}

