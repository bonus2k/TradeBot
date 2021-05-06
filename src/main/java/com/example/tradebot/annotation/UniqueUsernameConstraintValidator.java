package com.example.tradebot.annotation;

import com.example.tradebot.domain.User;
import com.example.tradebot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsername, User> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext ctx) {

        if (userService!=null && userService.getUserRepo().findByUsername(user.getUsername())
                .map(u->u.getId())
                .filter(id-> !id.equals(user.getId())).isPresent()){
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                    "Логин уже используется")
                    .addPropertyNode("username").addConstraintViolation();
            return false;
        }
        return true;
    }
}
