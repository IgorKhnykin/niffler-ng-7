package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserFriends implements BeforeEachCallback, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserFriends.class);

    private final UserClient userClient = new UserDbClient();

    private static final String password = "12345";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class).ifPresent(
                userAnno -> {
                    UserJson userJson = context.getStore(UserExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);

                    if (userJson == null) {
                        userJson = userClient.findAllUsers()
                                .stream()
                                .filter(user -> user.username().equals(userAnno.username()))
                                .findFirst()
                                .orElseGet(() -> userClient.createUser(userAnno.username(), password));
                    }

                    if (userAnno.withFriend() > 0) {
                        userJson.testData().friends().addAll(userClient.addFriend(userJson, userAnno.withFriend()));
                    } else if (userAnno.incomeRequest() > 0) {
                        userJson.testData().incomeRequests().addAll(userClient.getInvitation(userJson, userAnno.incomeRequest()));
                    } else if (userAnno.outcomeRequest() > 0) {
                        userJson.testData().outcomeRequests().addAll(userClient.sendInvitation(userJson, userAnno.outcomeRequest()));
                    }

                }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }
}
