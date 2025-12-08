# Project Structure and SOLID Architecture

## Overview
The Tally Backend has been refactored to follow SOLID principles, resulting in a cleaner, more maintainable, and testable codebase.

## Directory Structure

```
src/main/java/com/tally/
├── TallyApplication.java              # Spring Boot entry point
│
├── config/                             # Configuration classes
│   ├── CorsConfig.java                # CORS configuration
│   ├── JwtAuthenticationFilter.java    # JWT authentication filter
│   ├── SecurityConfig.java            # Spring Security configuration
│   └── WebSocketConfig.java           # WebSocket configuration
│
├── controller/                         # REST API Controllers (Dependency Inversion)
│   ├── AuthController.java            # ✅ REFACTORED: Uses IUserService, IAuthenticationService
│   ├── CompanyController.java         # Company management
│   ├── UserController.java            # User management
│   └── *.java.disabled                # Disabled controllers (future implementation)
│
├── service/                            # Business Logic Layer
│   ├── interfaces/                    # ✅ NEW: Service Interfaces (Interface Segregation)
│   │   ├── IUserService.java          # User operations contract
│   │   ├── IAuthenticationService.java# Authentication contract
│   │   ├── IResponseBuilder.java      # Response building contract
│   │   ├── ICompanyService.java       # Company operations contract
│   │   └── IExceptionHandler.java     # Exception handling contract
│   │
│   ├── impl/                          # ✅ NEW: Service Implementations
│   │   ├── AuthenticationService.java # JWT token operations (SRP)
│   │   ├── ResponseBuilder.java       # Response formatting (SRP)
│   │   └── ExceptionHandler.java      # Exception processing (SRP)
│   │
│   ├── UserService.java               # ✅ REFACTORED: Implements IUserService, Constructor injection
│   ├── CompanyService.java            # Company management
│   └── *.java                         # Other services
│
├── repository/                         # Data Access Layer (Spring Data JPA)
│   ├── UserRepository.java
│   ├── CompanyRepository.java
│   └── *.java                         # Other repositories
│
├── entity/                             # Entity Models (JPA)
│   ├── User.java
│   ├── Company.java
│   └── *.java                         # Other entities
│
├── util/                               # Utility Classes
│   ├── JwtUtil.java                   # JWT token utilities
│   ├── ApiResponse.java               # Response wrapper
│   └── *.java                         # Other utilities
│
└── websocket/                          # WebSocket Handlers
    └── SessionWebSocketHandler.java    # WebSocket session management
```

## Core Design Patterns Used

### 1. Dependency Injection
- **Constructor Injection**: Used for immutability and explicit dependencies
- **Spring @Autowired**: Used on constructor parameters
- **No Field Injection**: Avoids tight coupling and testing difficulties

```java
@RestController
public class AuthController {
    private final IUserService userService;
    private final IAuthenticationService authService;
    
    @Autowired
    public AuthController(UserService userService, IAuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
    }
}
```

### 2. Strategy Pattern
- **IResponseBuilder**: Different response formatting strategies
- **IAuthenticationService**: Token strategy abstraction

### 3. Template Method Pattern
- **Service Base Classes**: Common operations in parent classes (future)

### 4. Factory Pattern
- **Spring Configuration**: Beans created via Spring factory

## Service Layer Architecture

### Authentication Service
```java
IAuthenticationService authService = new AuthenticationService(jwtUtil);
- generateToken(username) -> String
- extractUsername(token) -> String
- validateToken(token) -> boolean
- isTokenExpired(token) -> boolean
```

### User Service
```java
IUserService userService = new UserService(userRepository, passwordEncoder);
- createUser(...) -> User
- authenticateUser(...) -> Optional<User>
- findByUsername(username) -> Optional<User>
- updateDeviceToken(...) -> void
- getDeviceToken(...) -> String
```

### Response Builder Service
```java
IResponseBuilder responseBuilder = new ResponseBuilder();
- success(message, data) -> Map
- error(message, statusCode) -> Map
- paginated(...) -> Map
```

## Dependency Graph

```
AuthController
    ├── depends on IUserService (implemented by UserService)
    ├── depends on IAuthenticationService (implemented by AuthenticationService)
    ├── depends on IResponseBuilder (implemented by ResponseBuilder)
    └── depends on SessionWebSocketHandler

UserService implements IUserService
    ├── depends on UserRepository (Spring Data)
    └── depends on PasswordEncoder

AuthenticationService implements IAuthenticationService
    └── depends on JwtUtil

ResponseBuilder implements IResponseBuilder
    └── no dependencies
```

## Key Improvements Over Previous Version

### Before Refactoring
```java
@RestController
public class AuthController {
    @Autowired
    private UserService userService;      // Tight coupling
    
    @Autowired
    private JwtUtil jwtUtil;              // Direct utility dependency
    
    @PostMapping("/login")
    public ResponseEntity<Map> login(@RequestBody LoginRequest req) {
        // ... mixed concerns ...
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);     // Response building logic mixed
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
```

### After Refactoring
```java
@RestController
public class AuthController {
    private final IUserService userService;           // Abstraction
    private final IAuthenticationService authService; // Separated concerns
    private final IResponseBuilder responseBuilder;   // Consistent responses
    
    @Autowired
    public AuthController(IUserService userService, ...) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map> login(@RequestBody LoginRequest req) {
        // ... focused logic ...
        Map<String, Object> response = responseBuilder.success(...);
        return ResponseEntity.ok(response);
    }
}
```

## Benefits Achieved

| Principle | Benefit |
|-----------|---------|
| **SRP** | Each class has one reason to change; easier to maintain |
| **OCP** | Open for extension, closed for modification |
| **LSP** | Implementations can be swapped without breaking code |
| **ISP** | Client-specific interfaces; no fat interfaces |
| **DIP** | Depend on abstractions, not concrete classes |

## Testing Improvements

### Before: Hard to Test
```java
// Would need to mock concrete UserService and JwtUtil
new AuthController()  // Can't inject mocks
```

### After: Easy to Test
```java
@Mock IUserService userService;
@Mock IAuthenticationService authService;
@Mock IResponseBuilder responseBuilder;

AuthController controller = new AuthController(userService, authService, responseBuilder);
// Can test each scenario easily
```

## Migration Roadmap

### Phase 1: ✅ COMPLETE
- [x] Create service interfaces
- [x] Implement interface-based services
- [x] Refactor AuthController
- [x] Update UserService with DI

### Phase 2: IN PROGRESS
- [ ] Refactor CompanyController
- [ ] Refactor remaining controllers
- [ ] Create base controller class
- [ ] Implement global exception handler

### Phase 3: TO DO
- [ ] Repository interfaces
- [ ] Add caching layer
- [ ] Event-driven architecture
- [ ] Comprehensive unit tests

## Configuration Properties

### JWT Configuration
```properties
jwt.secret=your-secret-key
jwt.expiration=86400000
```

### Server Configuration
```properties
server.port=8080
server.servlet.context-path=
```

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/TallyDB
spring.datasource.username=postgres
spring.datasource.password=your-password
```

## How to Use the Architecture

### Adding a New Feature
1. Create an interface in `service/interfaces/INewFeatureService.java`
2. Implement it in `service/impl/NewFeatureService.java`
3. Inject via constructor in controller
4. Use the abstraction, not the implementation

### Example:
```java
// 1. Interface
public interface IReportService {
    Map<String, Object> generateReport(Long companyId);
}

// 2. Implementation
@Service
public class ReportService implements IReportService {
    @Override
    public Map<String, Object> generateReport(Long companyId) { ... }
}

// 3. Controller
@RestController
public class ReportController {
    private final IReportService reportService;
    
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping("/{companyId}")
    public ResponseEntity<?> getReport(@PathVariable Long companyId) {
        Map<String, Object> data = reportService.generateReport(companyId);
        return ResponseEntity.ok(responseBuilder.success("Report generated", data));
    }
}
```

## Performance Considerations

- Constructor injection has no runtime overhead
- Spring manages singleton beans efficiently
- Interfaces have minimal memory footprint
- No performance degradation compared to direct dependency injection

## Security Considerations

- JWT tokens validated on every request
- Single-device login enforcement
- Device tokens tracked per user
- Unauthorized sessions invalidated on new login

