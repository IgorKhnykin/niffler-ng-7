package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AllPeoplePage extends BasePage<AllPeoplePage> {

    public static AllPeoplePage open() {
        return Selenide.open(Config.getInstance().frontUrl() + "people/all", AllPeoplePage.class);
    }

    private final SelenideElement allPeopleTable = $("#all");

    private final ElementsCollection peopleRows = allPeopleTable.$$("tr");

    private final SelenideElement addFriendBtn = $x(".//button[text()='Add friend']");

    private final SearchField search = new SearchField($("input[placeholder='Search']"));

    @Step("Найти пользователя {username} в списке всех пользователей")
    public AllPeoplePage searchUserInAllPeopleList(String username) {
        search.searchField(username);
        return this;
    }

    @Step("Проверить, что пользователь {username} присутствует в списке всех пользователей")
    public AllPeoplePage checkPeopleExist(String username) {
        searchUserInAllPeopleList(username);
        peopleRows.findBy(text(username)).shouldHave(text("Unfriend"));
        return this;
    }

    @Step("Проверить исходящее предложение о дружбе")
    public AllPeoplePage checkOutcomeInvitationInPeopleList(List<String> outcomeInvitations) {
        outcomeInvitations.forEach(username -> {
            searchUserInAllPeopleList(username);
            peopleRows.findBy(text(username)).shouldHave(text("waiting"));
        });
        return this;
    }
}
