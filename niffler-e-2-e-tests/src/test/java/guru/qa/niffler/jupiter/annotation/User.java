package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.jupiter.extension.UserFriendsExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({UserExtension.class, CategoryExtension.class, SpendingExtension.class, UserFriendsExtension.class})
public @interface User {
    String username() default "";
    Category[] categories() default {};
    Spending[] spendings() default {};
    int incomeRequest() default 0;
    int outcomeRequest() default 0;
    int withFriend() default 0;

}
