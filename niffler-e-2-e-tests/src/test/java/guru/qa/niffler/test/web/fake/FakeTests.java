package guru.qa.niffler.test.web.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FakeTests {

    @Test
    @ApiLogin(username = "Igor", password = "1234")
    void fakeTest(UserJson user, @Token String token) {
        MainPage.initPage().openProfile();
        System.out.println(user);
        System.out.println(token);
    }
}
