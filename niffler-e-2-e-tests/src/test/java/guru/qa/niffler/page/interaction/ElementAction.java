package guru.qa.niffler.page.interaction;

import com.codeborne.selenide.SelenideElement;

public class ElementAction {

    public static SelenideElement clickElement(SelenideElement el) {
        ElementCondition.checkElementClickable(el).click();
        return el;
    }

    public static SelenideElement setElementText(SelenideElement el, String text) {
        ElementCondition.checkElementVisible(el).setValue(text);
        return el;
    }

    public static SelenideElement setElementValue(SelenideElement el, String text) {
        ElementCondition.checkElementVisible(el).setValue(text);
        return el;
    }

    public static SelenideElement scrollIntoElement(SelenideElement el, boolean toTop) {
        ElementCondition.checkElementVisible(el).scrollIntoView(toTop);
        return el;
    }

    public static SelenideElement clearElement(SelenideElement el) {
        ElementCondition.checkElementVisible(el).clear();
        return el;
    }
}
