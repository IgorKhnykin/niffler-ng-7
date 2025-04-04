package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.*;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.List;
import java.util.Objects;

import static guru.qa.niffler.jupiter.extension.UserExtension.getUserFromContext;
import static guru.qa.niffler.jupiter.extension.UserExtension.setUserToContext;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendClient spendClient = SpendClient.getInstance();
    private final UserClient userClient = UserClient.getInstance();

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final AuthApiClient authApiClient = new AuthApiClient();

    private static final Config CFG = Config.getInstance();

    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(anno -> {
                    final UserJson userFromExtension = getUserFromContext();
                    final UserJson userToLogin;
                    if (anno.username().isEmpty() || anno.password().isEmpty()) {
                        if (context.getRequiredTestMethod().isAnnotationPresent(User.class)) {
                            userToLogin = userFromExtension;
                        } else {
                            throw new IllegalStateException("ApiLogin with empty login or password should be with @User annotation");
                        }
                    }
                    else{
                        if (userFromExtension != null) {
                            throw new IllegalStateException("ApiLogin with login and password should be without @User annotation");
                        }
                        userToLogin = collectTestDataModel(anno.username(), anno.password());
                        setUserToContext(userToLogin);
                    }

                    final String token = authApiClient.login(userToLogin.username(), userToLogin.testData().password());

                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", token);

                        WebDriverRunner.getWebDriver()
                                .manage()
                                .addCookie(getJsessionIdCookie());

                        Selenide.open(CFG.frontUrl() + "main", MainPage.class)
                                .checkMainPageEssentialInfo();
                    }
                }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class) &&
                AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static String getToken() {
        return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setToken(String token) {
        TestMethodContextExtension.getContext().getStore(NAMESPACE).put("token", token);
    }

    public static String getCode() {
        return TestMethodContextExtension.getContext().getStore(NAMESPACE).get("code", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.getContext().getStore(NAMESPACE).put("code", code);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie("JSESSIONID", ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID"));
    }

    private UserJson collectTestDataModel(String username, String password) {
        List<SpendJson> spends = spendClient.getAllSpendsByUsername(username);
        List<CategoryJson> categories = spendClient.getAllActiveCategoriesByUsername(username);
        List<UserJson> allFriends = userClient.findAllFriends(username);

        List<String> incomeRequests = allFriends.stream()
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.INVITE_RECEIVED))
                .map(UserJson::username)
                .toList();

        List<String> friends = allFriends.stream()
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.FRIEND))
                .map(UserJson::username)
                .toList();

        List<String> outcomeRequests = userClient.findAllUsers(username).stream()
                .filter(user -> Objects.nonNull(user.friendshipStatus()))
                .filter(user -> user.friendshipStatus().equals(FriendshipStatus.INVITE_SENT))
                .map(UserJson::username)
                .toList();

        return new UserJson(username, new TestData(password, categories, spends, incomeRequests, outcomeRequests, friends));
    }
}
