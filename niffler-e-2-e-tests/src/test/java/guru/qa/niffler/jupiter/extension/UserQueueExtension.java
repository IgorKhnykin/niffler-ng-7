package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
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
            String friend,
            String income,
            String outcome) {

    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();

    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();

    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("bee", "123", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "123","bill", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("igor", "123", null, "bill", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "123", null, null, "bill"));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
                .map(p -> p.getAnnotation(UserType.class))
                .forEach(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 10) {
                        user = getUserFromQueueByType(ut.value());
                    }

                    Allure.getLifecycle().updateTestCase(x -> x.setStart(new Date().getTime()));

                    user.ifPresentOrElse(x ->
                        ((Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                .getOrComputeIfAbsent(context.getUniqueId(),
                                        key -> new HashMap<>())).put(ut, x),
                            () -> { throw new IllegalStateException("Cant find user after 30 seconds");});

                    });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserType, StaticUser> e : map.entrySet()) {
            addUserToQueue(e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class) &&
                AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UserType userType = parameterContext.getParameter().getAnnotation(UserType.class);
        return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class).get(userType);
    }

    private static Optional<StaticUser> getUserFromQueueByType(UserType.Type userType) {
        return switch (userType) {
            case UserType.Type.EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
            case UserType.Type.WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
            case UserType.Type.WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
            case UserType.Type.WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
        };
    }

    private static void addUserToQueue(Map.Entry<UserType, StaticUser> userMap) {
        switch (userMap.getKey().value()) {
            case EMPTY -> EMPTY_USERS.add(userMap.getValue());
            case WITH_FRIEND -> WITH_FRIEND_USERS.add(userMap.getValue());
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(userMap.getValue());
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(userMap.getValue());
        }
    }
}
