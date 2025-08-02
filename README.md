expr Command Testing Project

This project demonstrates writing unit tests in Java using Maven and JUnit 5 for the external command-line utility expr, available in Linux (WSL).
Project Goal

The primary goal is to showcase the ability to:

    Interact with external software (in this case, expr) from Java.

    Write automated tests to verify the functionality of such tools.

    Investigate and demonstrate the limits of the tested software.

    Check its error reporting.

Technologies Used

    Java 17: The programming language.

    Maven: The build automation and dependency management tool.

    JUnit 5 (Jupiter): The unit testing framework, including Parameterized Tests for efficient scenario coverage.

    WSL (Windows Subsystem for Linux): The development environment where the expr command is executed.

Project Structure

calculator-tests/
├── src/
│   ├── main/java/
│   │   └── com/mycalculator/tests/
│   │       └── ExprCommandExecutor.java  # Class for executing 'expr' commands
│   └── test/java/
│       └── com/mycalculator/tests/
│           └── ExprCommandExecutorTest.java # Unit tests for ExprCommandExecutor
├── pom.xml                                 # Maven configuration
└── README.md                               # This file

How to Run Tests

    Ensure WSL (Windows Subsystem for Linux) is installed and the expr command is available within it.

    Clone the repository (if you haven't already):

    git clone https://github.com/KovalenkoMikhail/calculator-tests.git
    cd calculator-tests

    Open a WSL terminal in the project's root directory (calculator-tests).

    Execute the Maven command to build the project and run tests:

    mvn clean install

        clean: Cleans up previously compiled files.

        install: Compiles the main code, compiles and runs the tests, and then installs the artifact to the local Maven repository.

Demonstrating expr Limits and Error Reporting

ExprCommandExecutorTest.java contains tests that demonstrate key aspects of expr's behavior:

    Basic Arithmetic Operations: Covered using parameterized tests, ensuring efficient and readable testing of various number combinations (positive, negative, zero).

    Division by Zero: The testExprDivideByZero test verifies that expr correctly reports a "division by zero" error and returns the corresponding exit code (2).

    Invalid Syntax: The testExprInvalidExpression test demonstrates how expr reacts to incomplete or syntactically incorrect expressions, returning an error and exit code (2).

    Large Numbers (Overflow/Limits): The testExprLargeNumbers test investigates expr's behavior when dealing with numbers exceeding standard integer ranges. In your environment, expr for very large numbers returns "0" with exit code 1 or the direct value, showcasing its specific limits.

    Floating-Point Numbers: The testExprFloatingPointNumbers test shows that expr performs integer arithmetic by default and reports an error ("non-integer argument") when attempting to use floating-point numbers.

Advanced expr Output Handling

The ExprCommandExecutor.java class has been enhanced to read expr's standard output (stdout) and standard error stream (stderr) separately. This provides more precise control over distinguishing between operation results and error messages, increasing the reliability and informational value of external command processing.
Test Output

After successfully running mvn clean install, you will see a test report indicating that all tests passed without failures or errors:

[INFO] Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS

