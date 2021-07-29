package com.example.tradebot.util;

public class Test {
    public static void main(String[] args) {
        String str = "jdbc\\:postgresql\\://localhost/sorm";
        str = str.replaceAll("(^.*)(//)", "");
        System.out.println(str);
    }
}
