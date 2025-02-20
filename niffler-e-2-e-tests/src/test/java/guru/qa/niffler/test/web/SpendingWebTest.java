package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

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
                .save()
                .checkNotification("Spending is edited successfully");

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
                .save()
                .checkNotification("New spending is successfully created");

        MainPage.initPage().checkThatTableContainsSpending(spendingDescription);
    }

    @Test
    @DisplayName("Обновление описания траты")
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

    @DisplayName("Проверка статистики через скриншотный тест")
    @ScreenshotTest("img/expected-stat.png")
    @User(spendings = @Spending(
            category = "Обучение",
            description = "new description",
            amount = 79990.0))
    void checkStatTest(UserJson user, BufferedImage expected) throws IOException {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn();
        Selenide.sleep(2000);
        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());

        Assertions.assertFalse(new ScreenResult(expected, actual));
    }
 }

