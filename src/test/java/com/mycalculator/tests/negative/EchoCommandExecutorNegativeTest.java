package com.mycalculator.tests.negative;

import com.mycalculator.tests.EchoCommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EchoCommandExecutorNegativeTest {
    private EchoCommandExecutor echoCommandExecutor;

    @BeforeEach
    void setUp() {
        echoCommandExecutor = new EchoCommandExecutor();
    }

    @Test
    @DisplayName("BC: Division by zero - Error Reporting")
    void testBcDivideByZero() {
        // Why: Division by zero should always throw an error in bc
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("10 / 0");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("divide by zero") ||
            thrown.getMessage().toLowerCase().contains("divide by 0") ||
            thrown.getMessage().toLowerCase().contains("runtime error"),
            "Error message should contain 'divide by zero' or 'runtime error', actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Invalid expression - Error Reporting")
    void testBcInvalidExpression() {
        // Why: Invalid syntax should be caught and reported
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("5 +");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Empty input - Error Reporting")
    void testBcEmptyInput() {
        // Why: Empty input should not be accepted by bc
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error") ||
            thrown.getMessage().toLowerCase().contains("empty output"),
            "Error message should indicate syntax/parse error or empty output from bc, actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Invalid character - Error Reporting")
    void testBcInvalidCharacter() {
        // Why: Invalid characters should be rejected by bc
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("5 + $");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Division by zero with float - Error Reporting")
    void testBcDivideByZeroFloat() {
        // Why: Division by zero with float should also throw
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("10.0 / 0");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("divide by zero") ||
            thrown.getMessage().toLowerCase().contains("divide by 0") ||
            thrown.getMessage().toLowerCase().contains("runtime error"),
            "Error message should contain 'divide by zero' or 'runtime error', actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Only operator - Error Reporting")
    void testBcOnlyOperator() {
        // Why: Only operator should not be accepted by bc
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeEchoCommand("+");
        });
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }
}
