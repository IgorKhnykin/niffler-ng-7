package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.jupiter.Spending;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.TestData.*;

@ExtendWith(BrowserExtension.class)
public class SpendingWebTest {

    @Test
    @DisplayName("Обновление описания траты")
    @Spending(
            category = "Обучение",
            description = "new description",
            username = usernameMain,
            amount = 845444.0)
    void categoryDescriptionShouldBeEditedByCategoryAction(SpendJson spend) {
        final String newSpendingName = "Машина edited";
        LoginPage.open()
                .inputUsernameAndPassword(spend.username(), passwordMain)
                .clickLoginBtn()
                .editSpending(spend.description())
                .editSpendingDescription(newSpendingName)
                .save();

        MainPage.initPage().checkThatTableContainsSpending(newSpendingName);
    }

    @Test
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
        var username = Faker.instance().name().username();
        LoginPage.open()
                .clickCreateNewAccountBtn()
                .setUsername(username)
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

    @Test
    @Category(
            archived = true)
    @DisplayName("Проверка отображения архивной категории")
    void archiveCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) {
        LoginPage.open()
                .inputUsernameAndPassword(usernameMain, passwordMain)
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openProfile()
                .showArchivedCategories()
                .checkCategoryIsArchived(categoryJson.name());
    }

    @Test
    @Category(
            archived = false)
    @DisplayName("Проверка отображения не архивной категории")
    void activeCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) {
        LoginPage.open()
                .inputUsernameAndPassword(usernameMain, passwordMain)
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openProfile()
                .checkCategoryIsNotArchived(categoryJson.name());
    }
}

