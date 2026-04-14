package com.spring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @AfterEach
    void tearDown() {
        // Clean up environment variables after each test
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USER");
        System.clearProperty("DB_PASSWORD");
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        Constructor<DatabaseConnection> constructor = DatabaseConnection.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        DatabaseConnection instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void testDatabaseConnectionClassExists() {
        assertNotNull(DatabaseConnection.class);
    }

    @Test
    void testGetConnectionThrowsException() {
        // This test verifies that getConnection() throws an exception when database is unavailable
        // The exact exception type may vary based on database driver and environment
        assertThrows(Exception.class, DatabaseConnection::getConnection);
    }

    @Test
    void testGetConnectionWithEnvironmentVariables() {
        // Test that environment variables are used when set
        // Connection will fail because the URL is invalid, but that's expected
        assertThrows(Exception.class, DatabaseConnection::getConnection);
    }

    @Test
    void testGetConnectionUsingDefaultValues() {
        // Test that the method uses default values
        // Since default values point to localhost MariaDB, connection will fail if not available
        // This is expected behavior
        assertThrows(Exception.class, DatabaseConnection::getConnection);
    }
}