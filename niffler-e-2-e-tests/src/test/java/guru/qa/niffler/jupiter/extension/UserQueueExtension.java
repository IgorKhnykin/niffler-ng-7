package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UserQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            boolean empty) {

    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();

    private static final Queue<StaticUser> NOT_EMPTY_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("user1", "123", true));
        NOT_EMPTY_USERS.add(new StaticUser("user2", "123", false));
        NOT_EMPTY_USERS.add(new StaticUser("user3", "123", false));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
                .findFirst()
                .map(p -> p.getAnnotation(UserType.class))
                .ifPresent(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = ut.empty()
                                ? Optional.ofNullable(EMPTY_USERS.poll())
                                : Optional.ofNullable(NOT_EMPTY_USERS.poll());
                    }
                    Allure.getLifecycle().updateTestCase(x -> x.setStart(new Date().getTime()));
                    user.ifPresentOrElse(x -> context.getStore(NAMESPACE).put(context.getUniqueId(), x),
                            () -> { throw new IllegalStateException("Cant find user after 30 seconds");});
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        StaticUser user = context.getStore(NAMESPACE).get(context.getUniqueId(), StaticUser.class);
        if (user.empty) {
            EMPTY_USERS.add(user);
        }
        else {
            NOT_EMPTY_USERS.add(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class) &&
                AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
    }
}
