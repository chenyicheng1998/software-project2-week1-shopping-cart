package com.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartEdgeCasesTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void testAddItemWithMaxValues() {
        cart.addItem(Double.MAX_VALUE, Integer.MAX_VALUE);
        assertTrue(Double.isInfinite(Double.MAX_VALUE * Integer.MAX_VALUE));
        assertTrue(Double.isInfinite(cart.calculateTotalCost()));
    }

    @Test
    void testAddItemWithMinValues() {
        cart.addItem(Double.MIN_VALUE, 1);
        assertEquals(Double.MIN_VALUE, cart.calculateTotalCost());
    }

    @ParameterizedTest
    @CsvSource({
            "0.01, 1, 0.01",
            "0.10, 10, 1.00",
            "0.99, 100, 99.00"
    })
    void testPreciseCalculations(double price, int quantity, double expected) {
        cart.addItem(price, quantity);
        assertEquals(expected, cart.calculateTotalCost(), 0.0001);
    }

    @Test
    void testMultipleAddsThenClearThenAddAgain() {
        cart.addItem(10.0, 2);
        cart.addItem(20.0, 3);
        assertEquals(80.0, cart.calculateTotalCost());

        cart.clear();
        assertEquals(0.0, cart.calculateTotalCost());

        cart.addItem(5.0, 10);
        assertEquals(50.0, cart.calculateTotalCost());
        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getItems().get(0).getItemNumber());
    }

    @Test
    void testCalculateItemCostWithZeroValues() {
        assertEquals(0.0, cart.calculateItemCost(0.0, 0));
        assertEquals(0.0, cart.calculateItemCost(100.0, 0));
        assertEquals(0.0, cart.calculateItemCost(0.0, 100));
    }

    @Test
    void testAddItemAutomaticallyNumbersSequentially() {
        cart.addItem(1.0, 1);
        cart.addItem(2.0, 1);
        cart.addItem(3.0, 1);

        assertEquals(1, cart.getItems().get(0).getItemNumber());
        assertEquals(2, cart.getItems().get(1).getItemNumber());
        assertEquals(3, cart.getItems().get(2).getItemNumber());
    }

    @Test
    void testGetItemCostsReturnsCopy() {
        cart.addItem(10.0, 2);
        cart.addItem(20.0, 3);

        var costs = cart.getItemCosts();
        assertEquals(2, costs.size());
        assertEquals(20.0, costs.get(0));
        assertEquals(60.0, costs.get(1));

        // Modifying returned list shouldn't affect cart
        costs.set(0, 999.0);
        assertEquals(20.0, cart.getItemCosts().get(0));
    }
}