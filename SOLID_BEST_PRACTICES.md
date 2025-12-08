# SOLID Principles - Best Practices Guide

## Quick Reference

| Principle | Goal | Implementation |
|-----------|------|-----------------|
| **S**ingle Responsibility | One reason to change | Each class has one job |
| **O**pen/Closed | Extensible without modification | Use interfaces for extension |
| **L**iskov Substitution | Predictable inheritance | Implementations are substitutable |
| **I**nterface Segregation | Focused contracts | Multiple small interfaces |
| **D**ependency Inversion | Depend on abstractions | Inject via interfaces |

## Code Examples

### ❌ ANTI-PATTERN: Violating Multiple Principles

```java
@RestController
public class BadAuthController {
    @Autowired
    private UserRepository userRepository;        // Violates DIP
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    private CacheService cacheService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            // SRP violation: Authentication + Response building + Logging
            User user = userRepository.findByUsername(req.getUsername()).orElse(null);
            if (user == null) {
                loggingService.log("User not found");
                Map response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(401).body(response);
            }
            
            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                loggingService.log("Wrong password");
                cacheService.increment("failed_login:" + user.getId());
                // Response building again...
                return ResponseEntity.status(401).body(...);
            }
            
            String token = jwtUtil.generateToken(user.getUsername());
            emailService.sendLoginNotification(user.getEmail());  // Extra concern!
            loggingService.log("User logged in");
            cacheService.set("user:" + user.getId(), user);
            
            // Manual response building with duplicated logic
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Generic error handling
            loggingService.log("Login error: " + e.getMessage());
            Map response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // More methods with same issues...
}

// Problems:
// 1. SRP: Violates - handles authentication, response building, logging, caching
// 2. OCP: Violates - needs modification to change each concern
// 3. ISP: Violates - depends on many large interfaces
// 4. DIP: Violates - depends on concrete classes (Repository, Services)
// 5. Testability: Very difficult - needs to mock 6+ dependencies
// 6. Maintainability: High - changes to one concern affect everything
```

### ✅ CORRECT: Following SOLID Principles

```java
// 1. Separate concerns into focused interfaces
public interface IAuthenticationService {
    String generateToken(String username);
    boolean validateToken(String token);
}

public interface IUserService {
    Optional<User> authenticate(String username, String password);
}

public interface IResponseBuilder {
    Map<String, Object> success(String message, Object data);
    Map<String, Object> error(String message, int code);
}

// 2. Implement each interface with single responsibility
@Service
public class AuthenticationService implements IAuthenticationService {
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token) && !jwtUtil.isTokenExpired(token);
    }
}

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}

@Service
public class ResponseBuilder implements IResponseBuilder {
    @Override
    public Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @Override
    public Map<String, Object> error(String message, int code) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("statusCode", code);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}

// 3. Inject interfaces into controller
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;
    private final IAuthenticationService authService;
    private final IResponseBuilder responseBuilder;
    
    @Autowired
    public AuthController(
            UserService userService,
            IAuthenticationService authService,
            IResponseBuilder responseBuilder) {
        this.userService = userService;
        this.authService = authService;
        this.responseBuilder = responseBuilder;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        Optional<User> user = userService.authenticate(req.getUsername(), req.getPassword());
        
        if (user.isPresent()) {
            String token = authService.generateToken(user.get().getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.get().getId());
            data.put("username", user.get().getUsername());
            
            Map<String, Object> response = responseBuilder.success("Login successful", data);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = responseBuilder.error("Invalid credentials", 401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

// Benefits:
// 1. SRP: ✅ Each class has ONE responsibility
// 2. OCP: ✅ Open for extension (can add INotificationService, ICachingService, etc.)
// 3. LSP: ✅ Implementations are substitutable
// 4. ISP: ✅ Small, focused interfaces
// 5. DIP: ✅ Depends on abstractions (interfaces)
// 6. Testability: ✅ Very easy - inject mocks
// 7. Maintainability: ✅ Changes isolated to specific classes
```

### Unit Test Example

```java
@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {
    
    @Mock
    private IUserService userService;
    
    @Mock
    private IAuthenticationService authService;
    
    @Mock
    private IResponseBuilder responseBuilder;
    
    private AuthController controller;
    
    @Before
    public void setUp() {
        controller = new AuthController(userService, authService, responseBuilder);
    }
    
    @Test
    public void testLoginSuccess() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "password123");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("admin");
        
        when(userService.authenticate("admin", "password123"))
                .thenReturn(Optional.of(mockUser));
        when(authService.generateToken("admin"))
                .thenReturn("jwt-token-123");
        when(responseBuilder.success(anyString(), any()))
                .thenReturn(new HashMap<>());
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.login(request);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).authenticate("admin", "password123");
        verify(authService).generateToken("admin");
    }
    
    @Test
    public void testLoginFailure() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "wrongpass");
        
        when(userService.authenticate("admin", "wrongpass"))
                .thenReturn(Optional.empty());
        when(responseBuilder.error(anyString(), anyInt()))
                .thenReturn(new HashMap<>());
        
        // Act
        ResponseEntity<Map<String, Object>> response = controller.login(request);
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, never()).generateToken(anyString());
    }
}
```

## Common Mistakes to Avoid

### ❌ Mistake 1: Circular Dependencies
```java
// BAD
@Service
public class ServiceA {
    @Autowired private ServiceB serviceB;
}

@Service
public class ServiceB {
    @Autowired private ServiceA serviceA;
}

// GOOD: Use event-driven or separate concerns
```

### ❌ Mistake 2: Fat Interfaces
```java
// BAD
public interface IDataService {
    User getUser(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(Long id);
    Report generateReport();      // Unrelated!
    Email sendEmail();             // Unrelated!
    Cache cacheData();             // Unrelated!
}

// GOOD: Separate interfaces
public interface IUserRepository {
    User getUser(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(Long id);
}

public interface IReportService {
    Report generateReport();
}

public interface IEmailService {
    Email sendEmail();
}

public interface ICacheService {
    void cacheData();
}
```

### ❌ Mistake 3: Poor Exception Handling
```java
// BAD
public class Service {
    public void doSomething() {
        try {
            // ...
        } catch (Exception e) {
            System.out.println(e);  // Swallowing exception
        }
    }
}

// GOOD
public class Service {
    private final Logger logger = LoggerFactory.getLogger(Service.class);
    
    public void doSomething() throws CustomException {
        try {
            // ...
        } catch (Exception e) {
            logger.error("Error in doSomething", e);
            throw new CustomException("Failed to do something", e);
        }
    }
}
```

## Refactoring Checklist

When refactoring a class:

- [ ] Identify multiple responsibilities
- [ ] Create interface for each responsibility
- [ ] Create implementation class for each interface
- [ ] Update dependent classes to inject interfaces
- [ ] Remove direct class dependencies
- [ ] Add unit tests for new services
- [ ] Verify all tests pass
- [ ] Update documentation

## SOLID in Different Layers

### Repository Layer
```java
// Interface (depends on JPA)
public interface IUserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}

// Implementation
@Repository
public class UserRepository implements IUserRepository {
    @Autowired
    private JpaUserRepository jpaRepository;
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }
}
```

### Service Layer
```java
// Interface
public interface IUserService {
    User createUser(String username, String email, String password);
}

// Implementation
@Service
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    
    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Controller Layer
```java
// Uses service interface
@RestController
public class UserController {
    private final IUserService userService;
    
    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }
}
```

## Summary

**SOLID principles make code:**
- ✅ More maintainable
- ✅ More testable
- ✅ More extensible
- ✅ Less coupled
- ✅ Easier to understand
- ✅ Easier to refactor

**Investment in SOLID pays off quickly:**
- Faster bug fixes
- Faster feature addition
- Higher code quality
- Better team collaboration
- Lower technical debt
