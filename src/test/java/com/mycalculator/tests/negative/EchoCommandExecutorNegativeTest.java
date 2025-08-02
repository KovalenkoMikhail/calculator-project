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
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("10 / 0");
        }, "Division by zero should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(
            thrown.getMessage().toLowerCase().contains("divide by zero") ||
            thrown.getMessage().toLowerCase().contains("runtime error"),
            "Error message should contain 'divide by zero' or 'runtime error', actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Invalid expression - Error Reporting")
    void testBcInvalidExpression() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("5 +");
        }, "Invalid expression should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Empty input - Error Reporting")
    void testBcEmptyInput() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("");
        }, "Empty input should cause an IllegalArgumentException from ExprCommandExecutor");
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
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("5 + $");
        }, "Invalid character should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Division by zero with float - Error Reporting")
    void testBcDivideByZeroFloat() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("10.0 / 0");
        }, "Division by zero (float) should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(
            thrown.getMessage().toLowerCase().contains("divide by zero") ||
            thrown.getMessage().toLowerCase().contains("runtime error"),
            "Error message should contain 'divide by zero' or 'runtime error', actual: " + thrown.getMessage()
        );
    }

    @Test
    @DisplayName("BC: Only operator - Error Reporting")
    void testBcOnlyOperator() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            echoCommandExecutor.executeExprCommand("+");
        }, "Only operator should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(
            thrown.getMessage().toLowerCase().contains("syntax error") ||
            thrown.getMessage().toLowerCase().contains("parse error"),
            "Error message should indicate syntax/parse error from bc, actual: " + thrown.getMessage()
        );
    }
}
