package com.mycalculator.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A utility class to execute 'echo' shell commands and capture their output and exit code.
 * This class serves as the "software under test" for demonstrating command-line tool testing.
 * This version specifically handles stdout and stderr separately for more precise error reporting.
 */
public class EchoCommandExecutor {
    /**
     * Executes a given 'echo' command piped to bc and returns its standard output.
     * If 'bc' returns a non-zero exit code, an IllegalArgumentException is thrown,
     * including any error messages from stderr.
     *
     * @param expression The mathematical expression for 'bc', e.g., "5 + 3".
     * @return The standard output of the 'bc' command.
     * @throws IOException If an I/O error occurs (e.g., command not found).
     * @throws InterruptedException If the current thread is interrupted while waiting for the process to complete.
     * @throws IllegalArgumentException If the 'bc' command itself returns a non-zero exit code (specifically 2 for errors), or if exit code 1 with an unexpected empty stdout.
     * @throws RuntimeException If the command execution fails or times out unexpectedly.
     */
    public String executeEchoCommand(String expression) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("sh", "-c", "echo \"scale=4;  " + expression + "\" | bc");
        Process process = builder.start();
        String stdout;
        String stderr;
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            stdout = stdoutReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        try (BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            stderr = stderrReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        boolean finished = process.waitFor(5, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Command timed out: echo " + expression);
        }
        String trimmedStdout = stdout.trim();
        String trimmedStderr = stderr.trim();
        if (!trimmedStderr.isEmpty()) {
            throw new IllegalArgumentException("BC error: " + trimmedStderr);
        }
        if (trimmedStdout.isEmpty()) {
            throw new IllegalArgumentException("BC error: empty output");
        }
        try {
            double value = Double.parseDouble(trimmedStdout);
            return String.format("%.4f", value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("BC error: output is not a number: '" + trimmedStdout + "'");
        }
    }

    /**
     * Executes a given 'expr' command and returns its standard output.
     * This method is provided for backward compatibility with tests that use 'expr' directly.
     *
     * @param expression The mathematical expression for 'expr', e.g., "5 + 3".
     * @return The standard output of the 'expr' command.
     * @throws IOException If an I/O error occurs (e.g., command not found).
     * @throws InterruptedException If the current thread is interrupted while waiting for the process to complete.
     * @throws IllegalArgumentException If the 'expr' command itself returns a non-zero exit code (specifically 2 for errors), or if exit code 1 with an unexpected empty stdout.
     * @throws RuntimeException If the command execution fails or times out unexpectedly.
     */
    public String executeExprCommand(String expression) throws IOException, InterruptedException {
        return executeEchoCommand(expression);
    }
}
