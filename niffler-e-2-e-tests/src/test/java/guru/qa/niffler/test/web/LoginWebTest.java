package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.*;
import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;
import static guru.qa.niffler.utils.SelenideUtils.firefoxConfig;

public class LoginWebTest {

    private final BrowserExtension extension = new BrowserExtension();

    private final SelenideDriver driver = new SelenideDriver(chromeConfig);

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        extension.drivers.add(driver);

        LoginPage.open(driver).clickCreateNewAccountBtn()
                .setUsername(randomUsername())
                .setPassword(passwordMain)
                .setPasswordSubmit(passwordMain)
                .submitRegistration()
                .checkSuccessfulRegistrationMessage()
                .clickSingInButton()
                .inputUsernameAndPassword(randomUsername(), passwordMain)
                .clickLoginBtn();
    }

    @Test
    @DisplayName("Неуспешная регистрация по существующему имени пользователя")
    void shouldNotRegisterWithExistingUsername() {
        extension.drivers.add(driver);

        LoginPage.open(driver)
                .clickCreateNewAccountBtn()
                .setUsername(usernameMain)
                .setPassword(passwordMain)
                .setPasswordSubmit(passwordMain)
                .submitRegistration()
                .checkUsernameAlreadyExistMessage(usernameMain);
    }

    @Test
    @DisplayName("Неуспешная регистрация, если пароль при подтверждении не совпадает")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        extension.drivers.add(driver);

        LoginPage.open(driver)
                .clickCreateNewAccountBtn()
                .setUsername(randomUsername())
                .setPassword(passwordMain)
                .setPasswordSubmit("12345")
                .submitRegistration()
                .checkPasswordNotEqualsError();
    }

    @User(
            categories = {
                    @Category(categoryName = "Магазины", archived = false),
                    @Category(categoryName = "Бары", archived = true)
            },
            spendings = {
                    @Spending(category = "Обучение", description = "QA guru", amount = 80000)
            })
    @Test
    @DisplayName("Проверка отображения основных компонентов основной страницы после успешной регистрации")
    void mainPageShouldBeDisplayedAfterSuccessfulLogin(UserJson user) {
        extension.drivers.add(driver);

        LoginPage.open(driver)
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn();
    }

    @Test
    @DisplayName("Проверка появления ошибки при неверных данных пользователя")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        extension.drivers.add(driver);

        LoginPage.open(driver)
                .inputUsernameAndPassword(usernameMain, "123456")
                .clickLoginBtn();
        new LoginPage(driver).checkIncorrectCredentialsError();
    }
}
