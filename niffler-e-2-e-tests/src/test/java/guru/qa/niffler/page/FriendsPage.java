package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage {

    private final SelenideElement friendsTable = $("table[aria-labelledby='tableTitle']");

    private final ElementsCollection friendsRows = friendsTable.$$("tr");

    private final SelenideElement unfriendBtn = $x(".//button[text()='Unfriend']");

    private final SelenideElement friendsPage = $("#simple-tabpanel-friends");

    private final SelenideElement requestsTable = $("#requests");

    private final ElementsCollection requestsRows = requestsTable.$$("tr");

    private final SearchField search = new SearchField($("input[placeholder='Search']"));

    @Step("Проверка присутствия друга в таблице друзей")
    public FriendsPage checkFriendExist(List<String> friendsName) {
        friendsName.forEach(friendName -> {
            search.searchField(friendName);
            friendsRows.findBy(text(friendName)).shouldHave(text("Unfriend"));
        });
        return this;
    }

    @Step("Проверка отсутствия пользователей в таблице друзей")
    public FriendsPage checkFriendsTableIsEmpty() {
        friendsPage.shouldBe(visible);
        friendsPage.shouldHave(text("There are no users yet"));
        return this;
    }

    @Step("Проверка присутствия входящего запроса в друзья")
    public FriendsPage checkIncomeRequestExist(List<String> requestersNames) {
        requestersNames.forEach(requesterName -> {
            search.searchField(requesterName);
            requestsRows.findBy(text(requesterName)).shouldHave(text("Accept"));
        });
        return this;
    }

    @Step("Принять запрос в друзья")
    public FriendsPage acceptIncomeRequest(String requesterName) {
        search.searchField(requesterName);
        friendsRows.findBy(text(requesterName)).$x(".//button[text()='Accept']").click();
        return this;
    }

    @Step("Отклонить запрос в друзья")
    public FriendsPage declineIncomeRequest(String requesterName) {
        search.searchField(requesterName);
        friendsRows.findBy(text(requesterName)).$x(".//button[text()='Decline']").click();
        return this;
    }
}
