package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  public static MainPage initPage() {
    return Selenide.page(MainPage.class);
  }

  private final ElementsCollection tableRows = $$("#spendings tbody tr");

  private final SelenideElement statisticsBlock = $("#stat");

  private final SelenideElement spendingBlock = $("#spendings");

  private final SelenideElement searchInput = $("input[placeholder='Search']");

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
    search.searchField(spendingDescription);
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
}
