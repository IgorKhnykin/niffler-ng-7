package guru.qa.niffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {
    String username() default "";
    Category[] categories() default {};
    Spending[] spendings() default {};
    int incomeRequest() default 0;
    int outcomeRequest() default 0;
    int withFriend() default 0;

}
