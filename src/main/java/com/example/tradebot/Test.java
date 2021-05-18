package com.example.tradebot;

import com.example.tradebot.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
        SimpleDateFormat formater = new SimpleDateFormat("dd MMMM YY");
        System.out.println(String.format("Период расчета с %s по %s", formater.format(Util.getWeek(new Date())), formater.format(new Date())));
    }
}
