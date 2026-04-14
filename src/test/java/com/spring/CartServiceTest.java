package com.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private CartService cartService;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockCartRecordStatement;

    @Mock
    private PreparedStatement mockCartItemsStatement;

    @Mock
    private ResultSet mockGeneratedKeys;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void testSaveCartWithSingleItem() throws SQLException {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(10.0, 2);

        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);

        when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockCartRecordStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_items")))
                .thenReturn(mockCartItemsStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Act
            cartService.saveCart(cart, "en_US");

            // Assert
            verify(mockConnection).setAutoCommit(false);
            verify(mockCartRecordStatement).setInt(1, 1);
            verify(mockCartRecordStatement).setDouble(2, 20.0);
            verify(mockCartRecordStatement).setString(3, "en_US");
            verify(mockCartRecordStatement).executeUpdate();
            verify(mockCartItemsStatement).executeBatch();
            verify(mockConnection).commit();
            verify(mockConnection).setAutoCommit(true);
        }
    }

    @Test
    void testSaveCartWithMultipleItems() throws SQLException {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(10.0, 2);
        cart.addItem(5.0, 3);
        cart.addItem(2.5, 4);

        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(42);

        when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockCartRecordStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_items")))
                .thenReturn(mockCartItemsStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Act
            cartService.saveCart(cart, "fi_FI");

            // Assert
            verify(mockConnection).setAutoCommit(false);
            verify(mockCartRecordStatement).setInt(1, 3);
            verify(mockCartRecordStatement).setDouble(2, 45.0);
            verify(mockCartRecordStatement).setString(3, "fi_FI");
            verify(mockCartItemsStatement, times(3)).addBatch();
            verify(mockCartItemsStatement).executeBatch();
            verify(mockConnection).commit();
        }
    }

    @Test
    void testSaveCartRollbackOnException() throws SQLException {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(10.0, 1);

        when(mockGeneratedKeys.next()).thenReturn(false); // No generated keys
        when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockCartRecordStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Act & Assert
            assertThrows(SQLException.class, () -> cartService.saveCart(cart, "en_US"));

            verify(mockConnection).setAutoCommit(false);
            verify(mockConnection).rollback();
            verify(mockConnection).setAutoCommit(true);
        }
    }

    @Test
    void testSaveCartWithDifferentLanguages() throws SQLException {
        // Arrange
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(15.0, 1);

        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(10);

        when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockCartRecordStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_items")))
                .thenReturn(mockCartItemsStatement);

        String[] languages = {"en_US", "fi_FI", "sv_SE", "ja_JP", "ar_AR"};

        for (String language : languages) {
            try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
                mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
                reset(mockConnection, mockCartRecordStatement, mockCartItemsStatement);

                when(mockGeneratedKeys.next()).thenReturn(true);
                when(mockGeneratedKeys.getInt(1)).thenReturn(10);
                when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
                when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                        .thenReturn(mockCartRecordStatement);
                when(mockConnection.prepareStatement(contains("INSERT INTO cart_items")))
                        .thenReturn(mockCartItemsStatement);

                // Act
                cartService.saveCart(cart, language);

                // Assert
                verify(mockCartRecordStatement).setString(3, language);
            }
        }
    }

    @Test
    void testSaveCartWithEmptyCart() throws SQLException {
        // Arrange
        ShoppingCart cart = new ShoppingCart();

        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(1);

        when(mockCartRecordStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockCartRecordStatement);
        when(mockConnection.prepareStatement(contains("INSERT INTO cart_items")))
                .thenReturn(mockCartItemsStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Act
            cartService.saveCart(cart, "en_US");

            // Assert
            verify(mockConnection).setAutoCommit(false);
            verify(mockCartRecordStatement).setInt(1, 0);
            verify(mockCartItemsStatement, never()).addBatch();
            verify(mockCartItemsStatement).executeBatch();
            verify(mockConnection).commit();
        }
    }
}

