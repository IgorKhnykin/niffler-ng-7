package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AllPeoplePage {

    public static AllPeoplePage initPage() {
        return Selenide.page(AllPeoplePage.class);
    }

    private final SelenideElement allPeopleTable = $("#all");

    private final ElementsCollection peopleRows = allPeopleTable.$$("tr");

    private final SelenideElement addFriendBtn = $x(".//button[text()=\"Add friend\"]");

    private final SelenideElement searchInput = $("input[placeholder=\"Search\"]");

    public AllPeoplePage findUserInAllPeopleList(String username) {
        searchInput.setValue(username).pressEnter();
        return this;
    }

    public AllPeoplePage checkPeopleExist(String username) {
        peopleRows.findBy(text(username)).shouldHave(text("Unfriend"));
        return this;
    }

    public AllPeoplePage checkOutcomeInvitationInPeopleList(String username) {
        findUserInAllPeopleList(username);
        peopleRows.findBy(text(username)).shouldHave(text("waiting"));
        return this;
    }
}
