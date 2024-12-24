package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static guru.qa.niffler.page.interaction.ElementAction.clickElement;
import static guru.qa.niffler.page.interaction.ElementCondition.checkElementVisible;

public class ProfilePage {
    public static ProfilePage initPage() {
        return Selenide.page(ProfilePage.class);
    }

    private final SelenideElement switcher = $("input[type='checkbox']");

    private final ElementsCollection archiveCategories = $$x(".//span[@aria-label='Unarchive category']/..");

    private final ElementsCollection notArchiveCategories = $$x(".//button[@aria-label='Archive category']/../..");

    public ProfilePage showArchivedCategories() {
        clickElement(switcher);
        return this;
    }

    public ProfilePage checkCategoryIsArchived(String categoryName) {
        var archiveCategory =  archiveCategories.findBy(text("%s".formatted(categoryName)));
        checkElementVisible(archiveCategory);
        return this;
    }

    public ProfilePage checkCategoryIsNotArchived(String categoryName) {
        var activeCategory =  notArchiveCategories.findBy(text("%s".formatted(categoryName)));
        checkElementVisible(activeCategory);
        return this;
    }
}
