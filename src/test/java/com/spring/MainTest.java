package com.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream testOut;
    private ByteArrayInputStream inputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        testOut = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
    }

    @AfterEach
    void tearDown() {
        try {
            if (testOut != null) testOut.close();
        } catch (Exception e) {
            // ignore
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, en, US",
            "2, fi, FI",
            "3, sv, SE",
            "4, ja, JP",
            "5, ar, AR"
    })
    void testGetLocaleAllOptions(int choice, String expectedLanguage, String expectedCountry) {
        Locale locale = Main.getLocale(choice);
        assertEquals(expectedLanguage, locale.getLanguage());
        assertEquals(expectedCountry, locale.getCountry());
    }

    @Test
    void testGetLocaleInvalidDefaultsToEnglish() {
        Locale locale = Main.getLocale(99);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testRunWithEnglishAndOneItem() {
        String input = "1\n1\n10.0\n2\n";
        inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Main main = new Main(inputStream, testOut);
        assertDoesNotThrow(() -> main.run());

        assertEquals(20.0, main.getCart().calculateTotalCost());
    }

    @Test
    void testRunWithFinnishAndMultipleItems() {
        String input = "2\n2\n10.0\n2\n5.0\n3\n";
        inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Main main = new Main(inputStream, testOut);
        assertDoesNotThrow(() -> main.run());

        assertEquals(35.0, main.getCart().calculateTotalCost());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 10.0, 2, 20.0",
            "2, 2, 5.0, 4, 40.0",
            "3, 3, 2.0, 5, 30.0"
    })
    void testRunWithDifferentLanguagesAndItems(int languageChoice, int numItems,
                                               double price, int quantity, double expectedTotal) {
        StringBuilder input = new StringBuilder();
        input.append(languageChoice).append("\n");
        input.append(numItems).append("\n");
        for (int i = 0; i < numItems; i++) {
            input.append(price).append("\n");
            input.append(quantity).append("\n");
        }

        inputStream = new ByteArrayInputStream(input.toString().getBytes(StandardCharsets.UTF_8));
        Main main = new Main(inputStream, testOut);

        assertDoesNotThrow(() -> main.run());

        double expected = price * quantity * numItems;
        assertEquals(expected, main.getCart().calculateTotalCost(), 0.001);
    }

    @Test
    void testRunWithZeroItems() {
        String input = "1\n0\n";
        inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Main main = new Main(inputStream, testOut);
        assertDoesNotThrow(() -> main.run());

        assertEquals(0.0, main.getCart().calculateTotalCost());
    }
}