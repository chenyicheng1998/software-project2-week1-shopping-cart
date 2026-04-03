package com.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ShoppingCart class handles the logic for calculating item costs and total cart cost.
 */
public class ShoppingCart {

    private final List<CartItem> items = new ArrayList<>();

    public static class CartItem {
        private final int itemNumber;
        private final double price;
        private final int quantity;
        private final double subtotal;

        public CartItem(int itemNumber, double price, int quantity, double subtotal) {
            this.itemNumber = itemNumber;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public int getItemNumber() {
            return itemNumber;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getSubtotal() {
            return subtotal;
        }
    }

    /**
     * Calculates the cost of a single item (price multiplied by quantity).
     *
     * @param price    the price of one unit of the item
     * @param quantity the number of units
     * @return the total cost for this item
     * @throws IllegalArgumentException if price or quantity is negative
     */
    public double calculateItemCost(double price, int quantity) {
        if (price < 0 || quantity < 0) {
            throw new IllegalArgumentException("Price and quantity must not be negative.");
        }
        return price * quantity;
    }

    /**
     * Adds an item to the shopping cart.
     *
     * @param price    the price per unit
     * @param quantity the number of units
     */
    public void addItem(double price, int quantity) {
        double subtotal = calculateItemCost(price, quantity);
        int itemNumber = items.size() + 1;
        items.add(new CartItem(itemNumber, price, quantity, subtotal));
    }

    /**
     * Calculates the total cost of all items in the cart.
     *
     * @return the sum of all item costs
     */
    public double calculateTotalCost() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * Clears all items from the shopping cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns the list of individual item costs.
     *
     * @return list of item costs
     */
    public List<Double> getItemCosts() {
        List<Double> costs = new ArrayList<>();
        for (CartItem item : items) {
            costs.add(item.getSubtotal());
        }
        return costs;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
