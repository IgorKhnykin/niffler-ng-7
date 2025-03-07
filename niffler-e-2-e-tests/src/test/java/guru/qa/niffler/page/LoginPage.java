package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class LoginPage extends BasePage<LoginPage>{

    private final SelenideElement usernameInput;

    private final SelenideElement passwordInput;

    private final SelenideElement submitBtn;

    private final SelenideElement createNewAccountBtn;

    private final SelenideElement incorrectCredentialsError;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");

        this.passwordInput = driver.$("input[name='password']");

        this.submitBtn = driver.$("button[type='submit']");

        this.createNewAccountBtn = driver.$("a[class='form__register']");

        this.incorrectCredentialsError = driver.$(".form__error");
    }

    public LoginPage() {
        this.usernameInput = $("input[name='username']");

        this.passwordInput = $("input[name='password']");

        this.submitBtn = $("button[type='submit']");

        this.createNewAccountBtn = $("a[class='form__register']");

        this.incorrectCredentialsError = $(".form__error");
    }

    public static LoginPage open() {
        return Selenide.open(Config.getInstance().frontUrl(), LoginPage.class);
    }

    public static LoginPage open(SelenideDriver driver) {
        driver.open(Config.getInstance().frontUrl(), LoginPage.class);
        return new LoginPage(driver);
    }

    @Step("Нажать на кнопку 'Создать новый аккаунт'")
    public RegisterPage clickCreateNewAccountBtn() {
        createNewAccountBtn.click();
        return new RegisterPage();
    }

    @Step("Ввести имя пользователя и пароль")
    public LoginPage inputUsernameAndPassword(String username, String password) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("Нажать на кнопку 'Войти'")
    public MainPage clickLoginBtn() {
        submitBtn.click();
        return new MainPage();
    }

    @Step("Проверка отображения записи о неверных данных пользователя")
    public LoginPage checkIncorrectCredentialsError() {
        incorrectCredentialsError.shouldHave(text("Неверные учетные данные пользователя"));
        return this;
    }
}
