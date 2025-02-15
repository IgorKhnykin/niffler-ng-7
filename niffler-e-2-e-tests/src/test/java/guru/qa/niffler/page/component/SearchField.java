package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField(SelenideElement self) {
        super(self);
    }

    public SearchField searchField(String query) {
        self.click();
        self.clear();
        self.setValue(query).pressEnter();
        return this;
    }
}
