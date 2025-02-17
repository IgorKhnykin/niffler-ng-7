package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    public static RegisterPage initPage() {
        return Selenide.page(RegisterPage.class);
    }

    private final SelenideElement usernameInput = $("#username");

    private final SelenideElement passwordInput = $("#password");

    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");

    private final SelenideElement signUpBtn = $("button[class='form__submit']");

    private final SelenideElement errorMessage = $(".form__error");

    private final SelenideElement successfulRegistrationNotification = $(".form__paragraph_success");

    private final SelenideElement signInBtn = $(".form_sign-in");

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
        return new RegisterPage();
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
