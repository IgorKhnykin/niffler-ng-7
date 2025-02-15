package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    private final SelenideElement alert = $(".MuiAlert-message");

    @SuppressWarnings("unchecked")
    public T checkNotification(String text) {
        alert.shouldHave(text(text));
        return (T) this;
    };
}
