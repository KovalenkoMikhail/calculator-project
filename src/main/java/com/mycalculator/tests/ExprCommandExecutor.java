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
        ProcessBuilder builder = new ProcessBuilder("sh", "-c", "echo \"scale=4;  " + expression + "\" | bc");
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

        // Если bc вывел что-то в stderr — это ошибка (parse error, деление на ноль и т.д.)
        if (!trimmedStderr.isEmpty()) {
            throw new IllegalArgumentException("BC error: " + trimmedStderr);
        }

        // Если результат пустой — это тоже ошибка (например, пустой ввод)
        if (trimmedStdout.isEmpty()) {
            throw new IllegalArgumentException("BC error: empty output");
        }

        // Пробуем привести к double и вернуть с 4 знаками после запятой
        try {
            double value = Double.parseDouble(trimmedStdout);
            return String.format("%.4f", value);
        } catch (NumberFormatException e) {
            // Если не число — это ошибка (например, bc не смог посчитать)
            throw new IllegalArgumentException("BC error: output is not a number: '" + trimmedStdout + "'");
        }
    }
}
