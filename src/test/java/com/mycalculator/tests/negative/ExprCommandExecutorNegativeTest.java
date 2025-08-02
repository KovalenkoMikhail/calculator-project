package com.mycalculator.tests.negative;

import com.mycalculator.tests.ExprCommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExprCommandExecutorNegativeTest {
    private ExprCommandExecutor exprCommandExecutor;

    @BeforeEach
    void setUp() {
        exprCommandExecutor = new ExprCommandExecutor();
    }

    @Test
    @DisplayName("BC: Division by zero - Error Reporting")
    void testBcDivideByZero() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("10 / 0");
        }, "Division by zero should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(thrown.getMessage().toLowerCase().contains("divide by zero"),
                "Error message should contain 'divide by zero', actual: " + thrown.getMessage());
    }

    @Test
    @DisplayName("BC: Invalid expression - Error Reporting")
    void testBcInvalidExpression() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("5 +");
        }, "Invalid expression should cause an IllegalArgumentException from ExprCommandExecutor");
        assertTrue(thrown.getMessage().toLowerCase().contains("syntax error"),
                "Error message should indicate syntax error from bc, actual: " + thrown.getMessage());
    }
}
