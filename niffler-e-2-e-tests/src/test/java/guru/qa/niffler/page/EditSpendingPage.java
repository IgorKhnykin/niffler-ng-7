package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement saveBtn = $("#save");

  public EditSpendingPage editSpendingDescription(String description) {
    descriptionInput.shouldBe(visible).clear();
    descriptionInput.shouldBe(visible).setValue(description);
    return this;
  }

  public void save() {
    saveBtn.click();
  }
}
