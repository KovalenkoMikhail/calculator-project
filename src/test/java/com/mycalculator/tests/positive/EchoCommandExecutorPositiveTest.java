package com.mycalculator.tests.positive;

import com.mycalculator.tests.EchoCommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EchoCommandExecutorPositiveTest {
    private EchoCommandExecutor echoCommandExecutor;

    @BeforeEach
    void setUp() {
        echoCommandExecutor = new EchoCommandExecutor();
    }

    static Stream<Arguments> additionTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 8),
            Arguments.of(-5, 3, -2),
            Arguments.of(-5, -3, -8),
            Arguments.of(10, 0, 10),
            Arguments.of(0, 0, 0)
        );
    }
    static Stream<Arguments> subtractionTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 2),
            Arguments.of(-5, 10, -15),
            Arguments.of(3, 5, -2),
            Arguments.of(5, 0, 5),
            Arguments.of(0, 0, 0)
        );
    }
    static Stream<Arguments> multiplicationTestCases() {
        return Stream.of(
            Arguments.of(5, 3, 15),
            Arguments.of(-5, 3, -15),
            Arguments.of(-5, -3, 15),
            Arguments.of(5, 0, 0),
            Arguments.of(0, 0, 0)
        );
    }
    static Stream<Arguments> divisionTestCases() {
        return Stream.of(
            Arguments.of(6, 3, 2),
            Arguments.of(7, 2, 3),
            Arguments.of(-6, 3, -2),
            Arguments.of(-6, -3, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("additionTestCases")
    @DisplayName("BC: Addition with various numbers (Parameterized)")
    void testBcAdd(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " + " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", echoCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("subtractionTestCases")
    @DisplayName("BC: Subtraction with various numbers (Parameterized)")
    void testBcSubtract(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " - " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", echoCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("multiplicationTestCases")
    @DisplayName("BC: Multiplication with various numbers (Parameterized)")
    void testBcMultiply(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " * " + b;
        assertEquals(String.valueOf(expectedResult) + ".0000", echoCommandExecutor.executeExprCommand(expression));
    }

    @ParameterizedTest
    @MethodSource("divisionTestCases")
    @DisplayName("BC: Division with various numbers (Parameterized)")
    void testBcDivide(int a, int b, int expectedResult) throws IOException, InterruptedException {
        String expression = a + " / " + b;
        assertEquals(String.format("%.4f", (double)a / b), echoCommandExecutor.executeExprCommand(expression));
    }

    @Test
    @DisplayName("BC: Large numbers - Limits and Overflow")
    void testBcLargeNumbers() throws IOException, InterruptedException {
        // Why: To check bc's behavior with numbers near 64-bit integer limits
        String largeNum = "9223372036854775807";
        String result = echoCommandExecutor.executeExprCommand(largeNum + " + 1");
        assertTrue(result.startsWith("922337203685477"),
            "Result should start with '922337203685477', but was: " + result);
    }

    @Test
    @DisplayName("BC: Floating-point numbers - Behavior")
    void testBcFloatingPointNumbers() throws IOException, InterruptedException {
        // Why: To verify correct handling of floating-point addition
        String result = echoCommandExecutor.executeExprCommand("5.5 + 3.2");
        assertEquals("8.7000", result);
    }

    @Test
    @DisplayName("BC: Very long valid expression - Limits")
    void testBcVeryLongValidExpression() throws IOException, InterruptedException {
        // Why: To test bc's ability to handle very long expressions
        StringBuilder longExpressionBuilder = new StringBuilder("1");
        double expectedSum = 1;
        for (int i = 0; i < 1000; i++) {
            longExpressionBuilder.append(" + 1");
            expectedSum++;
        }
        String longExpression = longExpressionBuilder.toString();
        String result = echoCommandExecutor.executeExprCommand(longExpression);
        assertEquals(String.format("%.4f", expectedSum), result);
    }
}
