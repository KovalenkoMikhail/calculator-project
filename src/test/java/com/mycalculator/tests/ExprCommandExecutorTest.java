// src/test/java/com/mycalculator.tests/ExprCommandExecutorTest.java
// Ensure the package name matches your project structure (com.mycalculator.tests)

package com.mycalculator.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for ExprCommandExecutor.java using JUnit 5.
 * Tests the behavior of the 'expr' command through the executor,
 * covering basic arithmetic, error reporting, and limits.
 */
public class ExprCommandExecutorTest {

    private ExprCommandExecutor exprCommandExecutor; // Our "System Under Test"

    /**
     * Initializes a new ExprCommandExecutor instance before each test
     * to ensure a clean test state.
     */
    @BeforeEach
    void setUp() {
        exprCommandExecutor = new ExprCommandExecutor();
    }

    @Test
    @DisplayName("Expr: Addition of positive numbers")
    void testExprAddPositive() throws IOException, InterruptedException {
        assertEquals("8", exprCommandExecutor.executeExprCommand("5 + 3"));
    }

    @Test
    @DisplayName("Expr: Subtraction of positive numbers")
    void testExprSubtractPositive() throws IOException, InterruptedException {
        assertEquals("2", exprCommandExecutor.executeExprCommand("5 - 3"));
    }

    @Test
    @DisplayName("Expr: Multiplication of positive numbers")
    void testExprMultiplyPositive() throws IOException, InterruptedException {
        assertEquals("15", exprCommandExecutor.executeExprCommand("5 \\* 3")); // Note: '*' needs to be escaped in expr
    }

    @Test
    @DisplayName("Expr: Division of positive numbers")
    void testExprDividePositive() throws IOException, InterruptedException {
        assertEquals("2", exprCommandExecutor.executeExprCommand("6 / 3"));
    }

    @Test
    @DisplayName("Expr: Addition with negative numbers")
    void testExprAddNegative() throws IOException, InterruptedException {
        assertEquals("-2", exprCommandExecutor.executeExprCommand("-5 + 3"));
        assertEquals("-8", exprCommandExecutor.executeExprCommand("-5 + -3"));
    }

    @Test
    @DisplayName("Expr: Subtraction with negative numbers")
    void testExprSubtractNegative() throws IOException, InterruptedException {
        assertEquals("-8", exprCommandExecutor.executeExprCommand("-5 - 3"));
        assertEquals("-2", exprCommandExecutor.executeExprCommand("3 - 5"));
    }

    @Test
    @DisplayName("Expr: Multiplication with negative numbers")
    void testExprMultiplyNegative() throws IOException, InterruptedException {
        assertEquals("-15", exprCommandExecutor.executeExprCommand("-5 \\* 3"));
        assertEquals("15", exprCommandExecutor.executeExprCommand("-5 \\* -3"));
    }

    @Test
    @DisplayName("Expr: Division with negative numbers")
    void testExprDivideNegative() throws IOException, InterruptedException {
        assertEquals("-2", exprCommandExecutor.executeExprCommand("-6 / 3"));
        assertEquals("2", exprCommandExecutor.executeExprCommand("-6 / -3"));
    }

    @Test
    @DisplayName("Expr: Addition with zero")
    void testExprAddZero() throws IOException, InterruptedException {
        // Based on user's expr output: expr 5 + 0 -> 5 (exit code 0)
        assertEquals("5", exprCommandExecutor.executeExprCommand("5 + 0"));
        // Based on user's expr output: expr 0 + 0 -> 0 (exit code 1)
        assertEquals("0", exprCommandExecutor.executeExprCommand("0 + 0"));
    }

    @Test
    @DisplayName("Expr: Multiplication by zero")
    void testExprMultiplyZero() throws IOException, InterruptedException {
        // Based on user's expr output: expr 5 \* 0 -> 0 (exit code 1)
        assertEquals("0", exprCommandExecutor.executeExprCommand("5 \\* 0"));
        assertEquals("0", exprCommandExecutor.executeExprCommand("0 \\* 0"));
    }

    @Test
    @DisplayName("Expr: Division by zero - Error Reporting")
    void testExprDivideByZero() {
        // 'expr' reports "division by zero" error and exits with code 2
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("10 / 0");
        }, "Division by zero should cause an IllegalArgumentException from ExprCommandExecutor");

        // Verify the error message from 'expr' itself
        assertTrue(thrown.getMessage().contains("expr: division by zero"),
                "Error message should contain 'division by zero'");
        // Verify the exit code reported by ExprCommandExecutor
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
    }

    @Test
    @DisplayName("Expr: Invalid expression - Error Reporting")
    void testExprInvalidExpression() {
        // Based on user's expr output for "expr 5 +": no output, exit code 2.
        // The test expects IllegalArgumentException.
        // If expr 5 + returns exit code 2 and empty output, it will be caught by ExprCommandExecutor.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("5 +"); // Incomplete expression
        }, "Invalid expression should cause an IllegalArgumentException from ExprCommandExecutor");

        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
        // No specific syntax error message check as it might be empty on some expr versions for this input.
    }

    @Test
    @DisplayName("Expr: Large numbers - Limits and Overflow")
    void testExprLargeNumbers() throws IOException, InterruptedException {
        // GNU expr: при переполнении может вернуть либо "0", либо переполненное значение (например, "9223372036854775808")
        String largeNum = "9223372036854775807"; // Long.MAX_VALUE
        String result = exprCommandExecutor.executeExprCommand(largeNum + " + 1");
        assertTrue(result.equals("0") || result.equals("9223372036854775808"),
            "expr should return 0 or the overflowed value for this specific expr version, got: " + result);

        // Проверяем число чуть больше 64-битного диапазона
        String veryLargeNum = "9223372036854775808"; // Long.MAX_VALUE + 1 (as string)
        String resultVeryLarge = exprCommandExecutor.executeExprCommand(veryLargeNum + " + 1");
        assertTrue(resultVeryLarge.equals("0") || resultVeryLarge.equals("9223372036854775809"),
            "expr should return 0 or the overflowed value for numbers exceeding its internal limits, got: " + resultVeryLarge);
    }

    @Test
    @DisplayName("Expr: Floating-point numbers - Behavior")
    void testExprFloatingPointNumbers() {
        // Based on user's expr output: expr 5.5 + 3.2 -> ?expr: non-integer argument (exit code 2)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("5.5 + 3.2");
        }, "expr should not handle floating-point numbers directly and throw an error");

        assertTrue(thrown.getMessage().contains("non-integer argument"),
                "Error message should indicate non-integer argument for floating points");
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
    }
}
