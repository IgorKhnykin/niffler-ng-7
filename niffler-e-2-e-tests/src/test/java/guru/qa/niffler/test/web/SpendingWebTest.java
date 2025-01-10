package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.passwordMain;
import static guru.qa.niffler.utils.RandomDataUtils.usernameMain;

@WebTest
public class SpendingWebTest {

    @Test
    @DisplayName("Обновление описания траты")
    @User(username = usernameMain,
            spending = @Spending(
                    category = "Обучение",
                    description = "new description",
                    amount = 845444.0))
    void categoryDescriptionShouldBeEditedByCategoryAction(SpendJson spend) {
        final String newSpendingName = "Машина edited";
        LoginPage.open()
                .inputUsernameAndPassword(spend.username(), passwordMain)
                .clickLoginBtn()
                .editSpending(spend.description())
                .editSpendingDescription(newSpendingName)
                .save();

        MainPage.initPage().checkThatTableContainsSpending(newSpendingName);
    }
}

