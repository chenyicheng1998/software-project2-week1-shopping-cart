package com.spring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartAppTest {

    @Test
    void testShoppingCartAppCanBeInstantiated() {
        // Verify that ShoppingCartApp class exists and can be instantiated
        assertDoesNotThrow(ShoppingCartApp::new);
        ShoppingCartApp app = new ShoppingCartApp();
        assertNotNull(app);
    }

    @Test
    void testShoppingCartAppIsSubclassOfApplication() {
        // Verify that ShoppingCartApp is a JavaFX Application
        ShoppingCartApp app = new ShoppingCartApp();
        assertNotNull(app);
        assertInstanceOf(javafx.application.Application.class, app);
    }

    @Test
    void testShoppingCartAppClassExists() {
        // Verify that the class can be accessed and loaded
        assertEquals("com.spring.ShoppingCartApp", ShoppingCartApp.class.getName());
    }

    @Test
    void testShoppingCartAppHasStartMethod() {
        // Verify that the class has a start method
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method startMethod = ShoppingCartApp.class.getMethod("start", javafx.stage.Stage.class);
            assertNotNull(startMethod);
        });
    }

    @Test
    void testShoppingCartAppHasMainMethod() {
        // Verify that the class has a main method
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method mainMethod = ShoppingCartApp.class.getMethod("main", String[].class);
            assertNotNull(mainMethod);
        });
    }
}

