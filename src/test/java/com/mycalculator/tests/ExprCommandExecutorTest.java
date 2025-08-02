// src/test/java/com/mycalculator.tests/ExprCommandExecutorTest.java
// Ensure the package name matches your project structure (com.mycalculator.tests)

package com.mycalculator.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest; // New import for parameterized tests
import org.junit.jupiter.params.provider.CsvSource; // New import for CSV data source

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

    @ParameterizedTest
    @CsvSource({
        "5, 3, 8",      // positive + positive
        "-5, 3, -2",    // negative + positive
        "-5, -3, -8",   // negative + negative
        "10, 0, 10",    // positive + zero
        "0, 0, 0"       // zero + zero
    })
    @DisplayName("Expr: Addition with various numbers (Parameterized)")
    void testExprAdd(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " + " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @CsvSource({
        "5, 3, 2",      // positive - positive
        "-5, 10, -15",  // negative - positive
        "3, 5, -2",     // smaller - larger
        "5, 0, 5",      // positive - zero
        "0, 0, 0"       // zero - zero
    })
    @DisplayName("Expr: Subtraction with various numbers (Parameterized)")
    void testExprSubtract(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " - " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @CsvSource({
        "5, 3, 15",     // positive * positive
        "-5, 3, -15",   // negative * positive
        "-5, -3, 15",   // negative * negative
        "5, 0, 0",      // positive * zero
        "0, 0, 0"       // zero * zero
    })
    @DisplayName("Expr: Multiplication with various numbers (Parameterized)")
    void testExprMultiply(int a, int b, int expectedResult) throws IOException, InterruptedException {
        // Important: '*' needs to be escaped in expr, so use \\*
        String expression = a + " \\* " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @CsvSource({
        "6, 3, 2",      // positive / positive
        "7, 2, 3",      // positive / positive (integer division)
        "-6, 3, -2",    // negative / positive
        "-6, -3, 2"     // negative / negative
    })
    @DisplayName("Expr: Division with various numbers (Parameterized)")
    void testExprDivide(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " / " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
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
        // Based on user's MOST RECENT expr output: expr 9223372036854775807 + 1 -> 9223372036854775808 (no error)
        String largeNum = "9223372036854775807"; // Long.MAX_VALUE
        String expectedResultForLargeNum = "9223372036854775808"; // This is (Long.MAX_VALUE + 1) as a string
        String result = exprCommandExecutor.executeExprCommand(largeNum + " + 1");
        assertEquals(expectedResultForLargeNum, result, "expr should return the direct result for this large number");

        // Test with a number just beyond typical 64-bit signed integer range
        // Based on user's MOST RECENT expr output, it also returns the direct result.
        String veryLargeNum = "9223372036854775808"; // Long.MAX_VALUE + 1 (as string)
        String expectedResultForVeryLargeNum = "9223372036854775809"; // This is (Long.MAX_VALUE + 2) as a string
        String resultVeryLarge = exprCommandExecutor.executeExprCommand(veryLargeNum + " + 1");
        assertEquals(expectedResultForVeryLargeNum, resultVeryLarge, "expr should return the direct result for numbers exceeding its internal limits for this specific expr version");

        // If your expr *does* throw an error for truly massive numbers (e.g., beyond 64-bit),
        // you would add a separate assertThrows test for that.
        // For the provided output, it seems to just return the direct result.
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
