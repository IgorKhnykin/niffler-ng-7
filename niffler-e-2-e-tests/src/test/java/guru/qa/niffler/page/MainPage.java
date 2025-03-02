package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenResult;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage extends BasePage<MainPage> {

    public static MainPage initPage() {
        return Selenide.page(MainPage.class);
    }

    private final ElementsCollection tableRows = $$("#spendings tbody tr");

    private final SelenideElement statisticsBlock = $("#stat");

    private final SelenideElement spendingBlock = $("#spendings");

    private final SelenideElement searchInput = $("input[placeholder='Search']");

    private final SelenideElement acceptDeleteBtn = $x(".//div[@role='dialog']//button[text()='Delete']");

    private final SelenideElement statistic = $("canvas[role='img']");

    private final ElementsCollection statisticBubbles = $$("#legend-container li");

    private final Header header = new Header();

    private final SearchField search = new SearchField($("input[placeholder='Search']"));

    private final SpendingTable spendingTable = new SpendingTable();

    @Step("Перейти к странице редактирования траты {spendingDescription}")
    public EditSpendingPage openEditSpendingPage(String spendingDescription) {
        spendingTable.toEditSpendingPage(spendingDescription);
        return new EditSpendingPage();
    }

    @Step("Проверка присутствия траты {spendingDescription} в списке трат")
    public void checkThatTableContainsSpending(String spendingDescription) {
        spendingTable.searchSpendingByDescription(spendingDescription);
        tableRows.find(text(spendingDescription)).should(visible);
    }

    @Step("Проверка основной информации главной страницы")
    public MainPage checkMainPageEssentialInfo() {
        spendingBlock.shouldBe(visible);
        statisticsBlock.shouldBe(visible);
        return new MainPage();
    }

    @Step("Перейти к странице профиля пользователя")
    public ProfilePage openProfile() {
        return header.toProfilePage();
    }

    @Step("Перейти на вкладку Друзья")
    public FriendsPage openFriends() {
        return header.toFriendsPage();
    }

    @Step("Перейти на вкладку все пользователи")
    public AllPeoplePage openAllPeople() {
        return header.toAllPeoplePage();
    }

    @Step("Перейти на вкладку создания категории")
    public EditSpendingPage createNewSpend() {
        header.addSpendingPage();
        return new EditSpendingPage();
    }

    @Step("Удалить трату")
    public MainPage deleteSpend(String description) {
        spendingTable.deleteSpending(description);
        acceptDeleteBtn.click();
        return new MainPage();
    }

    @Step("Сделать скриншот статистики")
    public BufferedImage makeStatisticScreenshot() throws IOException {
        Selenide.sleep(2000);
        return ImageIO.read(Objects.requireNonNull(statistic.screenshot()));
    }

    @Step("Проверка присутствия bubbles под статистикой")
    public MainPage checkStatisticBubbles(List<SpendJson> spends, Color... colors) {
        List<CategoryJson> categoryList = spends.stream().map(SpendJson::category).distinct().toList();
            statisticBubbles.shouldHave(StatConditions.color(colors));

        spends.stream()
                .collect(Collectors.groupingBy(SpendJson::category, Collectors.summingDouble(SpendJson::amount)))
                .forEach((category, sum) -> {
                    String bubbleExpectedText = category.name() + " " + sum.longValue() + " ₽";
                    statisticBubbles.get(categoryList.indexOf(category)).shouldHave(Condition.text(bubbleExpectedText));
                });
        return new MainPage();
    }
}
