# Java Coding Guidelines

This document defines the coding standards for Java source code, based on the Google Java Style Guide.

## Version Requirements

- **Java Version**: 11
- **Spring Boot Version**: 2.7.18

All Java code must be compatible with Java 11 and use Spring Boot 2.7.18.

## 1 Source File Basics

### 1.1 File Name
The source file name consists of the case-sensitive name of the top-level class it contains (of which there is exactly one), plus the `.java` extension.

### 1.2 File Encoding
Source files are encoded in UTF-8.

### 1.3 Special Characters
- Use ASCII horizontal space (0x20) for indentation
- Use escape sequences (`\t`, `\b`, `\n`, `\r`, `\"`, `\'`, `\\`) for special characters in string and character literals

## 2 Source File Structure

A source file consists of, in order:
1. License or copyright information (if present)
2. Package statement
3. Import statements
4. Exactly one top-level class

**Exactly one blank line** separates each section that is present.

### 2.1 Package Statement
The package statement is not line-wrapped.

### 2.2 Import Statements

#### 2.2.1 No Wildcard Imports
Wildcard imports, static or otherwise, are not used.

```java
// Good
import java.util.List;
import java.util.ArrayList;

// Bad
import java.util.*;
```

#### 2.2.2 No Line-Wrapping
Import statements are not line-wrapped.

#### 2.2.3 Ordering
1. All static imports in a single block
2. All non-static imports in a single block

### 2.3 Class Declaration

#### 2.3.1 Exactly One Top-Level Class Declaration
Each top-level class resides in a source file of its own.

#### 2.3.2 Member Ordering
The order you choose for the members and initializers of your class can have a great effect on learnability. Suggested order:
1. Static fields
2. Instance fields
3. Constructors
4. Methods

#### 2.3.3 Overloads: Never Split
When a class has multiple constructors, or multiple methods with the same name, these appear sequentially, with no other code in between.

## 3 Formatting

### 3.1 Braces

#### 3.1.1 Use of Optional Braces
Braces are used with `if`, `else`, `for`, `do` and `while` statements, even when the body is empty or contains only a single statement.

#### 3.1.2 Nonempty Blocks: K & R Style
Braces follow the Kernighan and Ritchie style (Egyptian brackets):
- No line break before the opening brace
- Line break after the closing brace

```java
if (condition()) {
    doSomething();
} else if (otherCondition()) {
    doSomethingElse();
} else {
    doDefault();
}
```

### 3.2 Block Indentation
Indentation is +2 spaces for each level.

### 3.3 One Statement Per Line
Each statement is followed by a line break.

### 3.4 Column Limit
Java code has a column limit of 100 characters.

### 3.5 Whitespace

#### 3.5.1 Vertical Whitespace
A single blank line appears:
- Between consecutive members or initializers of a class: fields, constructors, methods, nested classes, static initializers
- As needed to group related sections

#### 3.5.2 Horizontal Whitespace
A single ASCII space appears:
- After `if`, `for`, `catch`, etc. keywords
- Before opening brace
- After commas and colons in type bounds

```java
// Good
if (x == 0) {
    doSomething();
}
List<String> list = new ArrayList<>();

// Bad
if(x==0){doSomething();}
```

### 3.6 Specific Constructs

#### 3.6.1 Enum Classes
Each enum constant may be followed by a comma and a body. Use braces for enum with a body.

```java
public enum OrderStatus {
    PENDING("Pending"),
    COMPLETED("Completed");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
```

#### 3.6.2 Switch Statements
Use modern switch expressions (Java 14+) when possible.

```java
// Preferred for Java 14+
int result = switch (day) {
    case MONDAY, FRIDAY -> 6;
    case TUESDAY -> 7;
    default -> 9;
};
```

## 4 Naming Conventions

### 4.1 Common Rules
- Identifiers use only ASCII letters and digits
- No prefix or suffix characters (e.g., `mFoo`, `s_bar`)

### 4.2 Rules by Identifier Type

#### 4.2.1 Package Names
Package names are all lowercase, with consecutive words simply concatenated.

```java
package com.example.myapp;
```

#### 4.2.2 Class Names
Class names are written in UpperCamelCase.

```java
public class UserService { }
public class OrderController { }
```

#### 4.2.3 Method Names
Method names are written in lowerCamelCase.

```java
public void processOrder() { }
public List<User> getActiveUsers() { }
```

#### 4.2.4 Variable Names
Variable names are written in lowerCamelCase.

```java
String userName;
int orderCount;
List<Order> orders;
```

#### 4.2.5 Constant Names
Constants are written in UPPER_SNAKE_CASE.

```java
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_LOCALE = "en-US";
```

A constant is a `static final` field whose contents are deeply immutable.

#### 4.2.6 Enum Values
Enum values are written in UPPER_SNAKE_CASE.

```java
public enum Color {
    RED,
    GREEN,
    BLUE
}
```

## 5 Programming Practices

### 5.1 @Override: Always Use
A method that overrides a superclass method must be annotated with `@Override`.

### 5.2 Caught Exceptions: Never Ignore
Never swallow exceptions without proper handling.

```java
// Bad
try {
    doSomething();
} catch (Exception e) {
    // ignore
}

// Good
try {
    doSomething();
} catch (SpecificException e) {
    log.error("Failed to do something", e);
    throw new BusinessException("Failed", e);
}
```

### 5.3 Static Members: Use Class Name
When referring to a static member, use the class name, not a reference.

```java
// Good
int count = ArrayList.size();

// Bad
List<String> list = new ArrayList<>();
int size = list.size(); // if size is static
```

### 5.4 Variable Declaration
Declare one variable per statement; avoid declaring multiple variables in one statement.

```java
// Good
int a;
int b;

// Bad
int a, b;
```

### 5.5 Array Initializers
Array initializers can be block-like or simple.

```java
// Block-like
int[] values = {
    1,
    2,
    3
};

// Simple
int[] values = {1, 2, 3};
```

### 5.6 Switch Expressions
Prefer switch expressions over traditional switch statements when applicable.

### 5.7 Lambda Expressions
When lambda parameters need type inference, keep it consistent.

```java
// Good
list.stream()
    .map(String::toLowerCase)
    .collect(Collectors.toList());
```

## 6 Javadoc

### 6.1 When to Use
Javadoc is required for every public class and for every public or protected member of such a class.

### 6.2 Format
```java
/**
 * Retrieves a customer by their ID.
 *
 * @param customerId The unique identifier of the customer
 * @return The customer with the specified ID, or null if not found
 * @throws IllegalArgumentException if customerId is null
 */
public Customer getCustomerById(String customerId) { }
```

### 6.3 What to Include
- Summary fragment (first sentence)
- Description of parameters
- Description of return value
- Description of exceptions

## 7 Best Practices

### 7.1 Immutability
Prefer immutable classes. Use `final` for classes, fields, and variables when appropriate.

### 7.2 Avoid Null
Avoid returning null where possible. Use:
- Empty collections instead of null
- `Optional` for methods that may not return a value
- `@Nullable` annotation for parameters that can be null

### 7.3 Single Responsibility
Each class should have a single responsibility.

### 7.4 Dependency Injection
Use constructor injection for dependencies.

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### 7.5 Logging
Use a logging framework (SLF4J) instead of `System.out.println()`.

```java
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

logger.debug("Debug message");
logger.info("Info message");
logger.warn("Warning message");
logger.error("Error message", exception);
```
