package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    private final SelenideElement accountMenuBtn = self.$("button[aria-label='Menu']");
    private final SelenideElement toMainMenuBtn = self.$("a[href='/main']");
    private final SelenideElement addNewSpendBtn = self.$("a[href='/spending']");
    private final SelenideElement openProfilePageBtn = $("a[href='/profile']");
    private final SelenideElement openFriendsPageBtn = $("a[href='/people/friends']");
    private final SelenideElement openAllPeoplePageBtn = $("a[href='/people/all']");
    private final SelenideElement signOutBtn = $("ul[role='menu'] li:last-child");

    public Header() {
        super($("#root header"));
    }

    public FriendsPage toFriendsPage() {
        accountMenuBtn.click();
        openFriendsPageBtn.click();
        return new FriendsPage();
    }

    public AllPeoplePage toAllPeoplePage() {
        accountMenuBtn.click();
        openAllPeoplePageBtn.click();
        return new AllPeoplePage();
    }

    public ProfilePage toProfilePage() {
        accountMenuBtn.click();
        openProfilePageBtn.click();
        return new ProfilePage();
    }

    public LoginPage sighOut() {
        accountMenuBtn.click();
        signOutBtn.click();
        return new LoginPage();
    }

    public EditSpendingPage addSpendingPage() {
        addNewSpendBtn.click();
        return new EditSpendingPage();
    }

    public MainPage toMainPage() {
        toMainMenuBtn.click();
        return new MainPage();
    }
}
