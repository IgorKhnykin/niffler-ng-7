package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.page.interaction.ElementAction.clickElement;
import static guru.qa.niffler.page.interaction.ElementAction.setElementValue;
import static guru.qa.niffler.page.interaction.ElementCondition.checkElementText;

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


    public RegisterPage setUsername(String username) {
        setElementValue(usernameInput, username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        setElementValue(passwordInput, password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String submitPassword) {
        setElementValue(passwordSubmitInput, submitPassword);
        return this;
    }

    public RegisterPage submitRegistration() {
        clickElement(signUpBtn);
        return new RegisterPage();
    }

    public RegisterPage checkSuccessfulRegistrationMessage() {
        checkElementText(successfulRegistrationNotification, "Congratulations! You've registered!");
        return this;
    }

    public LoginPage clickSingInButton() {
        clickElement(signInBtn);
        return new LoginPage();
    }

    public RegisterPage checkUsernameAlreadyExistMessage(String userName) {
        checkElementText(errorMessage, "Username `%s` already exists".formatted(userName));
        return this;
    }

    public RegisterPage checkPasswordNotEqualsError() {
        checkElementText(errorMessage, "Passwords should be equal");
        return this;
    }
}
