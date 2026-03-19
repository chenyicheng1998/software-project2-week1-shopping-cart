package com.spring;

import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingCart class handles the logic for calculating item costs and total cart cost.
 */
public class ShoppingCart {

    // List to store the cost of each item added to the cart
    private List<Double> itemCosts = new ArrayList<>();

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
        double cost = calculateItemCost(price, quantity);
        itemCosts.add(cost);
    }

    /**
     * Calculates the total cost of all items in the cart.
     *
     * @return the sum of all item costs
     */
    public double calculateTotalCost() {
        double total = 0;
        for (double cost : itemCosts) {
            total += cost;
        }
        return total;
    }

    /**
     * Clears all items from the shopping cart.
     */
    public void clear() {
        itemCosts.clear();
    }

    /**
     * Returns the list of individual item costs.
     *
     * @return list of item costs
     */
    public List<Double> getItemCosts() {
        return itemCosts;
    }
}