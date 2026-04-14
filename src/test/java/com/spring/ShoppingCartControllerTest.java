package com.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartControllerTest {

    private static volatile boolean fxStarted;

    private ShoppingCartController controller;
    private ComboBox<Object> languageCombo;
    private Label titleLabel;
    private Label languageLabel;
    private Label numberOfItemsLabel;
    private TextField numberOfItemsField;
    private Label priceLabel;
    private TextField priceField;
    private Label quantityLabel;
    private TextField quantityField;
    private Button addItemButton;
    private Button calculateButton;
    private Label outputLabel;
    private TextArea outputArea;
    private VBox rootContainer;

    @BeforeAll
    static void startJavaFx() throws Exception {
        if (fxStarted) {
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            fxStarted = true;
            return;
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not start in time");
        fxStarted = true;
    }

    @BeforeEach
    void setUp() throws Exception {
        runOnFxThread(() -> {
            controller = new ShoppingCartController();
            languageCombo = new ComboBox<>();
            titleLabel = new Label();
            languageLabel = new Label();
            numberOfItemsLabel = new Label();
            numberOfItemsField = new TextField();
            priceLabel = new Label();
            priceField = new TextField();
            quantityLabel = new Label();
            quantityField = new TextField();
            addItemButton = new Button();
            calculateButton = new Button();
            outputLabel = new Label();
            outputArea = new TextArea();
            rootContainer = new VBox();

            inject("languageCombo", languageCombo);
            inject("titleLabel", titleLabel);
            inject("languageLabel", languageLabel);
            inject("numberOfItemsLabel", numberOfItemsLabel);
            inject("numberOfItemsField", numberOfItemsField);
            inject("priceLabel", priceLabel);
            inject("priceField", priceField);
            inject("quantityLabel", quantityLabel);
            inject("quantityField", quantityField);
            inject("addItemButton", addItemButton);
            inject("calculateButton", calculateButton);
            inject("outputLabel", outputLabel);
            inject("outputArea", outputArea);
            inject("rootContainer", rootContainer);
        });
    }

    @Test
    void testInitializeAndAddItemUpdatesUiAndCart() throws Exception {
        runOnFxThread(() -> {
            try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
                Connection connection = mock(Connection.class);
                PreparedStatement statement = mock(PreparedStatement.class);
                ResultSet resultSet = mock(ResultSet.class);

                when(resultSet.next()).thenReturn(false);
                when(statement.executeQuery()).thenReturn(resultSet);
                when(connection.prepareStatement(anyString())).thenReturn(statement);
                mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);

                controller.initialize();

                priceField.setText("10.0");
                quantityField.setText("2");
                controller.handleAddItem();

                assertEquals(1, getShoppingCart().getItems().size());
                assertTrue(outputArea.getText().contains("20.00"));
                assertEquals("", priceField.getText());
                assertEquals("", quantityField.getText());
            }
        });
    }

    @Test
    void testHandleCalculateSavesCartAndAppendsTotal() throws Exception {
        runOnFxThread(() -> {
            try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
                Connection connection = mock(Connection.class);
                PreparedStatement localizationStatement = mock(PreparedStatement.class);
                PreparedStatement cartRecordStatement = mock(PreparedStatement.class);
                PreparedStatement cartItemsStatement = mock(PreparedStatement.class);
                ResultSet localizationResultSet = mock(ResultSet.class);
                ResultSet generatedKeys = mock(ResultSet.class);

                when(localizationResultSet.next()).thenReturn(false);
                when(localizationStatement.executeQuery()).thenReturn(localizationResultSet);
                when(connection.prepareStatement(anyString())).thenReturn(localizationStatement);
                when(connection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                        .thenReturn(cartRecordStatement);
                when(connection.prepareStatement(contains("INSERT INTO cart_items"))).thenReturn(cartItemsStatement);
                when(generatedKeys.next()).thenReturn(true);
                when(generatedKeys.getInt(1)).thenReturn(1);
                when(cartRecordStatement.getGeneratedKeys()).thenReturn(generatedKeys);

                mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);

                controller.initialize();
                priceField.setText("10.0");
                quantityField.setText("2");
                controller.handleAddItem();
                numberOfItemsField.setText("1");
                controller.handleCalculate();

                verify(connection).setAutoCommit(false);
                verify(connection).commit();
                verify(cartItemsStatement).executeBatch();
                assertTrue(outputArea.getText().contains("20.00"));
                assertFalse(outputArea.getText().isBlank());
            }
        });
    }

    @Test
    void testLanguageSelectionUpdatesDirectionAndFont() throws Exception {
        runOnFxThread(() -> {
            try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
                Connection connection = mock(Connection.class);
                PreparedStatement statement = mock(PreparedStatement.class);
                ResultSet resultSet = mock(ResultSet.class);

                when(resultSet.next()).thenReturn(false);
                when(statement.executeQuery()).thenReturn(resultSet);
                when(connection.prepareStatement(anyString())).thenReturn(statement);
                mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);

                controller.initialize();

                languageCombo.getSelectionModel().select(4);
                assertEquals(NodeOrientation.RIGHT_TO_LEFT, rootContainer.getNodeOrientation());
                assertEquals(NodeOrientation.RIGHT_TO_LEFT, outputArea.getNodeOrientation());

                languageCombo.getSelectionModel().select(3);
                assertTrue(rootContainer.getStyle().contains("Noto Sans CJK JP"));
                assertTrue(outputArea.getStyle().contains("Noto Sans CJK JP"));
            }
        });
    }

    @Test
    void testControllerMethodsExist() {
        assertDoesNotThrow(() -> {
            assertNotNull(ShoppingCartController.class.getDeclaredMethod("initialize"));
            assertNotNull(ShoppingCartController.class.getDeclaredMethod("handleAddItem"));
            assertNotNull(ShoppingCartController.class.getDeclaredMethod("handleCalculate"));
            assertNotNull(ShoppingCartController.class.getDeclaredMethod("getMessage", String.class));
            assertNotNull(ShoppingCartController.class.getDeclaredMethod("loadMessages", String.class));
        });
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = ShoppingCartController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private ShoppingCart getShoppingCart() throws Exception {
        Field field = ShoppingCartController.class.getDeclaredField("shoppingCart");
        field.setAccessible(true);
        return (ShoppingCart) field.get(controller);
    }

    private void runOnFxThread(FxTask task) throws Exception {
        if (Platform.isFxApplicationThread()) {
            task.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Throwable[] failure = new Throwable[1];
        Platform.runLater(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                failure[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out waiting for JavaFX task");
        if (failure[0] != null) {
            if (failure[0] instanceof Exception exception) {
                throw exception;
            }
            if (failure[0] instanceof Error error) {
                throw error;
            }
            throw new RuntimeException(failure[0]);
        }
    }

    @FunctionalInterface
    private interface FxTask {
        void run() throws Exception;
    }
}

