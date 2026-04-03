package com.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {

    private static final String QUERY = "SELECT `key`, value FROM localization_strings WHERE language = ?";

    public Map<String, String> getMessagesByLanguage(String languageCode) throws SQLException {
        Map<String, String> messages = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {
            statement.setString(1, languageCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String key = resultSet.getString("key");
                    String value = resultSet.getString("value");
                    messages.put(key, value);
                }
            }
        }

        return messages;
    }
}
