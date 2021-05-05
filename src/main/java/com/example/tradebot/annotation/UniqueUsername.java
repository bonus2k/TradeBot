package com.example.tradebot.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented

@Constraint(validatedBy = UniqueUsernameConstraintValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Логин занят";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
