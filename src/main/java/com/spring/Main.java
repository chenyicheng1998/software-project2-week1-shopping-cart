package com.spring;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Main class for the localized shopping cart console application.
 * Supports English, Finnish, Swedish, and Japanese based on user selection.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Set console output to UTF-8 to support non-Latin characters (e.g., Japanese)
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        ShoppingCart cart = new ShoppingCart();

        // Display language selection menu (always in a universal format)
        System.out.println("Select language / Valitse kieli / Valj sprak / Gengo wo sentaku:");
        System.out.println("1. English");
        System.out.println("2. Finnish (Suomi)");
        System.out.println("3. Swedish (Svenska)");
        System.out.println("4. Japanese (Nihongo)");
        System.out.print("Enter choice (1-4): ");

        int langChoice = scanner.nextInt();

        // Get Locale object based on user's language choice
        Locale locale = getLocale(langChoice);

        // Load the corresponding ResourceBundle (properties file) for the chosen locale
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);

        // Ask user how many items they want to purchase
        System.out.print(messages.getString("enter.num.items") + " ");
        int numItems = scanner.nextInt();

        // Loop through each item and collect price and quantity
        for (int i = 1; i <= numItems; i++) {
            System.out.print(messages.getString("enter.price") + " " + i + ": ");
            double price = scanner.nextDouble();

            System.out.print(messages.getString("enter.quantity") + " " + i + ": ");
            int quantity = scanner.nextInt();

            // Add item to cart
            cart.addItem(price, quantity);
        }

        // Calculate and display the total cost
        double total = cart.calculateTotalCost();
        System.out.printf("%s %.2f%n", messages.getString("total.cost"), total);

        scanner.close();
    }

    /**
     * Returns the appropriate Locale based on the user's menu selection.
     *
     * @param choice the number entered by the user (1-4)
     * @return the corresponding Locale object
     */
    public static Locale getLocale(int choice) {
        String language;
        String country;

        switch (choice) {
            case 2:
                language = "fi";
                country = "FI";
                break;
            case 3:
                language = "sv";
                country = "SE";
                break;
            case 4:
                language = "ja";
                country = "JP";
                break;
            default:
                language = "en";
                country = "US";
                break;
        }

        // Create Locale object using language and country code (as shown in lecture)
        return new Locale(language, country);
    }
}