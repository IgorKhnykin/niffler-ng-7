package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement clearBtn = $("#input-clear");

    public SearchField(SelenideElement self) {
        super(self);
    }

    public SearchField searchField(String query) {
        if (self.is(not(empty))) {
            clearBtn.click();
            self.shouldHave(value(""));
        }
        self.setValue(query);
        self.pressEnter();
        return this;
    }
}
