// src/main/java/com/mycalculator.tests/ExprCommandExecutor.java
// Ensure the package name matches your project structure (com.mycalculator.tests)

package com.mycalculator.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A utility class to execute 'expr' shell commands and capture their output and exit code.
 * This class serves as the "software under test" for demonstrating command-line tool testing.
 */
public class ExprCommandExecutor {

    /**
     * Executes a given 'expr' command and returns its output.
     *
     * @param expression The mathematical expression for 'expr', e.g., "5 + 3".
     * @return The standard output of the 'expr' command.
     * @throws IOException If an I/O error occurs (e.g., command not found).
     * @throws InterruptedException If the current thread is interrupted while waiting for the process to complete.
     * @throws IllegalArgumentException If the 'expr' command itself returns a non-zero exit code (specifically 2 for errors), or if exit code 1 with an unexpected empty output.
     * @throws RuntimeException If the command execution fails or times out unexpectedly.
     */
    public String executeExprCommand(String expression) throws IOException, InterruptedException {
        // Use "sh", "-c" to ensure the command is executed correctly in a shell,
        // especially important for expressions with spaces or special characters.
        // This makes the command "expr <expression>"
        ProcessBuilder builder = new ProcessBuilder("sh", "-c", "expr " + expression);
        builder.redirectErrorStream(true); // Redirect error stream to output stream to capture all messages

        Process process = builder.start();

        // Read the output from the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        // Wait for the process to complete with a timeout
        boolean finished = process.waitFor(5, TimeUnit.SECONDS); // Wait up to 5 seconds
        if (!finished) {
            process.destroyForcibly(); // Terminate if it times out
            throw new RuntimeException("Command timed out: expr " + expression);
        }

        // Get the exit code to determine if 'expr' reported an error
        int exitCode = process.exitValue();
        String exprOutput = output.toString().trim();

        // expr exit codes on GNU systems (common behavior):
        // 0: expression is neither null nor 0 (success)
        // 1: expression is null or 0 (e.g., "0" result, or empty string for boolean false)
        // 2: syntax error or other invalid operation (true error)

        // Throw IllegalArgumentException only for true errors (exit code 2)
        // or if exit code is 1 AND the output is empty (which for arithmetic usually means an error)
        // If output is "0" and exitCode is 1, it's a valid result, not an error.
        if (exitCode == 2) {
            throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": " + exprOutput);
        } else if (exitCode == 1 && exprOutput.isEmpty()) {
            // This handles cases where expr returns exit code 1 but no output, which is an error.
            throw new IllegalArgumentException("Expr command failed with exit code " + exitCode + ": " + exprOutput);
        }
        // For exitCode 0, or exitCode 1 with non-empty output (like "0"), we return the output.
        return exprOutput;
    }
}
