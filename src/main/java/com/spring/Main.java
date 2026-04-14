package com.spring;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Main class for the localized shopping cart console application.
 * Supports English, Finnish, Swedish, Japanese, and Arabic based on user selection.
 */
public class Main {

    private final Scanner scanner;
    private final PrintStream out;
    private final ShoppingCart cart;

    // Constructor for dependency injection (for testing)
    public Main(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
        this.cart = new ShoppingCart();
    }

    // Default constructor for production use
    public Main() {
        this(System.in, System.out);
    }

    public static void main(String[] args) throws Exception {
        // Set console output to UTF-8 to support non-Latin characters (e.g., Japanese)
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Main main = new Main();
        main.run();
    }

    public void run() {
        // Display language selection menu (always in a universal format)
        out.println("Select language / Valitse kieli / Valj sprak / Gengo wo sentaku / Ikhtar allugha:");
        out.println("1. English");
        out.println("2. Finnish (Suomi)");
        out.println("3. Swedish (Svenska)");
        out.println("4. Japanese (Nihongo)");
        out.println("5. Arabic (العربية)");
        out.print("Enter choice (1-5): ");

        int langChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Get Locale object based on user's language choice
        Locale locale = getLocale(langChoice);

        // Load the corresponding ResourceBundle (properties file) for the chosen locale
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);

        // Ask user how many items they want to purchase
        out.print(messages.getString("enter.num.items") + " ");
        int numItems = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Loop through each item and collect price and quantity
        for (int i = 1; i <= numItems; i++) {
            out.print(messages.getString("enter.price") + " " + i + ": ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            out.print(messages.getString("enter.quantity") + " " + i + ": ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // consume newline

            // Add item to cart
            cart.addItem(price, quantity);
        }

        // Calculate and display the total cost
        double total = cart.calculateTotalCost();
        out.printf("%s %.2f%n", messages.getString("total.cost"), total);
    }

    /**
     * Returns the appropriate Locale based on the user's menu selection.
     *
     * @param choice the number entered by the user (1-5)
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
            case 5:
                language = "ar";
                country = "AR";
                break;
            default:
                language = "en";
                country = "US";
                break;
        }

        // Create Locale object using language and country code (as shown in lecture)
        return new Locale(language, country);
    }

    // Getters for testing
    public ShoppingCart getCart() {
        return cart;
    }
}