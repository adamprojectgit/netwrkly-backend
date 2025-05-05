package com.netwrkly.auth.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyValidatorTest {

    private PasswordPolicyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordPolicyValidator();
    }

    @Test
    void validatePassword_ValidPassword_NoException() {
        // Test various valid passwords
        String[] validPasswords = {
            "Password1",
            "Test1234",
            "Abc12345",
            "MyPass123",
            "Complex1Password"
        };

        for (String password : validPasswords) {
            assertDoesNotThrow(() -> validator.validatePassword(password),
                "Password '" + password + "' should be valid");
        }
    }

    @Test
    void validatePassword_TooShort_ThrowsException() {
        String[] shortPasswords = {
            "Pass1",
            "Test12",
            "Abc123"
        };

        for (String password : shortPasswords) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePassword(password));
            assertTrue(exception.getMessage().contains("8 characters"));
        }
    }

    @Test
    void validatePassword_NoNumber_ThrowsException() {
        String[] noNumberPasswords = {
            "Password",
            "TestTest",
            "AbcDefGh"
        };

        for (String password : noNumberPasswords) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePassword(password));
            assertTrue(exception.getMessage().contains("digit"));
        }
    }

    @Test
    void validatePassword_NoUpperCase_ThrowsException() {
        String[] noUpperCasePasswords = {
            "password1",
            "test1234",
            "abcdef1"
        };

        for (String password : noUpperCasePasswords) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePassword(password));
            assertTrue(exception.getMessage().contains("uppercase"));
        }
    }

    @Test
    void validatePassword_NullOrEmpty_ThrowsException() {
        String[] emptyPasswords = {
            null,
            "",
            "   "
        };

        for (String password : emptyPasswords) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePassword(password));
            assertTrue(exception.getMessage().contains("empty"));
        }
    }
} 