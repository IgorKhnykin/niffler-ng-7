package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

//    @Test
//    @Category(
//            username = usernameMain,
//            archived = true)
//    @DisplayName("Проверка отображения архивной категории")
//    void archiveCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) {
//        LoginPage.open()
//                .inputUsernameAndPassword(usernameMain, passwordMain)
//                .clickLoginBtn()
//                .checkMainPageEssentialInfo()
//                .openProfile()
//                .showArchivedCategories()
//                .checkCategoryIsArchived(categoryJson.name());
//    }
//
//    @Test
//    @Category(
//            username = usernameMain,
//            archived = false)
//    @DisplayName("Проверка отображения не архивной категории")
//    void activeCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) {
//        LoginPage.open()
//                .inputUsernameAndPassword(usernameMain, passwordMain)
//                .clickLoginBtn()
//                .checkMainPageEssentialInfo()
//                .openProfile()
//                .checkCategoryIsNotArchived(categoryJson.name());
//    }

    @Test
    void testWithEmptyUser1(@UserType(empty = true) UserQueueExtension.StaticUser user, @UserType() UserQueueExtension.StaticUser user1) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
        System.out.println(user1);
    }

    @Test
    void testWithEmptyUser2(@UserType(empty = true) UserQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }
}
