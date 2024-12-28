package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(UserQueueExtension.class)
public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
        EMPTY,
        WITH_FRIEND,
        WITH_INCOME_REQUEST,
        WITH_OUTCOME_REQUEST
    }
}
