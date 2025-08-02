[![Java CI](https://github.com/KovalenkoMikhail/calculator-project/actions/workflows/ci.yml/badge.svg)](https://github.com/KovalenkoMikhail/calculator-project/actions/workflows/ci.yml)

# Calculator CLI Testing (echo | bc)
This project demonstrates a robust and efficient command-line calculator testing suite built by leveraging the power of standard Linux utilities `echo` and `bc`.
## Requirements
- Linux (or WSL)
- Java 21:
  ```sh
  sudo apt update
  sudo apt install openjdk-21-jdk
  java -version
  # Should print: openjdk version "21..."
  ```
- Maven:
  ```sh
  sudo apt install maven
  mvn -version
  # Should print: Apache Maven ...
  ```
- bc:
  ```sh
  sudo apt install bc
  bc --version
  # Should print: bc ...
  ```

## Quick Start
```sh
git clone https://github.com/KovalenkoMikhail/calculator-project.git
cd calculator-project
mvn test
```

## Project Structure
```
src/main/java/com/mycalculator/tests/EchoCommandExecutor.java      # CLI runner
src/test/java/com/mycalculator/tests/positive/EchoCommandExecutorPositiveTest.java  # Positive tests
src/test/java/com/mycalculator/tests/negative/EchoCommandExecutorNegativeTest.java  # Negative tests
pom.xml
```

## Test Coverage
- Arithmetic: +, -, *, / (including negative, zero)
- Floating-point numbers
- Large numbers
- Long expressions
- Error handling: division by zero, syntax errors, empty input, invalid characters

## Example
```sh
echo "scale=4; 5.5 + 3.2" | bc   # 8.7000
echo "scale=4; 10 / 0" | bc      # Runtime error (Divide by zero)
```

---
- All tests run with: `mvn test`
- Uses only standard Linux tools (`echo`, `bc`)
- Clean code, ready for CI

