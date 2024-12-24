package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.page.interaction.ElementAction.clickElement;
import static guru.qa.niffler.page.interaction.ElementAction.setElementValue;
import static guru.qa.niffler.page.interaction.ElementCondition.checkElementText;

public class LoginPage{

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
        clickElement(createNewAccountBtn);
        return new RegisterPage();
    }

    public LoginPage inputUsernameAndPassword(String username, String password) {
        setElementValue(usernameInput, username);
        setElementValue(passwordInput, password);
        return this;
    }

    public MainPage clickLoginBtn() {
        clickElement(submitBtn);
        return new MainPage();
    }

    public LoginPage checkIncorrectCredentialsError() {
        checkElementText(incorrectCredentialsError, "Неверные учетные данные пользователя");
        return this;
    }


}
