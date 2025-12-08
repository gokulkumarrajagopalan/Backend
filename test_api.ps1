# Comprehensive API Test Script for Tally Backend
# Tests User Login and Company Creation

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "TALLY BACKEND API TEST SUITE" -ForegroundColor Yellow
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Wait for app to be ready
Start-Sleep -Seconds 15

# TEST 1: LOGIN
Write-Host ""
Write-Host "TEST 1: USER LOGIN" -ForegroundColor Yellow
Write-Host "---" -ForegroundColor Yellow
Write-Host "Endpoint: POST http://localhost:8080/auth/login"
Write-Host "Method: POST"
Write-Host ""

try {
    $loginPayload = @{
        username = "Admin"
        password = "123456"
    } | ConvertTo-Json
    
    Write-Host "Request Body:"
    Write-Host $loginPayload -ForegroundColor Gray
    Write-Host ""
    
    $loginResponse = Invoke-RestMethod `
        -Uri "http://localhost:8080/auth/login" `
        -Method Post `
        -Body $loginPayload `
        -ContentType "application/json" `
        -ErrorAction Stop
    
    Write-Host "✓ LOGIN SUCCESS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Green
    Write-Host "  Success: $($loginResponse.success)"
    Write-Host "  Message: $($loginResponse.message)"
    Write-Host "  User ID: $($loginResponse.data.userId)"
    Write-Host "  Username: $($loginResponse.data.username)"
    Write-Host "  Device Token: $($loginResponse.data.deviceToken)"
    Write-Host "  JWT Token: $($loginResponse.data.token.Substring(0, 40))..."
    Write-Host "  WebSocket URL: $($loginResponse.data.wsUrl)"
    Write-Host ""
    
    $token = $loginResponse.data.token
    $userId = $loginResponse.data.userId
    
    # TEST 2: CREATE COMPANY
    Write-Host ""
    Write-Host "TEST 2: CREATE COMPANY" -ForegroundColor Yellow
    Write-Host "---" -ForegroundColor Yellow
    Write-Host "Endpoint: POST http://localhost:8080/companies"
    Write-Host "Method: POST"
    Write-Host ""
    
    $companyPayload = @{
        name = "Acme Corporation"
        userId = $userId
        financialYearStart = "01-04-2024"
        booksStart = "01-04-2024"
        gstState = "MH"
        gstin = "27AAPCT1234H1Z5"
        currencyFormalName = "INR"
        address = "Mumbai, Maharashtra"
    } | ConvertTo-Json
    
    Write-Host "Request Body:"
    Write-Host $companyPayload -ForegroundColor Gray
    Write-Host ""
    
    $companyResponse = Invoke-RestMethod `
        -Uri "http://localhost:8080/companies" `
        -Method Post `
        -Body $companyPayload `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -ErrorAction Stop
    
    Write-Host "✓ COMPANY CREATED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Green
    Write-Host "  Success: $($companyResponse.success)"
    Write-Host "  Message: $($companyResponse.message)"
    Write-Host "  Company ID: $($companyResponse.data.id)"
    Write-Host "  Company Name: $($companyResponse.data.name)"
    Write-Host "  Financial Year Start: $($companyResponse.data.financialYearStart)"
    Write-Host "  GSTIN: $($companyResponse.data.gstin)"
    Write-Host ""
    
    # TEST 3: GET ALL COMPANIES
    Write-Host ""
    Write-Host "TEST 3: GET ALL COMPANIES" -ForegroundColor Yellow
    Write-Host "---" -ForegroundColor Yellow
    Write-Host "Endpoint: GET http://localhost:8080/companies"
    Write-Host "Method: GET"
    Write-Host ""
    
    $companiesResponse = Invoke-RestMethod `
        -Uri "http://localhost:8080/companies" `
        -Method Get `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -ErrorAction Stop
    
    Write-Host "✓ GET COMPANIES SUCCESS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Green
    Write-Host "  Success: $($companiesResponse.success)"
    Write-Host "  Total Companies: $($companiesResponse.count)"
    Write-Host ""
    
    if ($companiesResponse.count -gt 0) {
        Write-Host "Companies:" -ForegroundColor Green
        foreach ($company in $companiesResponse.data) {
            Write-Host "  - ID: $($company.id), Name: $($company.name), User: $($company.userId)"
        }
    }
    Write-Host ""
    
    # TEST 4: GET CURRENT USER
    Write-Host ""
    Write-Host "TEST 4: GET CURRENT USER" -ForegroundColor Yellow
    Write-Host "---" -ForegroundColor Yellow
    Write-Host "Endpoint: GET http://localhost:8080/users/me"
    Write-Host "Method: GET"
    Write-Host ""
    
    $userResponse = Invoke-RestMethod `
        -Uri "http://localhost:8080/users/me" `
        -Method Get `
        -ContentType "application/json" `
        -Headers @{ "Authorization" = "Bearer $token" } `
        -ErrorAction Stop
    
    Write-Host "✓ GET USER SUCCESS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Green
    Write-Host "  Success: $($userResponse.success)"
    Write-Host "  User ID: $($userResponse.userId)"
    Write-Host "  Username: $($userResponse.username)"
    Write-Host "  Email: $($userResponse.email)"
    Write-Host "  Full Name: $($userResponse.fullName)"
    Write-Host "  Role: $($userResponse.role)"
    Write-Host ""
    
    # SUMMARY
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host "✓ ALL TESTS PASSED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Summary:" -ForegroundColor Green
    Write-Host "  ✓ User Login: SUCCESS"
    Write-Host "  ✓ Create Company: SUCCESS"
    Write-Host "  ✓ Get Companies: SUCCESS"
    Write-Host "  ✓ Get Current User: SUCCESS"
    Write-Host ""
    Write-Host "Key Endpoints Working:" -ForegroundColor Green
    Write-Host "  - POST http://localhost:8080/auth/login"
    Write-Host "  - POST http://localhost:8080/companies"
    Write-Host "  - GET http://localhost:8080/companies"
    Write-Host "  - GET http://localhost:8080/users/me"
    Write-Host "  - WebSocket: ws://localhost:8080/session"
    Write-Host ""
    
} catch {
    Write-Host "✗ ERROR!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error Details:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    if ($_.Exception.Response) {
        Write-Host "Response Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
    Write-Host ""
}
