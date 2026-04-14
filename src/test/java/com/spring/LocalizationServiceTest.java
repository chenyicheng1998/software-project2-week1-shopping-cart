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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocalizationServiceTest {

    private LocalizationService localizationService;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        localizationService = new LocalizationService();
    }

    @Test
    void testGetMessagesByLanguageReturnsMap() throws SQLException {
        // Mock the result set
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("key")).thenReturn("total.cost", "enter.price");
        when(mockResultSet.getString("value")).thenReturn("Total cost:", "Enter price:");

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        // Mock static DatabaseConnection
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Map<String, String> messages = localizationService.getMessagesByLanguage("en_US");

            assertNotNull(messages);
            assertEquals(2, messages.size());
            assertEquals("Total cost:", messages.get("total.cost"));
            assertEquals("Enter price:", messages.get("enter.price"));
        }
    }

    @Test
    void testGetMessagesByLanguageEmptyResult() throws SQLException {
        // Mock empty result set
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Map<String, String> messages = localizationService.getMessagesByLanguage("en_US");

            assertNotNull(messages);
            assertTrue(messages.isEmpty());
        }
    }

    @Test
    void testGetMessagesByLanguageSqlException() throws SQLException {
        // Mock SQL exception
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            SQLException exception = assertThrows(SQLException.class, () -> {
                localizationService.getMessagesByLanguage("en_US");
            });

            assertEquals("Database error", exception.getMessage());
        }
    }

    @Test
    void testGetMessagesByLanguageWithMultipleLanguages() throws SQLException {
        // Test with different language codes
        String[] languages = {"en_US", "fi_FI", "sv_SE", "ja_JP", "ar_AR"};

        for (String language : languages) {
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getString("key")).thenReturn("test.key");
            when(mockResultSet.getString("value")).thenReturn("Test Value");

            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
                mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

                Map<String, String> messages = localizationService.getMessagesByLanguage(language);

                assertNotNull(messages);
                assertEquals(1, messages.size());
                assertEquals("Test Value", messages.get("test.key"));
            }
        }
    }

    @Test
    void testGetMessagesByLanguageConnectionException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenThrow(new SQLException("Connection failed"));

            SQLException exception = assertThrows(SQLException.class, () -> {
                localizationService.getMessagesByLanguage("en_US");
            });

            assertEquals("Connection failed", exception.getMessage());
        }
    }

    @Test
    void testGetMessagesByLanguageWithLargeDataset() throws SQLException {
        // Test with many messages
        when(mockResultSet.next())
                .thenReturn(true, true, true, true, true, false);
        when(mockResultSet.getString("key"))
                .thenReturn("key1", "key2", "key3", "key4", "key5");
        when(mockResultSet.getString("value"))
                .thenReturn("value1", "value2", "value3", "value4", "value5");

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Map<String, String> messages = localizationService.getMessagesByLanguage("en_US");

            assertNotNull(messages);
            assertEquals(5, messages.size());
            assertEquals("value1", messages.get("key1"));
            assertEquals("value5", messages.get("key5"));
        }
    }

    @Test
    void testGetMessagesByLanguageResultSetException() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("ResultSet error"));
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            SQLException exception = assertThrows(SQLException.class, () -> {
                localizationService.getMessagesByLanguage("en_US");
            });

            assertEquals("ResultSet error", exception.getMessage());
        }
    }
}

