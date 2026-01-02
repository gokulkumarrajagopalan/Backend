# Test Master Sync Endpoints
# Tests all masters: Units, StockCategory, StockItem, StockGroup

$baseUrl = "http://localhost:8080"
$cmpId = 9  # Test company ID

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing Master Sync Endpoints" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Test 1: Get Master AlterID Mapping
Write-Host "`n[TEST 1] Get Master AlterID Mapping" -ForegroundColor Yellow
$mappingUrl = "$baseUrl/api/companies/$cmpId/master-mapping"
Write-Host "GET $mappingUrl" -ForegroundColor Gray

try {
    $response = Invoke-WebRequest -Uri $mappingUrl -Method Get -ContentType "application/json"
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $data | ConvertTo-Json | Write-Host
    
    if ($data.success) {
        Write-Host "`n✓ Master Mapping Retrieved Successfully" -ForegroundColor Green
        Write-Host "Current AlterID values:" -ForegroundColor Cyan
        $data.masters | ConvertTo-Json | Write-Host
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Sync Units
Write-Host "`n[TEST 2] Sync Units" -ForegroundColor Yellow
$unitsUrl = "$baseUrl/units/sync"
Write-Host "POST $unitsUrl" -ForegroundColor Gray

$unitsPayload = @(
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000ce"
        masterId = 206
        alterId = 208
        unitName = "`$"
        originalName = "Dollar"
        simpleUnit = $true
        reservedName = ""
    },
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000cf"
        masterId = 207
        alterId = 209
        unitName = "@!"
        originalName = "Rupee"
        simpleUnit = $true
        reservedName = ""
    }
) | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $unitsUrl -Method Post -ContentType "application/json" -Body $unitsPayload
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    if ($data.success) {
        Write-Host "✓ Units synced successfully" -ForegroundColor Green
        Write-Host "Synced: $($data.count) units" -ForegroundColor Green
    } else {
        Write-Host "✗ Sync failed: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Sync StockCategory
Write-Host "`n[TEST 3] Sync StockCategory" -ForegroundColor Yellow
$stockCategoryUrl = "$baseUrl/stock-categories/sync"
Write-Host "POST $stockCategoryUrl" -ForegroundColor Gray

$stockCategoryPayload = @(
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d0"
        masterId = 208
        alterId = 210
        name = "Goods"
        parent = "&#4; Primary"
        reservedName = ""
    }
) | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $stockCategoryUrl -Method Post -ContentType "application/json" -Body $stockCategoryPayload
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    if ($data.success) {
        Write-Host "✓ Stock Categories synced successfully" -ForegroundColor Green
        Write-Host "Synced: $($data.count) categories" -ForegroundColor Green
    } else {
        Write-Host "✗ Sync failed: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Sync StockGroup
Write-Host "`n[TEST 4] Sync StockGroup" -ForegroundColor Yellow
$stockGroupUrl = "$baseUrl/stock-groups/sync"
Write-Host "POST $stockGroupUrl" -ForegroundColor Gray

$stockGroupPayload = @(
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d5"
        masterId = 213
        alterId = 215
        name = "Sad"
        parent = "&#4; Primary"
        reservedName = ""
    }
) | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $stockGroupUrl -Method Post -ContentType "application/json" -Body $stockGroupPayload
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    if ($data.success) {
        Write-Host "✓ Stock Groups synced successfully" -ForegroundColor Green
        Write-Host "Synced: $($data.count) groups" -ForegroundColor Green
    } else {
        Write-Host "✗ Sync failed: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Sync StockItem
Write-Host "`n[TEST 5] Sync StockItem" -ForegroundColor Yellow
$stockItemUrl = "$baseUrl/stock-items/sync"
Write-Host "POST $stockItemUrl" -ForegroundColor Gray

$stockItemPayload = @(
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d4"
        masterId = 212
        alterId = 214
        name = "Us"
        parent = "&#4; Primary"
        category = "&#4; Not Applicable"
        description = "Test item"
        gstTypeOfSupply = "Goods"
        costingMethod = "Avg. Cost"
        valuationMethod = "Avg. Price"
        baseUnits = "`$"
        additionalUnits = "&#4; Not Applicable"
        batchWiseOn = $false
        costCentersOn = $false
    },
    @{
        userId = 1
        cmpId = $cmpId
        guid = "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d7"
        masterId = 215
        alterId = 219
        name = "Wd"
        parent = "&#4; Primary"
        category = "&#4; Not Applicable"
        description = "Another test item"
        gstTypeOfSupply = "Goods"
        costingMethod = "Default"
        valuationMethod = "Default"
        baseUnits = "&#4; Not Applicable"
        additionalUnits = "&#4; Not Applicable"
        batchWiseOn = $false
        costCentersOn = $false
    }
) | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $stockItemUrl -Method Post -ContentType "application/json" -Body $stockItemPayload
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    if ($data.success) {
        Write-Host "✓ Stock Items synced successfully" -ForegroundColor Green
        Write-Host "Synced: $($data.count) items" -ForegroundColor Green
    } else {
        Write-Host "✗ Sync failed: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Get Updated Master Mapping
Write-Host "`n[TEST 6] Get Updated Master Mapping After Sync" -ForegroundColor Yellow
Write-Host "GET $mappingUrl" -ForegroundColor Gray

try {
    $response = Invoke-WebRequest -Uri $mappingUrl -Method Get -ContentType "application/json"
    $data = $response.Content | ConvertFrom-Json
    
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    
    if ($data.success) {
        Write-Host "✓ Updated Master Mapping:" -ForegroundColor Green
        Write-Host "  - Group: $($data.masters.group)" -ForegroundColor Cyan
        Write-Host "  - Ledger: $($data.masters.ledger)" -ForegroundColor Cyan
        Write-Host "  - StockItem: $($data.masters.stockitem)" -ForegroundColor Cyan
        Write-Host "  - StockCategory: $($data.masters.stockcategory)" -ForegroundColor Cyan
        Write-Host "  - StockGroup: $($data.masters.stockgroup)" -ForegroundColor Cyan
        Write-Host "  - Units: $($data.masters.units)" -ForegroundColor Cyan
        Write-Host "  - CostCategory: $($data.masters.costcategory)" -ForegroundColor Cyan
        Write-Host "  - CostCenter: $($data.masters.costcenter)" -ForegroundColor Cyan
        Write-Host "  - Currency: $($data.masters.currency)" -ForegroundColor Cyan
        Write-Host "  - Godown: $($data.masters.godown)" -ForegroundColor Cyan
        Write-Host "  - TaxUnit: $($data.masters.taxunit)" -ForegroundColor Cyan
        Write-Host "  - VoucherType: $($data.masters.vouchertype)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`n✓ All master sync endpoints tested" -ForegroundColor Green
Write-Host "`nExpected AlterID increments:" -ForegroundColor Yellow
Write-Host "  - Units: 209 (was 0)" -ForegroundColor Cyan
Write-Host "  - StockCategory: 210 (was 0)" -ForegroundColor Cyan
Write-Host "  - StockGroup: 215 (was 0)" -ForegroundColor Cyan
Write-Host "  - StockItem: 219 (was 0)" -ForegroundColor Cyan
Write-Host "`nRun with: PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1" -ForegroundColor Green
