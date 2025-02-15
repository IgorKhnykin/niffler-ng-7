package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import java.util.Date;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelenideElement descriptionInput = $("#description");

  private final SelenideElement saveBtn = $("#save");

  private final SelenideElement amountInput = $("#amount");

  private final SelenideElement categoryInput = $("#category");

  private final Calendar calendar = new Calendar();

  private final SelenideElement calendarIcon = $x(".//button[contains(@aria-label, 'Choose')]");

  @Step("Изменить описание траты на {description}")
  public EditSpendingPage editSpendingDescription(String description) {
    descriptionInput.shouldBe(visible).clear();
    descriptionInput.shouldBe(visible).setValue(description);
    return this;
  }

  @Step("Изменить сумму траты на {amount}")
  public EditSpendingPage editSpendingAmount(String amount) {
    amountInput.shouldBe(visible).clear();
    amountInput.shouldBe(visible).setValue(amount);
    return this;
  }

  @Step("Изменить категорию траты на {categoryName}")
  public EditSpendingPage editSpendingCategory(String categoryName) {
    categoryInput.shouldBe(visible).clear();
    categoryInput.shouldBe(visible).setValue(categoryName);
    return this;
  }

  @Step("Изменить дату траты")
  public EditSpendingPage editSpendingDate(Date date) {
    calendarIcon.click();
    calendar.selectDateInCalendar(date);
    return this;
  }

  @Step("Нажать кнопку 'Add'")
  public MainPage save() {
    saveBtn.click();
    return new MainPage();
  }
}
