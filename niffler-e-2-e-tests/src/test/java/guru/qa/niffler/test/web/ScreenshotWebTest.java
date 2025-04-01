package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.service.impl.SpendApiClient;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@WebTest
public class ScreenshotWebTest {

    @DisplayName("Проверка изменения статистики при добавлении траты")
    @ScreenshotTest("img/expected-stat-after-adding-spend.png")
    @ApiLogin
    @User(spendings = @Spending(
            category = "Обучение",
            description = "new description",
            amount = 79990.0))
    void checkStatAfterAddingSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        MainPage.open()
                .makeStatisticScreenshot(expected)
                .checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка изменения статистики после изменения траты")
    @ScreenshotTest(value = "img/expected-stat-after-editing-spend.png")
    @ApiLogin
    @User(spendings = @Spending(
            category = "Обучение",
            description = "new description",
            amount = 79990.0))
    void checkStatAfterEditingTest(UserJson user, BufferedImage expected) throws IOException {
        SpendJson spend = user.testData().spends().get(0);
        SpendJson updatedSpend = new SpendApiClient().updateSpend(
                new SpendJson(spend.id(),
                        spend.spendDate(),
                        spend.category(),
                        spend.currency(),
                        228.0,
                        spend.description(),
                        spend.username()));

        MainPage.open()
                .makeStatisticScreenshot(expected)
                .checkStatisticBubbles(List.of(updatedSpend));
    }

    @DisplayName("Проверка изменения статистики при добавлении нескольких трат")
    @ScreenshotTest("img/expected-stat-after-adding-several-spends.png")
    @ApiLogin
    @User(spendings =
            {@Spending(category = "Обучение",
                    description = "new description",
                    amount = 79990.0),
                    @Spending(category = "Обучение-new",
                            description = "new description",
                            amount = 54300.0)})
    void checkStatAfterAddingSeveralSpendingsTest(UserJson user, BufferedImage expected) throws IOException {
        MainPage.open()
                .makeStatisticScreenshot(expected)
                .checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка изменения статистики после удаления траты")
    @ScreenshotTest("img/expected-stat-after-deleting-spend.png")
    @ApiLogin
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

        MainPage.open()
                .makeStatisticScreenshot(expected)
                .checkStatisticBubbles(user.testData().spends());
    }

    @DisplayName("Проверка добавления аватарки")
    @ScreenshotTest(value = "img/expected-new-avatar.png")
    @ApiLogin
    @User()
    void checkAddingNewAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        ProfilePage.open()
                .changeProfilePicture("expected-new-avatar.png")
                .makeAvatarScreenshot(expected);
    }

    @DisplayName("Проверка добавления аватарки в header")
    @ScreenshotTest("img/expected-new-avatar-in-header.png")
    @ApiLogin
    @User()
    void checkAddingNewAvatarInHeaderTest(BufferedImage expected) throws IOException {
        ProfilePage.open()
                .changeProfilePicture("expected-new-avatar.png")
                .makeAvatarScreenshotInHeader(expected);
    }
}
