# SOLID Refactoring Examples - Ready to Implement

This file provides template examples for refactoring other services in the project following SOLID principles.

## Example 1: Company Service Refactoring

### Step 1: Create Interface
```java
// src/main/java/com/tally/service/interfaces/ICompanyService.java
package com.tally.service.interfaces;

import com.tally.entity.Company;
import java.util.List;
import java.util.Optional;

/**
 * Company Service Interface
 * Follows Interface Segregation Principle (ISP) and Dependency Inversion Principle (DIP)
 */
public interface ICompanyService {
    
    /**
     * Create a new company
     */
    Company createCompany(Company company);
    
    /**
     * Get company by ID
     */
    Optional<Company> getCompanyById(Long id);
    
    /**
     * Get all companies for a user
     */
    List<Company> getUserCompanies(Long userId);
    
    /**
     * Update company
     */
    Company updateCompany(Long id, Company company);
    
    /**
     * Delete company
     */
    void deleteCompany(Long id);
    
    /**
     * Get all companies
     */
    List<Company> getAllCompanies();
}
```

### Step 2: Update Implementation to Implement Interface
```java
// src/main/java/com/tally/service/CompanyService.java
package com.tally.service;

import com.tally.entity.Company;
import com.tally.repository.CompanyRepository;
import com.tally.service.interfaces.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Company Service Implementation
 * Responsible for company management operations
 * Follows Single Responsibility Principle
 */
@Service
public class CompanyService implements ICompanyService {
    
    private final CompanyRepository companyRepository;
    
    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    
    @Override
    public Company createCompany(Company company) {
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }
        return companyRepository.save(company);
    }
    
    @Override
    public Optional<Company> getCompanyById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid company ID");
        }
        return companyRepository.findById(id);
    }
    
    @Override
    public List<Company> getUserCompanies(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return companyRepository.findByUserId(userId);
    }
    
    @Override
    public Company updateCompany(Long id, Company company) {
        Optional<Company> existing = companyRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Company not found");
        }
        
        Company toUpdate = existing.get();
        if (company.getName() != null) {
            toUpdate.setName(company.getName());
        }
        // ... update other fields
        
        return companyRepository.save(toUpdate);
    }
    
    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Company not found");
        }
        companyRepository.deleteById(id);
    }
    
    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
}
```

### Step 3: Update Controller to Use Interface
```java
// src/main/java/com/tally/controller/CompanyController.java
package com.tally.controller;

import com.tally.entity.Company;
import com.tally.service.interfaces.ICompanyService;
import com.tally.service.interfaces.IResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * Company Controller
 * Handles company-related REST endpoints
 * Uses dependency injection with interfaces following Dependency Inversion Principle
 */
@RestController
@RequestMapping("/companies")
public class CompanyController {
    
    private final ICompanyService companyService;
    private final IResponseBuilder responseBuilder;
    
    @Autowired
    public CompanyController(ICompanyService companyService, IResponseBuilder responseBuilder) {
        this.companyService = companyService;
        this.responseBuilder = responseBuilder;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@RequestBody Company company) {
        try {
            Company created = companyService.createCompany(company);
            Map<String, Object> response = responseBuilder.success("Company created successfully", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompany(@PathVariable Long id) {
        try {
            Optional<Company> company = companyService.getCompanyById(id);
            if (company.isPresent()) {
                Map<String, Object> response = responseBuilder.success("Company retrieved", company.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = responseBuilder.error("Company not found", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        try {
            var companies = companyService.getAllCompanies();
            Map<String, Object> response = responseBuilder.paginated(
                    "Companies retrieved",
                    companies,
                    1,
                    companies.size(),
                    companies.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompany(@PathVariable Long id, @RequestBody Company company) {
        try {
            Company updated = companyService.updateCompany(id, company);
            Map<String, Object> response = responseBuilder.success("Company updated successfully", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            Map<String, Object> response = responseBuilder.success("Company deleted successfully", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
```

## Example 2: Email Service with SRP

```java
// src/main/java/com/tally/service/interfaces/IEmailService.java
package com.tally.service.interfaces;

/**
 * Email Service Interface
 * Single Responsibility: Send emails
 */
public interface IEmailService {
    void sendLoginNotification(String email, String username);
    void sendPasswordResetLink(String email, String resetToken);
    void sendAccountCreationConfirmation(String email, String username);
}

// src/main/java/com/tally/service/impl/EmailService.java
package com.tally.service.impl;

import com.tally.service.interfaces.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email Service Implementation
 * Responsible ONLY for sending emails
 * Follows Single Responsibility Principle
 */
@Service
public class EmailService implements IEmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void sendLoginNotification(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Login Notification");
            message.setText("Hello " + username + ",\n\nYou have successfully logged in.");
            
            mailSender.send(message);
            logger.info("Login notification sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send login notification to: {}", email, e);
            // Don't throw - email failures shouldn't break login
        }
    }
    
    @Override
    public void sendPasswordResetLink(String email, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText("Click here to reset: " + resetToken);
            
            mailSender.send(message);
            logger.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", email, e);
        }
    }
    
    @Override
    public void sendAccountCreationConfirmation(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Account Created Successfully");
            message.setText("Welcome " + username + "!\n\nYour account has been created.");
            
            mailSender.send(message);
            logger.info("Account creation email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send account creation email to: {}", email, e);
        }
    }
}
```

## Example 3: Audit Service with SRP

```java
// src/main/java/com/tally/service/interfaces/IAuditService.java
package com.tally.service.interfaces;

import com.tally.entity.AuditLog;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Audit Service Interface
 * Single Responsibility: Track user actions
 */
public interface IAuditService {
    void logAction(Long userId, String action, String details);
    List<AuditLog> getUserAuditLogs(Long userId);
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

// src/main/java/com/tally/service/impl/AuditService.java
package com.tally.service.impl;

import com.tally.entity.AuditLog;
import com.tally.repository.AuditLogRepository;
import com.tally.service.interfaces.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Audit Service Implementation
 * Responsible ONLY for audit logging
 * Follows Single Responsibility Principle
 */
@Service
public class AuditService implements IAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    @Override
    public void logAction(Long userId, String action, String details) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(userId);
            log.setAction(action);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(log);
            logger.debug("Audit log created for user: {} action: {}", userId, action);
        } catch (Exception e) {
            logger.error("Failed to create audit log for user: {}", userId, e);
            // Don't throw - audit failures shouldn't break business operations
        }
    }
    
    @Override
    public List<AuditLog> getUserAuditLogs(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }
}
```

## Example 4: Validation Service with SRP

```java
// src/main/java/com/tally/service/interfaces/IValidationService.java
package com.tally.service.interfaces;

/**
 * Validation Service Interface
 * Single Responsibility: Validate input data
 */
public interface IValidationService {
    boolean isValidEmail(String email);
    boolean isValidUsername(String username);
    boolean isValidPassword(String password);
    void validateUser(String username, String email, String password);
}

// src/main/java/com/tally/service/impl/ValidationService.java
package com.tally.service.impl;

import com.tally.service.interfaces.IValidationService;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * Validation Service Implementation
 * Responsible ONLY for input validation
 * Follows Single Responsibility Principle
 */
@Service
public class ValidationService implements IValidationService {
    
    private static final String EMAIL_PATTERN = 
            "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String USERNAME_PATTERN = 
            "^[a-zA-Z0-9_-]{3,20}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
    
    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email).matches();
    }
    
    @Override
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return usernamePattern.matcher(username).matches();
    }
    
    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches(".*[A-Z].*") &&    // Contains uppercase
               password.matches(".*[a-z].*") &&    // Contains lowercase
               password.matches(".*\\d.*");        // Contains digit
    }
    
    @Override
    public void validateUser(String username, String email, String password) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters with uppercase, lowercase, and digit");
        }
    }
}
```

## Integration Example: Updated AuthController Using Multiple Services

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final IUserService userService;
    private final IAuthenticationService authService;
    private final IResponseBuilder responseBuilder;
    private final IValidationService validationService;
    private final IAuditService auditService;
    private final IEmailService emailService;
    
    @Autowired
    public AuthController(
            UserService userService,
            IAuthenticationService authService,
            IResponseBuilder responseBuilder,
            IValidationService validationService,
            IAuditService auditService,
            IEmailService emailService) {
        this.userService = userService;
        this.authService = authService;
        this.responseBuilder = responseBuilder;
        this.validationService = validationService;
        this.auditService = auditService;
        this.emailService = emailService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            // Validate input
            validationService.validateUser(request.getUsername(), request.getEmail(), request.getPassword());
            
            // Create user
            User user = userService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getLicenceNo(),
                    request.getPassword(),
                    request.getFullName()
            );
            
            // Audit log
            auditService.logAction(user.getId(), "REGISTER", "User registered");
            
            // Send welcome email
            emailService.sendAccountCreationConfirmation(user.getEmail(), user.getUsername());
            
            // Build response
            Map<String, Object> response = responseBuilder.success("Registration successful", 
                    Map.of("userId", user.getId(), "username", user.getUsername()));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            auditService.logAction(null, "REGISTER_FAILED", e.getMessage());
            Map<String, Object> response = responseBuilder.error(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
```

## Migration Checklist

When refactoring a service:

```
[ ] Create IServiceName interface
[ ] Add interface to service/interfaces/
[ ] Update existing Service to implement interface
[ ] Use constructor injection instead of @Autowired fields
[ ] Update all controllers that use the service
[ ] Update controllers to use interface type
[ ] Add constructor with interface parameters
[ ] Remove field injection
[ ] Test the refactored code
[ ] Update documentation
[ ] Commit changes with clear message
```

## Key Takeaways

1. **One Interface, One Responsibility**
   - IUserService = User operations only
   - IEmailService = Email operations only
   - IValidationService = Validation only

2. **Constructor Injection**
   - Makes dependencies explicit
   - Enables easy testing
   - Improves code clarity

3. **Service-to-Controller Flow**
   - Controller calls service interface
   - Service implements business logic
   - Service calls repository
   - Repository handles database

4. **Consistent Response Format**
   - Use IResponseBuilder in all controllers
   - Consistent error handling
   - Consistent success responses

This structure ensures clean, maintainable, and testable code!
