package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.passwordMain;
import static guru.qa.niffler.utils.RandomDataUtils.usernameMain;

@WebTest
public class ProfileTest {

    @Test
    @User(username = usernameMain,
            category = @Category(
                    archived = true)
    )
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
    @User(username = usernameMain,
            category = @Category(
                    archived = false)
    )
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
