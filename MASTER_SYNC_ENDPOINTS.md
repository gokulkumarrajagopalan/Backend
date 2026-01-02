# Master Sync Endpoints - Complete Documentation

## Overview
This document describes the per-entity AlterID sync endpoints that follow Tally Prime's scoped AlterID architecture.

---

## Key Principle: AlterID is Per Entity, Per Company

**AlterID is NOT global across all masters.**
Each master (Group, Ledger, StockItem, etc.) maintains its own AlterID sequence independently.

Example:
- Groups table: max(alterid) = 211
- Ledgers table: max(alterid) = 115
- StockItems table: max(alterid) = 38

These are independent and should NEVER be compared or mixed.

---

## Endpoints

### 1. Get Master AlterID Mapping
**Retrieve current max AlterID for all masters in a company**

```
GET /api/companies/{cmpId}/master-mapping
```

**Path Parameters:**
- `cmpId` (Long): Company ID

**Response Example:**
```json
{
  "success": true,
  "cmpId": 9,
  "masters": {
    "group": 211,
    "ledger": 115,
    "stockitem": 38,
    "stockcategory": 210,
    "stockgroup": 215,
    "units": 209,
    "costcategory": 5,
    "costcenter": 22,
    "currency": 3,
    "godown": 8,
    "taxunit": 0,
    "vouchertype": 7
  }
}
```

**Usage:**
```powershell
# Get mapping for company 9
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/companies/9/master-mapping" -Method Get
$data = $response.Content | ConvertFrom-Json
Write-Host $data.masters
```

---

### 2. Sync Units
**Incremental sync of Units from Tally**

```
POST /units/sync
```

**Request Body:**
```json
[
  {
    "userId": 1,
    "cmpId": 9,
    "guid": "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000ce",
    "masterId": 206,
    "alterId": 208,
    "unitName": "$",
    "originalName": "Dollar",
    "simpleUnit": true,
    "reservedName": ""
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "Units synced successfully",
  "count": 1,
  "data": [...]
}
```

**Sync Logic:**
1. Get last max alterId from database: `SELECT MAX(alterid) FROM units WHERE cmpid = 9`
2. Fetch from Tally: `AlterID > lastMaxAlterId`
3. Upsert based on `cmpId + masterId` combination
4. Update mapping

---

### 3. Sync Stock Categories
**Incremental sync of Stock Categories from Tally**

```
POST /stock-categories/sync
```

**Request Body:**
```json
[
  {
    "userId": 1,
    "cmpId": 9,
    "guid": "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d0",
    "masterId": 208,
    "alterId": 210,
    "name": "Goods",
    "parent": "&#4; Primary",
    "reservedName": ""
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "Stock Categories synced successfully",
  "count": 1,
  "data": [...]
}
```

---

### 4. Sync Stock Groups
**Incremental sync of Stock Groups from Tally**

```
POST /stock-groups/sync
```

**Request Body:**
```json
[
  {
    "userId": 1,
    "cmpId": 9,
    "guid": "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d5",
    "masterId": 213,
    "alterId": 215,
    "name": "Sad",
    "parent": "&#4; Primary",
    "reservedName": ""
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "Stock Groups synced successfully",
  "count": 1,
  "data": [...]
}
```

---

### 5. Sync Stock Items
**Incremental sync of Stock Items from Tally**

```
POST /stock-items/sync
```

**Request Body:**
```json
[
  {
    "userId": 1,
    "cmpId": 9,
    "guid": "badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d4",
    "masterId": 212,
    "alterId": 214,
    "name": "Us",
    "parent": "&#4; Primary",
    "category": "&#4; Not Applicable",
    "description": "Test item",
    "gstTypeOfSupply": "Goods",
    "costingMethod": "Avg. Cost",
    "valuationMethod": "Avg. Price",
    "baseUnits": "$",
    "additionalUnits": "&#4; Not Applicable",
    "batchWiseOn": false,
    "costCentersOn": false
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "Stock Items synced successfully",
  "count": 1,
  "data": [...]
}
```

---

## Correct Sync Flow

```
For each Company:
  ├─ Get master mapping
  │  └─ GET /api/companies/{cmpId}/master-mapping
  │
  ├─ For each Master Type:
  │  ├─ Get last max AlterID from mapping
  │  ├─ Fetch from Tally WHERE AlterID > lastMaxAlterId
  │  ├─ Send to sync endpoint
  │  │  ├─ POST /units/sync
  │  │  ├─ POST /stock-categories/sync
  │  │  ├─ POST /stock-groups/sync
  │  │  └─ POST /stock-items/sync
  │  │
  │  └─ Database UPSERTS using cmpId + masterId
  │
  └─ Get updated master mapping
     └─ GET /api/companies/{cmpId}/master-mapping
```

---

## Repository Methods

All entity repositories have the `getMaxAlterIdForCompany()` method:

```java
@Query("SELECT COALESCE(MAX(e.alterId), 0) FROM EntityName e WHERE e.cmpId = :cmpId")
Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
```

**Available in:**
- GroupRepository
- LedgerRepository
- StockItemRepository
- StockCategoryRepository
- StockGroupRepository
- UnitsRepository
- CostCategoryRepository
- CostCenterRepository
- CurrencyRepository
- GodownRepository
- TaxUnitRepository
- VoucherTypeRepository

---

## PowerShell Test Script

Run the comprehensive test:
```powershell
PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1
```

The test will:
1. Get initial master mapping
2. Sync Units (AlterID 208, 209)
3. Sync StockCategory (AlterID 210)
4. Sync StockGroup (AlterID 215)
5. Sync StockItem (AlterID 214, 219)
6. Get updated master mapping

Expected results:
- Units: 0 → 209
- StockCategory: 0 → 210
- StockGroup: 0 → 215
- StockItem: 0 → 219

---

## Common Errors & Solutions

### Error 1: Missing mandatory fields (500)
**Cause:** Some required fields are null in request body
**Solution:** Ensure all @Column(nullable=false) fields are provided

### Error 2: Duplicate GUID
**Cause:** Same GUID being inserted twice without UPSERT
**Solution:** Use `cmpId + masterId` for UPSERT logic (already implemented)

### Error 3: Data loss (missing records)
**Cause:** Using global max AlterID across all masters
**Solution:** Use per-entity max AlterID (CORRECT approach)

---

## Best Practices

✓ Always fetch master mapping before sync
✓ Process each entity independently
✓ Never mix AlterIDs across entities
✓ Use UPSERT with cmpId + masterId
✓ Return max AlterID after successful sync
✓ Log sync attempts and outcomes
✓ Handle entity-specific constraints

---

## Database Schema

All masters have:
- `cmpid` (Long) - Company ID
- `userid` (Long) - User ID
- `masterId` (Long) - Tally Master ID
- `alterid` (Long) - Tally AlterID (per-entity)
- `guid` (String) - Unique identifier from Tally

UPSERT Logic:
```sql
SELECT * FROM entity_table 
WHERE cmpid = ? AND masterid = ?
```

---

## Performance Considerations

1. **Index on (cmpid, alterid)**
   ```sql
   CREATE INDEX idx_cmpid_alterid ON entity_table(cmpid, alterid);
   ```

2. **Batch sync**
   - Sync 100-500 records per request
   - Reduces network roundtrips
   - Improves database transaction efficiency

3. **Caching**
   - Cache master mapping for 5 minutes
   - Reduces database queries during bulk sync
