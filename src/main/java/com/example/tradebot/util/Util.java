package com.example.tradebot.util;


import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Util {

    public static Map<String, String> getErrors(BindingResult bindingResult) {

        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage,
                (a, b) -> String.join("; ", a, b));
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    public static Date getWeek(Date date) {
        Date dateWeek = new Date(date.getTime() - 604_800_000);
        return dateWeek;
    }

}
