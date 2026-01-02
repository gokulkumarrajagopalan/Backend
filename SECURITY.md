# Security Implementation Guide

This document outlines the security measures implemented in the Tally Backend application.

## 1. Environment Variables & Credentials Management

### ✅ Implemented
- All sensitive credentials moved to environment variables
- Database credentials, JWT secret, and AWS credentials are no longer hardcoded
- `.env.example` provided as template

### Usage
```bash
# Create your .env file (never commit to git)
cp .env.example .env

# Edit .env with your actual values
# Then set environment variables before running the application
```

## 2. CORS (Cross-Origin Resource Sharing)

### ✅ Implemented
- Restricts allowed origins to specific domains only (configurable)
- Only allows specific HTTP methods: GET, POST, PUT, DELETE
- Only allows required headers: Authorization, Content-Type, X-Device-Token
- Credentials are enabled for same-origin requests

### Configuration
Update `CORS_ALLOWED_ORIGINS` in your `.env`:
```
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://app.yourdomain.com
```

## 3. CSRF (Cross-Site Request Forgery) Protection

### ✅ Implemented
- CSRF token repository enabled
- Configured to ignore /auth/** and /ws/** endpoints (stateless JWT)
- CSRF tokens are HttpOnly and Secure

## 4. Security Headers

### ✅ Implemented Headers
1. **X-Frame-Options: DENY** - Prevents clickjacking attacks
2. **X-Content-Type-Options: nosniff** - Prevents MIME type sniffing
3. **X-XSS-Protection: 1; mode=block** - Enables XSS protection
4. **Strict-Transport-Security** - Enforces HTTPS with preload
5. **Content-Security-Policy** - Restricts resource loading
6. **Referrer-Policy: strict-no-referrer** - Prevents referrer leakage
7. **Permissions-Policy** - Disables dangerous browser features

## 5. JWT (JSON Web Tokens)

### ✅ Implemented
- Short-lived access tokens (15 minutes default)
- Refresh tokens support (7 days default)
- Proper token validation and expiration checking
- Device token validation for single device login

### Configuration
In `.env`:
```
JWT_SECRET=your-long-random-secret-key-here
```

## 6. Input Validation

### ✅ Implemented
- Request DTOs use `@Valid` annotation with `@NotBlank`, `@Size`, `@Email`, `@Pattern` constraints
- Username must be 3-50 characters, alphanumeric with underscore/hyphen only
- Password must be 8+ characters
- Email validation using standard email patterns
- Global exception handler for validation errors

### Example Validation
```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    // Validation enforced automatically
}
```

## 7. Sensitive Data Protection

### ✅ Implemented
- Error messages don't expose stack traces or internal details
- Logging doesn't expose usernames, device tokens, or passwords
- SQL queries use parameterized statements (@Param) to prevent SQL injection
- Database credentials are never logged

## 8. Rate Limiting

### ✅ Implemented
- Authentication endpoints: 10 requests per minute
- Login endpoint: 5 attempts per 15 minutes (brute force protection)
- General API: 100 requests per minute

### Configuration
Edit `RateLimiterConfiguration.java` to adjust limits:
```java
.limitForPeriod(10)  // Max 10 requests
.limitRefreshPeriod(Duration.ofMinutes(1))  // Per 1 minute
```

## 9. HTTPS/SSL Configuration

### ✅ Implemented
- SSL configuration options in application.properties
- Session cookies are Secure and HttpOnly
- HSTS (HTTP Strict-Transport-Security) header enforces HTTPS

### Production Setup
```properties
SSL_ENABLED=true
SSL_KEYSTORE_PATH=/path/to/your/keystore.p12
SSL_KEYSTORE_PASSWORD=your_password
```

## 10. WebSocket Security

### ✅ Implemented
- JWT token validation for WebSocket connections
- Device token verification
- Single device login enforcement

## 11. Password Security

### ✅ Implemented
- Passwords encrypted with BCrypt
- No plaintext password storage
- Cost factor: 10 (industry standard)

## 12. SQL Injection Prevention

### ✅ Implemented
- All queries use Spring Data JPA derived methods or `@Query` with `@Param`
- No string concatenation in SQL queries
- Parameterized queries by default

## Best Practices to Follow

### 🔒 Development
1. Never commit `.env` file to git
2. Add `.env` to `.gitignore`
3. Use `.env.example` as template
4. Review logs regularly for security events

### 🔐 Production Deployment
1. Generate strong JWT secret (minimum 64 characters)
2. Enable SSL/HTTPS
3. Set `SPRING_PROFILES_ACTIVE=production`
4. Use strong database passwords
5. Enable AWS credential rotation
6. Configure proper CORS origins
7. Monitor rate limiting logs
8. Use secrets management (AWS Secrets Manager, HashiCorp Vault, etc.)

### 🛡️ Regular Maintenance
1. Keep dependencies updated
2. Review security advisories quarterly
3. Rotate AWS credentials periodically
4. Monitor application logs for suspicious activity
5. Conduct penetration testing

## Environment-Specific Configuration

### Development
```
SPRING_PROFILES_ACTIVE=dev
SSL_ENABLED=false
```

### Staging
```
SPRING_PROFILES_ACTIVE=staging
SSL_ENABLED=true
SECURE_COOKIES=true
```

### Production
```
SPRING_PROFILES_ACTIVE=production
SSL_ENABLED=true
SECURE_COOKIES=true
APP_BASE_URL=https://yourdomain.com
```

## Testing Security

### Test Default Credentials
```bash
# These endpoints require no authentication
curl http://localhost:8080/auth/register
curl http://localhost:8080/auth/login

# These endpoints require JWT token in header
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "X-Device-Token: YOUR_DEVICE_TOKEN" \
     http://localhost:8080/api/protected-endpoint
```

## References
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
