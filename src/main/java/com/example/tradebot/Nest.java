package com.example.tradebot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Nest {
    public static void main(String[] args) {
//        String s = "16.00000000";
//        s = s.replaceAll("[\\.0]*$", "");
//        System.out.println(s);
//
//        Test test = new Test();
//        test.setName("Test");
//        InnerTest innerTest = null;
////                new InnerTest("InnerTest", new InnerInner("InnnerInner"));
//        test.setInnerTest(innerTest);
//
//        Optional<Test> optionalTest = Optional.ofNullable(test);
//        String text = optionalTest
//                .map(Test::getInnerTest)
//                .map(InnerTest::getInnerInner)
//                .map(InnerInner::getInnerInnerName)
//                .orElseGet(String::new);
//        System.out.println(text.isEmpty());

        Map<String, Object> request = new HashMap<>();
        request.put("status", "nnn");
        Optional<Map<String, Object>> requestOpt = Optional.ofNullable(request);

        Object result = requestOpt
                .map(o->o.get("status"))
                .orElseThrow(()->new RuntimeException());
        System.out.println(result);
    }


}
