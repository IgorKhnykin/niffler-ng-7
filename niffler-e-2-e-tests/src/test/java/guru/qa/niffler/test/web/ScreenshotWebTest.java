package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.utils.ScreenResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ScreenshotWebTest {

    @DisplayName("Проверка изменения статистики при добавлении траты")
    @ScreenshotTest("img/expected-stat-after-adding-spend.png")
    @User(spendings = @Spending(
            category = "Обучение",
            description = "new description",
            amount = 79990.0))
    void checkStatAfterAddingSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .makeStatisticScreenshot();

        Assertions.assertFalse(new ScreenResult(expected, actual));

        new MainPage().checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка изменения статистики после изменения траты")
    @ScreenshotTest(value = "img/expected-stat-after-editing-spend.png")
    @User(spendings = @Spending(
            category = "Обучение",
            description = "new description",
            amount = 79990.0))
    void checkStatAfterEditingTest(UserJson user, BufferedImage expected) throws IOException {
        SpendJson spend = user.testData().spends().get(0);
        new SpendApiClient().updateSpend(
                new SpendJson(spend.id(),
                spend.spendDate(),
                spend.category(),
                spend.currency(),
                228.0,
                spend.description(),
                spend.username()));

        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .makeStatisticScreenshot();

        Assertions.assertFalse(new ScreenResult(expected, actual));
        new MainPage().checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка изменения статистики при добавлении нескольких трат")
    @ScreenshotTest(value = "img/expected-stat-after-adding-several-spends.png")
    @User(spendings =
            {@Spending(category = "Обучение",
                    description = "new description",
                    amount = 79990.0),
            @Spending(category = "Обучение-new",
                    description = "new description",
                    amount = 54300.0)})
    void checkStatAfterAddingSeveralSpendingsTest(UserJson user, BufferedImage expected) throws IOException {
        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .makeStatisticScreenshot();

        Assertions.assertFalse(new ScreenResult(expected, actual));

        new MainPage().checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка изменения статистики после удаления траты")
    @ScreenshotTest(value = "img/expected-stat-after-deleting-spend.png", rewriteExpected = true)
    @User(spendings =
            {@Spending(category = "Обучение",
                    description = "new description",
                    amount = 79990.0),
            @Spending(category = "Обучение-new",
                    description = "new description",
                    amount = 54300.0)})
    void checkStatAfterDeletingTest(UserJson user, BufferedImage expected) throws IOException {
        List<SpendJson> createdSpends = user.testData().spends();
        new SpendApiClient().deleteSpend(createdSpends.get(0));
        createdSpends.remove(createdSpends.get(0));

        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .makeStatisticScreenshot();

        Assertions.assertFalse(new ScreenResult(expected, actual));
        new MainPage().checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка добавления аватарки")
    @ScreenshotTest(value = "img/expected-new-avatar.png")
    @User()
    void checkAddingNewAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .openProfile()
                .changeProfilePicture("expected-new-avatar.png")
                .makeAvatarScreenshot();

        Assertions.assertFalse(new ScreenResult(expected, actual));
    }

    @DisplayName("Проверка добавления аватарки в header")
    @ScreenshotTest(value = "img/expected-new-avatar-in-header.png")
    @User()
    void checkAddingNewAvatarInHeaderTest(UserJson user, BufferedImage expected) throws IOException {
        BufferedImage actual = LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .openProfile()
                .changeProfilePicture("expected-new-avatar.png")
                .makeAvatarScreenshotInHeader();

        Assertions.assertFalse(new ScreenResult(expected, actual));
    }
}
