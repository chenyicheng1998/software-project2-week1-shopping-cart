package com.spring;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.NodeOrientation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ShoppingCartController {
    @FXML
    private ComboBox<LocaleOption> languageCombo;
    @FXML
    private Label titleLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label numberOfItemsLabel;
    @FXML
    private TextField numberOfItemsField;
    @FXML
    private Label priceLabel;
    @FXML
    private TextField priceField;
    @FXML
    private Label quantityLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button calculateButton;
    @FXML
    private Label outputLabel;
    @FXML
    private TextArea outputArea;
    @FXML
    private VBox rootContainer;

    private final ShoppingCart shoppingCart = new ShoppingCart();
    private final LocalizationService localizationService = new LocalizationService();
    private final CartService cartService = new CartService();
    private Map<String, String> messages = new HashMap<>();

    @FXML
    public void initialize() {
        languageCombo.setItems(FXCollections.observableArrayList(
                new LocaleOption("English", "en_US"),
                new LocaleOption("Suomi", "fi_FI"),
                new LocaleOption("Svenska", "sv_SE"),
                new LocaleOption("日本語", "ja_JP"),
                new LocaleOption("العربية", "ar_AR")
        ));
        languageCombo.getSelectionModel().selectFirst();
        loadMessages(languageCombo.getValue().code());

        languageCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadMessages(newValue.code());
                shoppingCart.clear();
                outputArea.clear();
                applyDirection(newValue.code());
            }
        });
        applyDirection(languageCombo.getValue().code());
    }

    @FXML
    public void handleCalculate() {
        int expectedItems = parseExpectedItems();
        if (expectedItems > 0 && shoppingCart.getItems().size() != expectedItems) {
            appendOutput(getMessage("item.count.mismatch") + " " + expectedItems);
        }

        try {
            double total = shoppingCart.calculateTotalCost();
            appendOutput(getMessage("total.cost") + " " + String.format("%.2f", total));

            LocaleOption selected = languageCombo.getValue();
            cartService.saveCart(shoppingCart, selected != null ? selected.code() : "en_US");
            appendOutput(getMessage("saved.db"));
        } catch (NumberFormatException ex) {
            appendOutput(getMessage("invalid.number"));
        } catch (SQLException ex) {
            appendOutput(getMessage("db.error") + " " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            appendOutput(ex.getMessage());
        }
    }

    @FXML
    public void handleAddItem() {
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            shoppingCart.addItem(price, quantity);
            double subtotal = shoppingCart.calculateItemCost(price, quantity);
            String line = String.format("%s %d: %.2f x %d = %.2f",
                    getMessage("item"),
                    shoppingCart.getItems().size(),
                    price,
                    quantity,
                    subtotal);
            appendOutput(line);
            priceField.clear();
            quantityField.clear();
        } catch (NumberFormatException ex) {
            appendOutput(getMessage("invalid.number"));
        } catch (IllegalArgumentException ex) {
            appendOutput(ex.getMessage());
        }
    }

    private int parseExpectedItems() {
        String text = numberOfItemsField.getText();
        if (text == null || text.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            appendOutput(getMessage("invalid.number"));
            return 0;
        }
    }

    private void appendOutput(String line) {
        if (!outputArea.getText().isBlank()) {
            outputArea.appendText(System.lineSeparator());
        }
        outputArea.appendText(line);
    }

    private void loadMessages(String languageCode) {
        Map<String, String> dbMessages = new HashMap<>();
        try {
            dbMessages = localizationService.getMessagesByLanguage(languageCode);
        } catch (SQLException ex) {
            appendOutput(getMessage("db.error") + " " + ex.getMessage());
        }
        this.messages = buildMergedMessages(languageCode, dbMessages);
        refreshUiTexts();
    }

    private Map<String, String> buildMergedMessages(String languageCode, Map<String, String> dbMessages) {
        String[] parts = languageCode.split("_");
        Locale locale = parts.length == 2 ? new Locale(parts[0], parts[1]) : Locale.US;
        ResourceBundle defaultBundle = ResourceBundle.getBundle("MessagesBundle", Locale.US);
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);

        Map<String, String> merged = new HashMap<>();
        for (String key : defaultBundle.keySet()) {
            merged.put(key, defaultBundle.getString(key));
        }
        for (String key : bundle.keySet()) {
            merged.put(key, bundle.getString(key));
        }
        merged.putAll(dbMessages);
        return merged;
    }

    private String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }

    private void refreshUiTexts() {
        titleLabel.setText(getMessage("app.title") + " - CHEN Yicheng");
        languageLabel.setText(getMessage("select.language"));
        numberOfItemsLabel.setText(getMessage("enter.num.items"));
        priceLabel.setText(getMessage("enter.price"));
        quantityLabel.setText(getMessage("enter.quantity"));
        addItemButton.setText(getMessage("add.item"));
        calculateButton.setText(getMessage("calculate.total"));
        outputLabel.setText(getMessage("result.log"));
        numberOfItemsField.setPromptText(getMessage("enter.num.items"));
        priceField.setPromptText(getMessage("enter.price"));
        quantityField.setPromptText(getMessage("enter.quantity"));
    }

    private void applyDirection(String languageCode) {
        boolean isArabic = "ar_AR".equals(languageCode);
        NodeOrientation orientation = isArabic ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
        rootContainer.setNodeOrientation(orientation);
        outputArea.setNodeOrientation(orientation);
    }

    private record LocaleOption(String label, String code) {
        @Override
        public String toString() {
            return label;
        }
    }
}
