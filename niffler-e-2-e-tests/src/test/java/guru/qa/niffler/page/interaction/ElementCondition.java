package guru.qa.niffler.page.interaction;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

public class ElementCondition {
    public static final int TIME_OUT = 10;

    public static SelenideElement checkElementVisible(SelenideElement el) {
        el.shouldBe(Condition.visible, Duration.ofSeconds(TIME_OUT));
        return el;
    }

    public static SelenideElement checkElementClickable(SelenideElement el) {
        el.shouldBe(Condition.clickable, Duration.ofSeconds(TIME_OUT));
        return el;
    }

    public static SelenideElement checkElementNotVisible(SelenideElement el) {
        el.shouldBe(Condition.disappear, Duration.ofSeconds(TIME_OUT));
        return el;
    }

    public static SelenideElement checkElementText(SelenideElement el, String expectedText) {
        el.shouldBe(Condition.text(expectedText), Duration.ofSeconds(TIME_OUT));
        return el;
    }
}
