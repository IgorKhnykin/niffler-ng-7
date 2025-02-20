package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    @Test
    @User(categories = @Category(
                    archived = true)
    )
    @DisplayName("Проверка отображения архивной категории")
    void archiveCategoryShouldPresentInCategoriesListTest(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openProfile()
                .showArchivedCategories()
                .checkCategoryIsArchived(user.testData().categories().get(0).name());
    }

    @Test
    @User(categories = @Category(
                    archived = false)
    )
    @DisplayName("Проверка отображения не архивной категории")
    void activeCategoryShouldPresentInCategoriesListTest(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openProfile()
                .checkCategoryIsNotArchived(user.testData().categories().get(0).name());
    }

    @Test
    @User()
    @DisplayName("Проверка возможности редактирования профиля")
    void editProfileTestTest(UserJson user) {
        LoginPage.open()
                .inputUsernameAndPassword(user.username(), user.testData().password())
                .clickLoginBtn()
                .checkMainPageEssentialInfo()
                .openProfile()
                .changeName("Новое имя");
    }
}
