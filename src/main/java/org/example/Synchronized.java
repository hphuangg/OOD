package org.example;

import java.time.LocalDateTime;

public class Synchronized {
    public synchronized void instanceMethod() {
        System.out.println(LocalDateTime.now() + " - "  + Thread.currentThread().getName() + " is executing instanceMethod");
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println(LocalDateTime.now() + " - "  + Thread.currentThread().getName() + " finished instanceMethod");
    }

    public static synchronized void staticMethod() {
        System.out.println(LocalDateTime.now() + " - "  + Thread.currentThread().getName() + " is executing staticMethod");
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println(LocalDateTime.now() + " - "  + Thread.currentThread().getName() + " finished staticMethod");
    }

    public static void main(String[] args) {
        Synchronized obj1 = new Synchronized();
        Synchronized obj2 = new Synchronized();

        // Test instance methods
        new Thread(() -> obj1.instanceMethod(), "Thread 1").start();
        new Thread(() -> obj2.instanceMethod(), "Thread 2").start();

        // Test static methods
        new Thread(() -> obj1.staticMethod(), "Thread 3").start();
        new Thread(() -> obj2.staticMethod(), "Thread 4").start();
    }
}
