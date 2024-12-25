package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    public static LoginPage open() {
        return Selenide.open(Config.getInstance().frontUrl(), LoginPage.class);
    }

    public static LoginPage initPage() {
        return Selenide.page(LoginPage.class);
    }

    private final SelenideElement usernameInput = $("input[name='username']");

    private final SelenideElement passwordInput = $("input[name='password']");

    private final SelenideElement submitBtn = $("button[type='submit']");

    private final SelenideElement createNewAccountBtn = $("a[class='form__register']");

    private final SelenideElement incorrectCredentialsError = $(".form__error");


    public RegisterPage clickCreateNewAccountBtn() {
        createNewAccountBtn.shouldBe(visible).click();
        return new RegisterPage();
    }

    public LoginPage inputUsernameAndPassword(String username, String password) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    public MainPage clickLoginBtn() {
        submitBtn.click();
        return new MainPage();
    }

    public LoginPage checkIncorrectCredentialsError() {
        incorrectCredentialsError.shouldHave(text("Неверные учетные данные пользователя"));
        return this;
    }
}
