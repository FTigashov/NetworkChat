package com.personal.annotations;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class ConfigTest {
    public static void start(Class<?> className) {
        final int MIN_PRIORITY = 1;
        final int MAX_PRIORITY = 10;

        boolean beforeMethodIsExists = false;
        boolean afterMethodIsExists = false;

        Map<Integer, Method> map = new TreeMap<>();

        for (Method method : className.getDeclaredMethods()) {
            if (method.getAnnotation(BeforeSuite.class) != null) {
                if (beforeMethodIsExists) {
                    throw new RuntimeException("Метод с аннотацией @BeforeSuite уже существует");
                } else {
                    beforeMethodIsExists = true;
                    map.put(MIN_PRIORITY - 1, method);
                }
            }

            if (method.getAnnotation(AfterSuite.class) != null) {
                if (afterMethodIsExists) {
                    throw new RuntimeException("Метод с аннотацией @AfterSuite уже существует");
                } else {
                    afterMethodIsExists = true;
                    map.put(MAX_PRIORITY + 1, method);
                }
            }

            if (method.getAnnotation(Test.class) != null) {
                Test test = method.getAnnotation(Test.class);
                map.put(test.priority(), method);
            }
        }

        System.out.println(className.getSimpleName() + ":");
        for (Integer key : map.keySet()) {
            System.out.println(map.get(key).getName() + "\tpriority: " + key);
        }
    }
}
