package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.TestData.*;

@WebTest
public class LoginWebTest {

    @Test
    @DisableByIssue("3")
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        LoginPage.open()
                .clickCreateNewAccountBtn()
                .setUsername(randomUsername)
                .setPassword(passwordMain)
                .setPasswordSubmit(passwordMain)
                .submitRegistration()
                .checkSuccessfulRegistrationMessage()
                .clickSingInButton()
                .inputUsernameAndPassword(randomUsername, passwordMain)
                .clickLoginBtn();

        MainPage.initPage().checkMainPageEssentialInfo();
    }

    @Test
    @DisplayName("Неуспешная регистрация по существующему имени пользователя")
    void shouldNotRegisterWithExistingUsername() {
        LoginPage.open()
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
        LoginPage.open()
                .clickCreateNewAccountBtn()
                .setUsername(randomUsername)
                .setPassword(passwordMain)
                .setPasswordSubmit("12345")
                .submitRegistration()
                .checkPasswordNotEqualsError();
    }

    @Test
    @DisplayName("Проверка отображения основных компонентов основной страницы после успешной регистрации")
    void mainPageShouldBeDisplayedAfterSuccessfulLogin() {
        LoginPage.open()
                .inputUsernameAndPassword(usernameMain, passwordMain)
                .clickLoginBtn();

        MainPage.initPage().checkMainPageEssentialInfo();
    }

    @Test
    @DisplayName("Проверка появления ошибки при неверных данных пользователя")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        LoginPage.open()
                .inputUsernameAndPassword(usernameMain, "123456")
                .clickLoginBtn();

        LoginPage.initPage().checkIncorrectCredentialsError();
    }
}
