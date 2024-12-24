package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.page.interaction.ElementAction.*;

public class EditSpendingPage {

    public static EditSpendingPage initPage() {
        return Selenide.page(EditSpendingPage.class);
    }

     private final SelenideElement descriptionInput = $("#description");

     private final SelenideElement submitBtn = $("#save");

     public void editSpendingDescription(String description) {
         clearElement(descriptionInput);
         setElementValue(descriptionInput, description);
         clickElement(submitBtn);
     }
}
