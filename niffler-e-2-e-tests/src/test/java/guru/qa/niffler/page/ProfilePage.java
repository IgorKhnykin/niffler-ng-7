package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.ScreenResult;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;

public class ProfilePage extends BasePage<ProfilePage>{

    public static ProfilePage open() {
        return Selenide.open(Config.getInstance().frontUrl() + "profile", ProfilePage.class);
    }

    private final SelenideElement switcher = $("input[type='checkbox']");

    private final SelenideElement nameInput = $("#name");

    private final SelenideElement saveChangesBtn = $("button[type='submit']");

    private final SelenideElement uploadFileBtn = $("input[type='file']");

    private final SelenideElement alert = $(".MuiAlert-message");

    private final SelenideElement profileAvatar = $("form .MuiAvatar-img");

    private final ElementsCollection archiveCategories = $$x(".//span[@aria-label='Unarchive category']/..");

    private final ElementsCollection notArchiveCategories = $$x(".//button[@aria-label='Archive category']/../..");

    private final Header header = new Header();

    @Step("Отобразить архивные категории")
    public ProfilePage showArchivedCategories() {
        switcher.click();
        return this;
    }

    @Step("Проверить, что категория {categoryName} в архиве")
    public ProfilePage checkCategoryIsArchived(String categoryName) {
        var archiveCategory =  archiveCategories.findBy(text("%s".formatted(categoryName)));
        archiveCategory.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что категория {categoryName} не в архиве")
    public ProfilePage checkCategoryIsNotArchived(String categoryName) {
        var activeCategory =  notArchiveCategories.findBy(text("%s".formatted(categoryName)));
        activeCategory.shouldBe(visible);
        return this;
    }

    @Step("Изменить имя пользователя на {name}")
    public ProfilePage changeName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        saveChangesBtn.click();
        alert.shouldHave(text("Profile successfully updated"));
        return this;
    }

    @Step("Изменить фото профиля")
    public ProfilePage changeProfilePicture(String filename) {
        uploadFileBtn.uploadFromClasspath("img/%s".formatted(filename));
        saveChangesBtn.click();
        alert.shouldHave(text("Profile successfully updated"));
        return this;
    }

    @Step("Сделать скриншот аватарки пользователя")
    public ProfilePage makeAvatarScreenshot(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(profileAvatar.screenshot()));
        Assertions.assertFalse(new ScreenResult(expected, actual));
        return this;
    }

    @Step("Сделать скриншот аватарки пользователя в header-е")
    public ProfilePage makeAvatarScreenshotInHeader(BufferedImage expected) throws IOException {
        BufferedImage actual = header.getUserAvatarFromHeader();
        Assertions.assertFalse(new ScreenResult(expected, actual));
        return this;
    }
}
