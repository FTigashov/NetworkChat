package com.personal.annotations;

public class TestClass {

    @BeforeSuite
    void init() {
        System.out.println("Start test...");
    }

    @Test
    void testOne() {
        System.out.println("test 1");
    }

    @Test(priority = 4)
    void testTwo() {
        System.out.println("test 2");
    }

    @Test(priority = 3)
    void testThree() {
        System.out.println("test 3");
    }

    @Test(priority = 2)
    void testFour() {
        System.out.println("test 4");
    }

    @Test(priority = 1)
    void testFive() {
        System.out.println("test 5");
    }

    @AfterSuite
    void destroy() {
        System.out.println("Finish test...");
    }
}
