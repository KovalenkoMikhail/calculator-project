# Java Calculator with Unit Tests

## Project Overview

This project implements basic arithmetic operations (addition, subtraction, multiplication, division) in Java and provides a set of unit tests using JUnit 5. The tests cover various scenarios, including standard operations, handling of negative numbers and zero, as well as edge cases like division by zero and integer overflow.

The project uses Apache Maven for build automation and is designed to be run from the Linux command line (e.g., within WSL).

## Prerequisites

To build and run this project, you need:

* **Java Development Kit (JDK) 17** or newer.
* **Apache Maven** (latest stable version recommended).
* **Linux operating system** (e.g., Ubuntu, Debian) or **Windows Subsystem for Linux (WSL)**.

## How to Run Tests

1.  **Clone this repository or unpack the project archive.**
    Navigate to the project's root directory (where `pom.xml` is located).

2.  **Open your Linux terminal** (e.g., Ubuntu in WSL).

3.  **Navigate to the project's root directory** (`calculator-tests`):
    ```bash
    cd ~/your_path/calculator-tests
    ```

4.  **Execute all unit tests using Maven:**
    ```bash
    mvn test
    ```
    Maven will compile the source code, download dependencies, and run the tests. A `BUILD SUCCESS` message indicates all tests passed.

## Behavior at Limits and Error Handling

This project explores specific behaviors of arithmetic operations in Java:

* **Division by Zero:**
    * The `divide` method explicitly checks for division by zero and throws an `IllegalArgumentException`.
    * Tests (`testDivideByZero`) confirm this expected exception.

* **Integer Overflow/Underflow:**
    * Java's `int` and `long` primitive types do **not** throw exceptions on overflow/underflow. Instead, the result wraps around.
    * Tests (`testIntOverflowAddition`, `testIntUnderflowSubtraction`, `testIntOverflowMultiplication`) verify this wrapping behavior for `int` type. For handling arbitrarily large numbers, Java provides `java.math.BigInteger` and `java.math.BigDecimal`.