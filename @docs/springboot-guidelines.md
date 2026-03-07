# Spring Boot Coding Guidelines

This document provides best practices for Spring Boot application development.

## Version Requirements

- **Java Version**: 11
- **Spring Boot Version**: 2.7.18

## 1 Project Structure

### 1.1 Standard Package Structure
```
com.example.app
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Data access layer
├── model/           # Entities and DTOs
├── config/          # Configuration classes
├── exception/       # Exception handling
└── util/            # Utility classes
```

### 1.2 Feature-Based Structure (For Large Projects)
```
com.example.app
├── feature-a/
│   ├── FeatureAController
│   ├── FeatureAService
│   ├── FeatureARepository
│   └── dto/
├── feature-b/
│   └── ...
```

## 2 Dependency Management

### 2.1 Use Spring Boot Starters
Always use Spring Boot starter dependencies for common functionality.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 2.2 Keep Dependencies Updated
- Regularly update to stable Spring Boot versions
- Use dependency management to avoid version conflicts
- Consider using a BOM (Bill of Materials)

## 3 Configuration

### 3.1 Externalize Configuration
Never hardcode configuration values in code.

```java
// Good
@Value("${app.service.url}")
private String serviceUrl;

// Better: Use ConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String serviceUrl;
    private int timeout;
}
```

### 3.2 Use Profiles
Manage environment-specific configurations.

```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/dev_db

# application-prod.properties
spring.datasource.url=jdbc:mysql://prod-server:3306/prod_db
```

### 3.3 Secure Configuration
- Never store secrets in application.properties
- Use environment variables or secret management tools
- Use Spring Vault for sensitive data

## 4 REST API Design

### 4.1 Resource Naming
- Use nouns, not verbs, for resource names
- Use plural form for collections
- Use HTTP methods appropriately

```java
// Good
@GetMapping("/users")
@PostMapping("/users")
@GetMapping("/users/{id}")
@PutMapping("/users/{id}")
@DeleteMapping("/users/{id}")

// Bad
@PostMapping("/getUser")
@PostMapping("/createUser")
```

### 4.2 Response Structure
Use consistent response structures.

```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }
}
```

### 4.3 HTTP Status Codes
- 200: Successful GET, PUT, PATCH, DELETE
- 201: Successful POST (resource created)
- 400: Bad request (client error)
- 401: Unauthorized
- 403: Forbidden
- 404: Resource not found
- 500: Internal server error

## 5 Controller Best Practices

### 5.1 Controllers Handle HTTP Only
Controllers should only handle request processing, not business logic.

```java
// Good
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

### 5.2 Use DTOs
Expose only necessary data using Data Transfer Objects.

```java
// Entity
@Entity
public class User {
    private Long id;
    private String password;  // sensitive
    private String email;
    private LocalDateTime createdAt;
}

// DTO
public record UserResponse(Long id, String email) {}
```

### 5.3 Input Validation
Use Bean Validation for input validation.

```java
public record CreateUserRequest(
    @NotBlank @Size(max = 50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password
) {}

@PostMapping("/users")
public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
    userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}
```

## 6 Service Layer

### 6.1 Business Logic in Services
Keep all business logic in service classes.

```java
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public UserResponse createUser(CreateUserRequest request) {
        // business logic here
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        // ...
        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(saved.getEmail());
        return toResponse(saved);
    }
}
```

### 6.2 Transactions
- Use @Transactional at service layer, not controller
- Use readOnly = true for read operations
- Keep transactions short

```java
@Service
public class OrderService {
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // write operations
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrders(Long userId) {
        // read operations
    }
}
```

### 6.3 Avoid N+1 Queries
Use eager fetching or JOIN FETCH strategies.

```java
// Bad: N+1 query
List<Order> orders = orderRepository.findByUserId(userId);
for (Order order : orders) {
    order.getItems().size();  // each triggers a query
}

// Good: fetch join
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.user.id = :userId")
List<Order> findByUserIdWithItems(@Param("userId") Long userId);
```

## 7 Repository Layer

### 7.1 Use Spring Data JPA
Leverage Spring Data JPA for data access.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    List<User> findByStatus(UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailCustom(@Param("email") String email);
}
```

### 7.2 Use Entity Graphs
For complex fetch requirements.

```java
@EntityGraph(attributePaths = {"orders", "profile"})
Optional<User> findByIdWithOrders(Long id);
```

## 8 Exception Handling

### 8.1 Centralized Exception Handling
Use @ControllerAdvice for global exception handling.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
}
```

### 8.2 Custom Exceptions
Define business-specific exceptions.

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

## 9 Logging

### 9.1 Use SLF4J
Always use SLF4J for logging.

```java
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

logger.info("Processing order: {}", orderId);
logger.error("Failed to process order", exception);
```

### 9.2 Log Levels
- DEBUG: Detailed information for debugging
- INFO: General application events
- WARN: Potential issues
- ERROR: Error conditions

### 9.3 Avoid Logging Sensitive Data
Never log passwords, tokens, or personal identifiable information (PII).

## 10 Testing

### 10.1 Unit Tests
Test individual components in isolation.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        
        CreateUserRequest request = new CreateUserRequest("test", "test@test.com", "password");
        UserResponse response = userService.createUser(request);
        
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }
}
```

### 10.2 Integration Tests
Use @SpringBootTest for integration testing.

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnUser() throws Exception {
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

### 10.3 Test Database
Use Testcontainers for database integration tests.

```java
@Testcontainers
class UserRepositoryTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    // tests
}
```

## 11 Security

### 11.1 Spring Security
Use Spring Security for authentication and authorization.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 11.2 Input Validation
- Validate all inputs at API boundaries
- Use parameterized queries to prevent SQL injection
- Sanitize inputs before using in queries

## 12 Performance

### 12.1 Caching
Use Spring's caching abstraction.

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void updateUser(Long id, User user) {
        // update logic
    }
}
```

### 12.2 Pagination
Always paginate large data sets.

```java
@GetMapping("/users")
public Page<UserResponse> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(this::toResponse);
}
```

## 13 Documentation

### 13.1 Use OpenAPI/Swagger
Document APIs with OpenAPI annotations.

```java
@Operation(summary = "Get user by ID")
@ApiResponse(responseCode = "200", description = "User found")
@ApiResponse(responseCode = "404", description = "User not found")
@GetMapping("/users/{id}")
public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    // ...
}
```
