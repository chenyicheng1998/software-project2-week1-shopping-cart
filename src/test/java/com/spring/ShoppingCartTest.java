package com.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void testCalculateItemCost_normalValues() {
        assertEquals(20.0, cart.calculateItemCost(4.0, 5));
    }

    @Test
    void testCalculateItemCost_zeroQuantity() {
        assertEquals(0.0, cart.calculateItemCost(10.0, 0));
    }

    @Test
    void testCalculateItemCost_zeroPrice() {
        assertEquals(0.0, cart.calculateItemCost(0.0, 5));
    }

    @Test
    void testCalculateItemCost_negativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cart.calculateItemCost(-1.0, 2));
    }

    @Test
    void testCalculateItemCost_negativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cart.calculateItemCost(10.0, -1));
    }

    @Test
    void testCalculateTotalCost_emptyCart() {
        assertEquals(0.0, cart.calculateTotalCost());
    }

    @Test
    void testCalculateTotalCost_singleItem() {
        cart.addItem(10.0, 3);
        assertEquals(30.0, cart.calculateTotalCost());
    }

    @Test
    void testCalculateTotalCost_multipleItems() {
        cart.addItem(10.0, 2);
        cart.addItem(5.0, 4);
        cart.addItem(3.0, 1);
        assertEquals(43.0, cart.calculateTotalCost());
    }

    @Test
    void testClearCart() {
        cart.addItem(10.0, 2);
        cart.clear();
        assertEquals(0.0, cart.calculateTotalCost());
    }

    @Test
    void testClearCart_itemsListIsEmpty() {
        cart.addItem(10.0, 2);
        cart.clear();
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testGetItemCosts_singleItem() {
        cart.addItem(5.0, 3);
        List<Double> costs = cart.getItemCosts();
        assertEquals(1, costs.size());
        assertEquals(15.0, costs.get(0));
    }

    @Test
    void testGetItemCosts_multipleItems() {
        cart.addItem(5.0, 2);
        cart.addItem(3.0, 4);
        List<Double> costs = cart.getItemCosts();
        assertEquals(2, costs.size());
        assertEquals(10.0, costs.get(0));
        assertEquals(12.0, costs.get(1));
    }

    @Test
    void testGetItems_isUnmodifiable() {
        // Fix for SonarQube Medium issue (L99):
        // Extract the method call that throws into its own single-expression lambda.
        // The list reference is obtained outside the lambda so the lambda only
        // contains the one statement that can throw UnsupportedOperationException.
        cart.addItem(5.0, 2);
        List<ShoppingCart.CartItem> items = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(null));
    }

    @Test
    void testCartItem_getters() {
        cart.addItem(7.5, 4);
        ShoppingCart.CartItem item = cart.getItems().get(0);
        assertEquals(1, item.getItemNumber());
        assertEquals(7.5, item.getPrice());
        assertEquals(4, item.getQuantity());
        assertEquals(30.0, item.getSubtotal());
    }

    @Test
    void testAddMultipleItems_itemNumbersIncrement() {
        cart.addItem(1.0, 1);
        cart.addItem(2.0, 1);
        cart.addItem(3.0, 1);
        assertEquals(1, cart.getItems().get(0).getItemNumber());
        assertEquals(2, cart.getItems().get(1).getItemNumber());
        assertEquals(3, cart.getItems().get(2).getItemNumber());
    }

    // ---- Tests for Main.getLocale() ----

    @Test
    void testGetLocale_english() {
        Locale locale = Main.getLocale(1);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testGetLocale_finnish() {
        Locale locale = Main.getLocale(2);
        assertEquals("fi", locale.getLanguage());
        assertEquals("FI", locale.getCountry());
    }

    @Test
    void testGetLocale_swedish() {
        Locale locale = Main.getLocale(3);
        assertEquals("sv", locale.getLanguage());
        assertEquals("SE", locale.getCountry());
    }

    @Test
    void testGetLocale_japanese() {
        Locale locale = Main.getLocale(4);
        assertEquals("ja", locale.getLanguage());
        assertEquals("JP", locale.getCountry());
    }

    @Test
    void testGetLocale_arabic() {
        Locale locale = Main.getLocale(5);
        assertEquals("ar", locale.getLanguage());
        assertEquals("AR", locale.getCountry());
    }

    @Test
    void testGetLocale_invalidChoiceDefaultsToEnglish() {
        Locale locale = Main.getLocale(99);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testGetLocale_zeroDefaultsToEnglish() {
        Locale locale = Main.getLocale(0);
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void testGetLocale_negativeDefaultsToEnglish() {
        Locale locale = Main.getLocale(-1);
        assertEquals("en", locale.getLanguage());
    }
}