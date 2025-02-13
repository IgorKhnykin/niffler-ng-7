package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

public class SearchField {

    private final SelenideElement self;

    public SearchField(SelenideElement self) {
        this.self = self;
    }

    public SearchField searchField(String query) {
        self.click();
        self.clear();
        self.setValue(query).pressEnter();
        return this;
    }
}
