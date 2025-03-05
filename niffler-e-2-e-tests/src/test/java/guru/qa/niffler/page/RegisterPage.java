package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput;

    private final SelenideElement passwordInput;

    private final SelenideElement passwordSubmitInput;

    private final SelenideElement signUpBtn;

    private final SelenideElement errorMessage;

    private final SelenideElement successfulRegistrationNotification;

    private final SelenideElement signInBtn;

    public RegisterPage(SelenideDriver driver) {

        usernameInput = driver.$("#username");

        passwordInput = driver.$("#password");

        passwordSubmitInput = driver.$("#passwordSubmit");

        signUpBtn = driver.$("button[class='form__submit']");

        errorMessage = driver.$(".form__error");

        successfulRegistrationNotification = driver.$(".form__paragraph_success");

        signInBtn = driver.$(".form_sign-in");
    }

    public RegisterPage() {
        usernameInput = $("#username");

        passwordInput = $("#password");

        passwordSubmitInput = $("#passwordSubmit");

        signUpBtn = $("button[class='form__submit']");

        errorMessage = $(".form__error");

        successfulRegistrationNotification = $(".form__paragraph_success");

        signInBtn = $(".form_sign-in");
    }

    @Step("Ввести имя пользователя")
    public RegisterPage setUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        return this;
    }

    @Step("Ввести пароль пользователя")
    public RegisterPage setPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("Подтвердить пароль пользователя")
    public RegisterPage setPasswordSubmit(String submitPassword) {
        passwordSubmitInput.shouldBe(visible).setValue(submitPassword);
        return this;
    }

    @Step("Нажать кнопку 'Подтвердить регистрацию'")
    public RegisterPage submitRegistration() {
        signUpBtn.click();
        return this;
    }

    @Step("Проверить наличие сообщения об успешной регистрации")
    public RegisterPage checkSuccessfulRegistrationMessage() {
        successfulRegistrationNotification.shouldHave(text("Congratulations! You've registered!"));
        return this;
    }

    @Step("Нажать кнопку 'Войти'")
    public LoginPage clickSingInButton() {
        signInBtn.click();
        return new LoginPage();
    }

    @Step("Проверить присутствие сообщения о существовании пользователя с таким именем")
    public RegisterPage checkUsernameAlreadyExistMessage(String userName) {
        errorMessage.shouldHave(text("Username `%s` already exists".formatted(userName)));
        return this;
    }

    @Step("Проверить наличие сообщения о несоответствии пароля")
    public RegisterPage checkPasswordNotEqualsError() {
        errorMessage.shouldHave(text("Passwords should be equal"));
        return this;
    }
}
