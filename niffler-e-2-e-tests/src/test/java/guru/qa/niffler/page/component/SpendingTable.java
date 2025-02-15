package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class SpendingTable extends BaseComponent<SpendingTable> {

    private final ElementsCollection spendingTableRows = self.$$("tr");

    private final SelenideElement deleteBtn = self.$("button[id='delete']");

    private final SearchField search = new SearchField($("input[placeholder='Search']"));

    public SpendingTable() {
        super($("div[id='spendings']"));
    }

    public EditSpendingPage toEditSpendingPage(String description) {
        findSpendByDescription(description).$("button[aria-label='Edit spending']").click();
        return new EditSpendingPage();
    }

    public SpendingTable deleteSpending(String description) {
        findSpendByDescription(description).$("input[type='checkbox']").click();
        deleteBtn.click();
        return this;
    }

    public SpendingTable searchSpendingByDescription(String description) {
        search.searchField(description);
        return this;
    }

    public SpendingTable checkTableContains(String... expectedSpendings) {
        for (String expectedSpending : expectedSpendings) {
            findSpendByDescription(expectedSpending).shouldBe(visible);
        }
        return this;
    }

    public SpendingTable checkTableSize(int expectedSize) {
        int actualSize = spendingTableRows.size() - 1;
        Assertions.assertEquals(expectedSize, actualSize, "Размер таблицы трат не совпадает с ожидаемым");
        return this;
    }

    private SelenideElement findSpendByDescription(String description) {
        search.searchField(description);
        return spendingTableRows.findBy(text(description));
    }
}
