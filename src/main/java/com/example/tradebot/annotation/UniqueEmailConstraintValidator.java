package com.example.tradebot.annotation;

import com.example.tradebot.domain.User;
import com.example.tradebot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Configurable
public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, User> {
    @Autowired
    UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext ctx) {

        if (userService!=null && userService.getUserRepo().findByEmail(user.getEmail())
                .map(u->u.getId())
                .filter(id-> !id.equals(user.getId())).isPresent()){
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                    "Email уже используется")
                    .addPropertyNode("email").addConstraintViolation();
            return false;
        }
        return true;
    }
}
