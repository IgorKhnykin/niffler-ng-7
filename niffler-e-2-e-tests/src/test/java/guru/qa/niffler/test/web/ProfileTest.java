package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    @Test
    @User(categories = @Category(
                    archived = false)
    )
    @ApiLogin
    @DisplayName("Проверка отображения архивной категории")
    void archiveCategoryShouldPresentInCategoriesListTest(UserJson user) {
        ProfilePage.open()
                .showArchivedCategories()
                .checkCategoryIsArchived(user.testData().categories().get(0).name());
    }

    @Test
    @User(categories = @Category(
                    archived = false)
    )
    @ApiLogin
    @DisplayName("Проверка отображения не архивной категории")
    void activeCategoryShouldPresentInCategoriesListTest(UserJson user) {
        ProfilePage.open()
                .checkCategoryIsNotArchived(user.testData().categories().get(0).name());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Проверка возможности редактирования профиля")
    void editProfileTestTest(UserJson user) {
        ProfilePage.open()
                .changeName("Новое имя");
    }
}
