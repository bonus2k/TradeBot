package com.example.tradebot.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented

@Constraint(validatedBy = UniqueEmailConstraintValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

    String message() default "Email занят";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
