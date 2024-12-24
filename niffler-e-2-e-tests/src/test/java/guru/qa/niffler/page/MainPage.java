package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.page.interaction.ElementAction.clickElement;
import static guru.qa.niffler.page.interaction.ElementCondition.checkElementVisible;

public class MainPage {

  public static MainPage initPage() {
    return Selenide.page(MainPage.class);
  }

  private final ElementsCollection tableRows = $$("#spendings tbody tr");

  private final SelenideElement statisticsBlock = $("#stat");

  private final SelenideElement spendingBlock = $("#spendings");

  private final SelenideElement profileIcon = $("button[aria-label='Menu']");

  private final SelenideElement dropdownMenu = $("ul[role='menu']");

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).$$("td").get(5).click();
    return new EditSpendingPage();
  }

  public void checkThatTableContainsSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).should(visible);
  }

  public MainPage checkMainPageEssentialInfo() {
    checkElementVisible(spendingBlock);
    checkElementVisible(statisticsBlock);
    return new MainPage();
  }

  public ProfilePage openProfile() {
    clickElement(profileIcon);
    clickElement(dropdownMenu.$("a[href='/profile']"));
    return new ProfilePage();
  }
}
