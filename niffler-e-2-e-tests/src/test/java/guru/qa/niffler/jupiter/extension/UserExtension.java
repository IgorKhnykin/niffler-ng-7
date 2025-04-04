package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UserClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    private final UserClient userClient = UserClient.getInstance();

    private static final String defaultPassword = "1234";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(user -> {
                    final String username = randomUsername();
                    if ("".equals(user.username())) {
                        UserJson userJson = userClient.createUser(username, defaultPassword);
                        context.getStore(NAMESPACE)
                                .put(context.getUniqueId(), userJson);
                    }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }

    public static UserJson getUserFromContext() {
        final ExtensionContext context = TestMethodContextExtension.getContext();
        return context.getStore(UserExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }

    public static void setUserToContext(UserJson userToLogin) {
        final ExtensionContext context = TestMethodContextExtension.getContext();
        context.getStore(UserExtension.NAMESPACE).put(context.getUniqueId(), userToLogin);
    }
}
