package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.TestData.passwordMain;
import static guru.qa.niffler.utils.TestData.usernameMain;

@ExtendWith(BrowserExtension.class)
public class SpendingWebTest {

    @Test
    @DisplayName("Обновление описания траты")
    @Spending(
            category = "Обучение",
            description = "new description",
            username = usernameMain,
            amount = 845444.0)
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

