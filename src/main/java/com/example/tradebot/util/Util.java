package com.example.tradebot.util;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public static PageRequest getPageRequest(Pageable pageable, String direction) {

        if (!"desc".equalsIgnoreCase(direction)) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().descending());
        }
        if (!"asc".equalsIgnoreCase(direction)) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().ascending());
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }

    public static String replaceZero(String str) {
        return str.replaceAll("[\\.0]*$", "");
    }

    public static String getStringRequest(Map<String, String> form) {
        form.remove("_csrf");
        form.remove("page");
        form.remove("size");
        form.remove("sort");
        form.remove("direc");

        return form.entrySet()
                .stream()
                .filter(o -> !o.getValue().isEmpty())
                .map(o -> o.getKey() + "=" + o.getValue())
                .collect(Collectors.joining("&", "&", ""));
    }
}
