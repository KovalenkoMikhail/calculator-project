package com.mycalculator.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Calculator.java using JUnit 5.
 * Covers basic arithmetic operations, division by zero,
 * and demonstrates Java's integer overflow/underflow behavior.
 */
public class CalculatorTest {

    private Calculator calculator;

    /**
     * Initializes a new Calculator instance before each test.
     */
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Addition: positive numbers")
    void testAddPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3), "2 + 3 should be 5");
    }

    @Test
    @DisplayName("Addition: negative numbers")
    void testAddNegativeNumbers() {
        assertEquals(-5, calculator.add(-2, -3), "-2 + -3 should be -5");
    }

    @Test
    @DisplayName("Addition: mixed numbers")
    void testAddMixedNumbers() {
        assertEquals(1, calculator.add(-2, 3), "-2 + 3 should be 1");
    }

    @Test
    @DisplayName("Addition: with zero")
    void testAddWithZero() {
        assertEquals(10, calculator.add(10, 0), "10 + 0 should be 10");
    }

    @Test
    @DisplayName("Subtraction: positive numbers")
    void testSubtractPositiveNumbers() {
        assertEquals(1, calculator.subtract(3, 2), "3 - 2 should be 1");
    }

    @Test
    @DisplayName("Subtraction: negative numbers")
    void testSubtractNegativeNumbers() {
        assertEquals(1, calculator.subtract(-2, -3), "-2 - -3 should be 1");
    }

    @Test
    @DisplayName("Subtraction: with zero")
    void testSubtractWithZero() {
        assertEquals(5, calculator.subtract(5, 0), "5 - 0 should be 5");
    }

    @Test
    @DisplayName("Multiplication: positive numbers")
    void testMultiplyPositiveNumbers() {
        assertEquals(6, calculator.multiply(2, 3), "2 * 3 should be 6");
    }

    @Test
    @DisplayName("Multiplication: by zero")
    void testMultiplyByZero() {
        assertEquals(0, calculator.multiply(5, 0), "5 * 0 should be 0");
    }

    @Test
    @DisplayName("Multiplication: negative numbers")
    void testMultiplyNegativeNumbers() {
        assertEquals(-6, calculator.multiply(-2, 3), "-2 * 3 should be -6");
        assertEquals(6, calculator.multiply(-2, -3), "-2 * -3 should be 6");
    }

    @Test
    @DisplayName("Division: positive numbers")
    void testDividePositiveNumbers() {
        assertEquals(2, calculator.divide(4, 2), "4 / 2 should be 2");
    }

    @Test
    @DisplayName("Division: with remainder (integer division)")
    void testDivideWithRemainder() {
        assertEquals(3, calculator.divide(7, 2), "7 / 2 (integer division) should be 3");
    }

    @Test
    @DisplayName("Division: negative numbers")
    void testDivideNegativeNumbers() {
        assertEquals(-2, calculator.divide(-4, 2), "-4 / 2 should be -2");
        assertEquals(2, calculator.divide(-4, -2), "-4 / -2 should be 2");
    }

    @Test
    @DisplayName("Division by zero: expect IllegalArgumentException")
    void testDivideByZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(10, 0),
                "Division by zero should throw IllegalArgumentException");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                calculator.divide(10, 0));
        assertEquals("Division by zero is not allowed", exception.getMessage());
    }

    @Test
    @DisplayName("Integer overflow/underflow demonstration")
    void testIntegerOverflow() {
        int maxInt = Integer.MAX_VALUE;
        assertEquals(Integer.MIN_VALUE, calculator.add(maxInt, 1),
                "Integer.MAX_VALUE + 1 should be Integer.MIN_VALUE due to overflow");
        assertEquals(Integer.MAX_VALUE, calculator.subtract(Integer.MIN_VALUE, 1),
                "Integer.MIN_VALUE - 1 should be Integer.MAX_VALUE due to underflow");
        assertEquals(-2, calculator.multiply(maxInt, 2),
                "Integer.MAX_VALUE * 2 should overflow to -2");
    }

    @Test
    @DisplayName("Floating-point division by zero (Infinity/NaN)")
    void testFloatDivisionByZeroBehavior() {
        double positiveInfinity = 10.0 / 0.0;
        double negativeInfinity = -10.0 / 0.0;
        double nan = 0.0 / 0.0;

        assertTrue(Double.isInfinite(positiveInfinity), "10.0 / 0.0 should be Positive Infinity");
        assertTrue(positiveInfinity > 0, "Positive Infinity should be positive");

        assertTrue(Double.isInfinite(negativeInfinity), "-10.0 / 0.0 should be Negative Infinity");
        assertTrue(negativeInfinity < 0, "Negative Infinity should be negative");

        assertTrue(Double.isNaN(nan), "0.0 / 0.0 should be NaN (Not a Number)");
        assertFalse(Double.isInfinite(nan), "NaN should not be Infinity");
    }
}
