// src/main/java/com/mycalculator.tests/ExprCommandExecutor.java
// Ensure the package name matches your project structure (com.mycalculator.tests)

package com.mycalculator.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors; // New import for Collectors

/**
 * A utility class to execute 'expr' shell commands and capture their output and exit code.
 * This class serves as the "software under test" for demonstrating command-line tool testing.
 * This version specifically handles stdout and stderr separately for more precise error reporting.
 */
public class ExprCommandExecutor {

    /**
     * Executes a given 'expr' command and returns its standard output.
     * If 'expr' returns a non-zero exit code, an IllegalArgumentException is thrown,
     * including any error messages from stderr.
     *
     * @param expression The mathematical expression for 'expr', e.g., "5 + 3".
     * @return The standard output of the 'expr' command.
     * @throws IOException If an I/O error occurs (e.g., command not found).
     * @throws InterruptedException If the current thread is interrupted while waiting for the process to complete.
     * @throws IllegalArgumentException If the 'expr' command itself returns a non-zero exit code (specifically 2 for errors), or if exit code 1 with an unexpected empty stdout.
     * @throws RuntimeException If the command execution fails or times out unexpectedly.
     */
    public String executeExprCommand(String expression) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("sh", "-c", "expr " + expression);
        // builder.redirectErrorStream(true); // NO LONGER REDIRECTING ERROR STREAM

        Process process = builder.start();

        // Read stdout and stderr separately
        String stdout;
        String stderr;

        // Use try-with-resources for BufferedReader to ensure they are closed
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            stdout = stdoutReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        try (BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            stderr = stderrReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        // Wait for the process to complete with a timeout
        boolean finished = process.waitFor(5, TimeUnit.SECONDS); // Wait up to 5 seconds
        if (!finished) {
            process.destroyForcibly(); // Terminate if it times out
            throw new RuntimeException("Command timed out: expr " + expression);
        }

        int exitCode = process.exitValue();
        String trimmedStdout = stdout.trim();
        String trimmedStderr = stderr.trim();

        // expr exit codes on GNU systems (common behavior):
        // 0: expression is neither null nor 0 (success)
        // 1: expression is null or 0 (e.g., "0" result, or empty string for boolean false)
        // 2: syntax error or other invalid operation (true error)

        if (exitCode == 2) {
            // True error from expr (syntax error, division by zero, non-integer argument)
            // The error message is typically in stderr, but sometimes also printed to stdout before exit.
            String errorMessage = trimmedStderr.isEmpty() ? trimmedStdout : trimmedStderr;
            throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": " + errorMessage);
        } else if (exitCode == 1) {
            // exit code 1 means "expression is null or 0".
            // If stdout is "0", it's a valid result.
            // If stdout is empty, it's typically an error for arithmetic operations.
            if (trimmedStdout.equals("0")) {
                return trimmedStdout; // Valid "0" result
            } else if (trimmedStdout.isEmpty() && !trimmedStderr.isEmpty()) {
                // If stdout is empty but stderr has a message, it's an error.
                throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": " + trimmedStderr);
            } else if (trimmedStdout.isEmpty()) {
                 // If both stdout and stderr are empty, but exit code is 1, it's an unexpected scenario for arithmetic.
                throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": (no output)");
            }
            // If stdout is not "0" but not empty, and exit code is 1, it's an unexpected case.
            // We'll treat it as an error for robustness.
            throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": " + trimmedStdout);
        }
        // For exitCode 0, return stdout.
        return trimmedStdout;
    }
}
