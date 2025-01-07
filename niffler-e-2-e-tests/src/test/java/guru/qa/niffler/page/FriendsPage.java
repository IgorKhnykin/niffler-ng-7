package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage {

    private final SelenideElement friendsTable = $("#friends");

    private final ElementsCollection friendsRows = friendsTable.$$("tr");

    private final SelenideElement unfriendBtn = $x(".//button[text()='Unfriend']");

    private final SelenideElement friendsPage = $("#simple-tabpanel-friends");

    private final SelenideElement requestsTable = $("#requests");

    private final ElementsCollection requestsRows = requestsTable.$$("tr");

    public FriendsPage checkFriendExist(String friendName) {
        friendsRows.findBy(text(friendName))
                .shouldHave(text("Unfriend"));
        return this;
    }

    public FriendsPage checkFriendsTableIsEmpty() {
        friendsPage.shouldBe(visible);
        friendsPage.shouldHave(text("There are no users yet"));
        return this;
    }

    public FriendsPage checkIncomeRequestExist(String whoRequestName) {
        requestsRows.findBy(text(whoRequestName))
                .shouldHave(text("Accept"));
        return this;
    }

}
