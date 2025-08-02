package com.mycalculator.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EchoCommandExecutor {
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
        // Wait for process to finish, but kill if it hangs (e.g. bc waits for input)
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

    // For legacy compatibility only; all logic is in executeEchoCommand
    public String executeExprCommand(String expression) throws IOException, InterruptedException {
        return executeEchoCommand(expression);
    }
}
