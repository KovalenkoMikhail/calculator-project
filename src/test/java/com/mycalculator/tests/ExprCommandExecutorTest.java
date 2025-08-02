// src/test/java/com/mycalculator.tests/ExprCommandExecutorTest.java
// Ensure the package name matches your project structure (com.mycalculator.tests)

package com.mycalculator.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments; // New import for Arguments
import org.junit.jupiter.params.provider.MethodSource; // Changed from CsvSource to MethodSource

import java.io.IOException;
import java.util.stream.Stream; // New import for Stream

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for ExprCommandExecutor.java using JUnit 5.
 * Tests the behavior of the 'expr' command through the executor,
 * covering basic arithmetic, error reporting, and limits.
 * This version includes advanced test cases for non-standard inputs and boolean expressions,
 * utilizing MethodSource for parameterized test data.
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

    // --- MethodSource for Parameterized Tests ---

    static Stream<Arguments> additionTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 8),      // positive + positive
            Arguments.of(-5, 3, -2),    // negative + positive
            Arguments.of(-5, -3, -8),   // negative + negative
            Arguments.of(10, 0, 10),    // positive + zero
            Arguments.of(0, 0, 0)       // zero + zero
        );
    }

    static Stream<Arguments> subtractionTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 2),      // positive - positive
            Arguments.of(-5, 10, -15),  // negative - positive
            Arguments.of(3, 5, -2),     // smaller - larger
            Arguments.of(5, 0, 5),      // positive - zero
            Arguments.of(0, 0, 0)       // zero - zero
        );
    }

    static Stream<Arguments> multiplicationTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 15),     // positive * positive
            Arguments.of(-5, 3, -15),   // negative * positive
            Arguments.of(-5, -3, 15),   // negative * negative
            Arguments.of(5, 0, 0),      // positive * zero
            Arguments.of(0, 0, 0)       // zero * zero
        );
    }

    static Stream<Arguments> divisionTestCases() {
        return Stream.of(
            Arguments.of(6, 3, 2),      // positive / positive
            Arguments.of(7, 2, 3),      // positive / positive (integer division)
            Arguments.of(-6, 3, -2),    // negative / positive
            Arguments.of(-6, -3, 2)     // negative / negative
        );
    }

    static Stream<Arguments> booleanAndStringComparisonTestCases() {
        return Stream.of(
            Arguments.of("1 = 1", "1"),     // True boolean expression
            Arguments.of("1 = 2", "0"),     // False boolean expression
            Arguments.of("a = a", "1"),     // String comparison (true)
            Arguments.of("a = b", "0")      // String comparison (false)
        );
    }

    // --- Parameterized Tests using MethodSource ---

    @ParameterizedTest
    @MethodSource("additionTestCases")
    @DisplayName("Expr: Addition with various numbers (Parameterized)")
    void testExprAdd(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " + " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("subtractionTestCases")
    @DisplayName("Expr: Subtraction with various numbers (Parameterized)")
    void testExprSubtract(int a, int b, int expectedResult) throws IOException, InterruptedException { // FIXED: Removed duplicate 'int'
        String expression = a + " - " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("multiplicationTestCases")
    @DisplayName("Expr: Multiplication with various numbers (Parameterized)")
    void testExprMultiply(int a, int b, int expectedResult) throws IOException, InterruptedException {
        // Important: '*' needs to be escaped in expr, so use \\*
        String expression = a + " \\* " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("divisionTestCases")
    @DisplayName("Expr: Division with various numbers (Parameterized)")
    void testExprDivide(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " / " + b;
        assertEquals(String.valueOf(expectedResult), exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("booleanAndStringComparisonTestCases")
    @DisplayName("Expr: Boolean expressions and string comparisons (Parameterized)")
    void testExprBooleanAndStringComparisons(String expression, String expectedResult) throws IOException, InterruptedException {
        // For boolean expressions, expr returns "1" for true and "0" for false.
        // It also handles string comparisons.
        assertEquals(expectedResult, exprCommandExecutor.executeExprCommand(expression),
                "Boolean/string comparison should return expected 1 or 0");
    }

    // --- Standard Test Cases (Non-Parameterized) ---

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
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
    }

    @Test
    @DisplayName("Expr: Invalid expression - Error Reporting")
    void testExprInvalidExpression() {
        // Based on expr output for "expr 5 +": no output, exit code 2.
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
        // Based on expr output: expr 5.5 + 3.2 -> ?expr: non-integer argument (exit code 2)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("5.5 + 3.2");
        }, "expr should not handle floating-point numbers directly and throw an error");

        assertTrue(thrown.getMessage().contains("non-integer argument"),
                "Error message should indicate non-integer argument for floating points");
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
    }

    @Test
    @DisplayName("Expr: Empty input - Error Reporting (Specific message for this expr version)")
    void testExprEmptyInput() {
        // 'expr' on this system returns "missing operand" for empty input
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("");
        }, "Empty expr input should cause an IllegalArgumentException");

        assertTrue(thrown.getMessage().contains("expr: missing operand"),
                "Error message should contain 'missing operand' for empty input");
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 for empty input");
    }

    @Test
    @DisplayName("Expr: Non-numeric arguments - Error Reporting (Specific message for this expr version)")
    void testExprNonNumericArguments() {
        // 'expr' on this system returns "non-integer argument" for non-numeric arguments in arithmetic operations
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            exprCommandExecutor.executeExprCommand("a + b");
        }, "Non-numeric arguments should cause an IllegalArgumentException");

        assertTrue(thrown.getMessage().contains("expr: non-integer argument"),
                "Error message should indicate 'non-integer argument' for non-numeric arguments");
        assertTrue(thrown.getMessage().contains("exit code 2"),
                "Error message should indicate exit code 2 from expr");
    }

    @Test
    @DisplayName("Expr: Very long valid expression - Limits")
    void testExprVeryLongValidExpression() throws IOException, InterruptedException {
        // Test if expr handles very long, but syntactically correct, expressions.
        // Some shell limits or expr limits might exist, but usually they are very high.
        // Construct a long string like "1 + 1 + 1 + ... + 1"
        StringBuilder longExpressionBuilder = new StringBuilder("1");
        long expectedSum = 1;
        for (int i = 0; i < 1000; i++) { // Create an expression with 1000 additions
            longExpressionBuilder.append(" + 1");
            expectedSum++;
        }
        String longExpression = longExpressionBuilder.toString();

        String result = exprCommandExecutor.executeExprCommand(longExpression);
        assertEquals(String.valueOf(expectedSum), result, "Expr should correctly calculate a very long expression");
    }
}
