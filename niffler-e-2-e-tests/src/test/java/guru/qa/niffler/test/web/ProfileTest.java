package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.TestData.passwordMain;
import static guru.qa.niffler.utils.TestData.usernameMain;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    @Test
    @Category(
            username = usernameMain,
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
            username = usernameMain,
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
