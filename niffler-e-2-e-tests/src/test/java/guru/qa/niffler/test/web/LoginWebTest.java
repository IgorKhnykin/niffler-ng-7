package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.converters.Browser;
import guru.qa.niffler.jupiter.converters.BrowserConverter;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.usernameMain;

public class LoginWebTest {

    @RegisterExtension
    private static NonStaticBrowserExtension extension = new NonStaticBrowserExtension();

    @User()
    @ParameterizedTest()
    @DisplayName("Проверка отображения основных компонентов основной страницы после успешной регистрации ")
    @EnumSource(Browser.class)
    void mainPageShouldBeDisplayedAfterSuccessfulLogin(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        extension.getDrivers().add(driver);
        LoginPage.open(driver)
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn();
    }

    @ParameterizedTest()
    @DisplayName("Проверка появления ошибки при неверных данных пользователя")
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        extension.getDrivers().add(driver);
        LoginPage.open(driver)
                .inputUsernameAndPassword(usernameMain, "123456")
                .clickLoginBtn();
        new LoginPage(driver).checkIncorrectCredentialsError();
    }

    @ParameterizedTest()
    @DisplayName("Проверка появления ошибки при неверных данных пользователя")
    @EnumSource(Browser.class)
    void userShouldStay(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        extension.getDrivers().add(driver);
        LoginPage.open(driver)
                .inputUsernameAndPassword(usernameMain, "123456")
                .clickLoginBtn();
        new LoginPage(driver).checkIncorrectCredentialsError();
    }
}
