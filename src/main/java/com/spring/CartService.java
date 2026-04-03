package com.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CartService {

    private static final String INSERT_CART_RECORD =
            "INSERT INTO cart_records(total_items, total_cost, language) VALUES (?, ?, ?)";
    private static final String INSERT_CART_ITEM =
            "INSERT INTO cart_items(cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

    public void saveCart(ShoppingCart shoppingCart, String languageCode) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int cartRecordId = insertCartRecord(connection, shoppingCart, languageCode);
                insertCartItems(connection, cartRecordId, shoppingCart);
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private int insertCartRecord(Connection connection, ShoppingCart shoppingCart, String languageCode) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CART_RECORD, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, shoppingCart.getItems().size());
            statement.setDouble(2, shoppingCart.calculateTotalCost());
            statement.setString(3, languageCode);
            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert cart record and obtain generated ID.");
    }

    private void insertCartItems(Connection connection, int cartRecordId, ShoppingCart shoppingCart) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CART_ITEM)) {
            for (ShoppingCart.CartItem item : shoppingCart.getItems()) {
                statement.setInt(1, cartRecordId);
                statement.setInt(2, item.getItemNumber());
                statement.setDouble(3, item.getPrice());
                statement.setInt(4, item.getQuantity());
                statement.setDouble(5, item.getSubtotal());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
