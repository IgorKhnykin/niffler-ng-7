package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

@WebTest
public class SpendingWebTest {

    @Test
    @DisplayName("Обновление описания траты")
    @User(spendings = {
            @Spending(
                    category = "Обучение4",
                    description = "new description",
                    amount = 841114.0),
            @Spending(
                    category = "Обучение4",
                    description = "new description",
                    amount = 841114.0)})
    void categoryDescriptionShouldBeEditedByCategoryActionTest(UserJson user) {
        final SpendJson spend1 = user.testData().spends().get(0);
        final SpendJson spend2 = user.testData().spends().get(1);
        final SpendJson spendWithNewDesc = initNewSpend(user, spend1.category().name(), "new Spending Description", spend1.amount());

        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .openEditSpendingPage(spend1.description())
                .editSpendingDescription(spendWithNewDesc.description())
                .save()
                .checkNotification("Spending is edited successfully");

        MainPage.initPage().checkThatTableContainsSpending(spend2, spendWithNewDesc);
    }

    @Test
    @DisplayName("Проверка добавления нового спендинга через UI")
    @User()
    void addNewSpendingThroughUITest(UserJson user) {
        SpendJson spend = initNewSpend(user, "new Category", "new description", 2000.0);
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .createNewSpend()
                .editSpendingAmount(spend.amount().toString())
                .editSpendingCategory(spend.category().name())
                .editSpendingDate(spend.spendDate())
                .editSpendingDescription(spend.description())
                .save()
                .checkNotification("New spending is successfully created");

        MainPage.initPage().checkThatTableContainsSpending(spend);
    }

    @Test
    @DisplayName("Проверка удаления траты")
    @User(spendings = @Spending(
            category = "Обучение4",
            description = "new description",
            amount = 841114.0))
    void deleteSpendingTestTest(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .deleteSpend(user.testData().spends().get(0).description())
                .checkNotification("Spendings succesfully deleted");
    }

    private SpendJson initNewSpend(UserJson user, String categoryName, String description, Double amount) {
        return new SpendJson(
                null,
                new Date(),
                new CategoryJson(null, categoryName, user.username(), false),
                CurrencyValues.RUB,
                amount,
                description,
                user.username());
    }
}

