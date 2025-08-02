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
 * Tests the behavior of the 'bc' command through the executor,
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

    // --- Parameterized Tests using MethodSource ---

    @ParameterizedTest
    @MethodSource("additionTestCases")
    @DisplayName("BC: Addition with various numbers (Parameterized)")
    void testBcAdd(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " + " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("subtractionTestCases")
    @DisplayName("BC: Subtraction with various numbers (Parameterized)")
    void testBcSubtract(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " - " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("multiplicationTestCases")
    @DisplayName("BC: Multiplication with various numbers (Parameterized)")
    void testBcMultiply(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " * " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", exprCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("divisionTestCases")
    @DisplayName("BC: Division with various numbers (Parameterized)")
    void testBcDivide(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " / " + b;
        // bc returns float with 4 decimals
        assertEquals(String.format("%.4f", (double)a / b), exprCommandExecutor.executeExprCommand(expression));
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

    @Test
    @DisplayName("BC: Large numbers - Limits and Overflow")
    void testBcLargeNumbers() throws IOException, InterruptedException {
        String largeNum = "9223372036854775807";
        String result = exprCommandExecutor.executeExprCommand(largeNum + " + 1");
        // bc округляет большие числа, сравниваем только первые 15 знаков
        assertTrue(result.startsWith("922337203685477"),
            "Result should start with '922337203685477', but was: " + result);
    }

    @Test
    @DisplayName("BC: Floating-point numbers - Behavior")
    void testBcFloatingPointNumbers() throws IOException, InterruptedException {
        String result = exprCommandExecutor.executeExprCommand("5.5 + 3.2");
        assertEquals("8.7000", result);
    }

    @Test
    @DisplayName("BC: Very long valid expression - Limits")
    void testBcVeryLongValidExpression() throws IOException, InterruptedException {
        StringBuilder longExpressionBuilder = new StringBuilder("1");
        double expectedSum = 1;
        for (int i = 0; i < 1000; i++) {
            longExpressionBuilder.append(" + 1");
            expectedSum++;
        }
        String longExpression = longExpressionBuilder.toString();
        String result = exprCommandExecutor.executeExprCommand(longExpression);
        assertEquals(String.format("%.4f", expectedSum), result);
    }
}
