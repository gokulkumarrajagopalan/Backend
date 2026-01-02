# Master Sync Implementation - Complete Guide

## 📋 Overview

This document describes the complete implementation of **per-entity AlterID synchronization** from Tally Prime to your backend database.

### Key Principle: AlterID is Scoped Per Entity Per Company

✅ **CORRECT:** Each entity maintains its own AlterID sequence
```
Company 9:
  - Groups: max(alterid) = 211
  - Ledgers: max(alterid) = 115
  - StockItems: max(alterid) = 219
```

❌ **WRONG:** Using a global max AlterID across all entities (causes data loss)

---

## 🎯 Architecture

### Master Entities (12 Total)

1. **Group** - Account classification
2. **Ledger** - Individual accounts
3. **StockItem** - Inventory items
4. **StockCategory** - Stock classification
5. **StockGroup** - Stock hierarchy
6. **Units** - Measurement units
7. **CostCategory** - Cost classification
8. **CostCenter** - Profit centers
9. **Currency** - Multi-currency
10. **Godown** - Warehouse locations
11. **TaxUnit** - Tax rates
12. **VoucherType** - Transaction types

---

## 📁 Files Created/Modified

### New Files Created

1. **test_master_sync.ps1**
   - PowerShell test script
   - Tests all 4 sample masters with real Tally data
   - Verifies endpoints work correctly
   - **Location:** `/test_master_sync.ps1`

2. **MASTER_SYNC_ENDPOINTS.md**
   - Complete API documentation
   - All endpoint details with examples
   - Correct sync flow diagram
   - Best practices and error solutions
   - **Location:** `/MASTER_SYNC_ENDPOINTS.md`

3. **MASTER_SYNC_QUERIES.sql**
   - SQL verification queries
   - Check max AlterID per master
   - Verify UPSERT logic (no duplicates)
   - Performance indexes
   - **Location:** `/MASTER_SYNC_QUERIES.sql`

### Modified Files

#### Repositories (12 files)
Added `getMaxAlterIdForCompany()` method to each:
- `GroupRepository.java`
- `LedgerRepository.java`
- `StockItemRepository.java`
- `StockCategoryRepository.java`
- `StockGroupRepository.java`
- `UnitsRepository.java`
- `CostCategoryRepository.java`
- `CostCenterRepository.java`
- `CurrencyRepository.java`
- `GodownRepository.java`
- `TaxUnitRepository.java`
- `VoucherTypeRepository.java`

#### Service
**CompanySyncStatusService.java**
- Added 12 repository autowires
- Added `getEntityAlterIdMapping(cmpId)` method

#### Controller
**CompanySyncStatusController.java**
- Added `getMasterAlterIdMapping()` endpoint: `GET /api/companies/{cmpId}/master-mapping`

---

## 🚀 Quick Start

### 1. Run the Test Script

```powershell
cd D:\Talliffy\TallyBackend
PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1
```

**What it does:**
- Gets master mapping for company 9
- Syncs 2 Units (AlterID 208, 209)
- Syncs 1 StockCategory (AlterID 210)
- Syncs 1 StockGroup (AlterID 215)
- Syncs 2 StockItems (AlterID 214, 219)
- Verifies updated mapping

**Expected Output:**
```
[TEST 1] Get Master AlterID Mapping
✓ Master Mapping Retrieved Successfully

[TEST 2] Sync Units
✓ Units synced successfully (2 units)

[TEST 3] Sync StockCategory
✓ Stock Categories synced successfully (1 categories)

[TEST 4] Sync StockGroup
✓ Stock Groups synced successfully (1 groups)

[TEST 5] Sync StockItem
✓ Stock Items synced successfully (2 items)

[TEST 6] Get Updated Master Mapping After Sync
✓ Updated Master Mapping:
  - Units: 209
  - StockCategory: 210
  - StockGroup: 215
  - StockItem: 219
```

---

## 🔌 API Endpoints

### 1. Get Master AlterID Mapping
```http
GET /api/companies/{cmpId}/master-mapping
```

**Example:**
```bash
curl http://localhost:8080/api/companies/9/master-mapping
```

**Response:**
```json
{
  "success": true,
  "cmpId": 9,
  "masters": {
    "group": 211,
    "ledger": 115,
    "stockitem": 219,
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

### 2. Sync Units
```http
POST /units/sync
```

### 3. Sync Stock Categories
```http
POST /stock-categories/sync
```

### 4. Sync Stock Groups
```http
POST /stock-groups/sync
```

### 5. Sync Stock Items
```http
POST /stock-items/sync
```

---

## 📊 Sync Workflow

```
For each Company:
  │
  ├─ 1. Get master mapping
  │  └─ GET /api/companies/{cmpId}/master-mapping
  │     Returns: { "masters": { "units": 209, "stockitem": 219, ... } }
  │
  ├─ 2. For each Master Type:
  │  │
  │  ├─ Units
  │  │  ├─ Get last AlterID: 209
  │  │  ├─ Fetch from Tally: AlterID > 209
  │  │  └─ POST /units/sync
  │  │
  │  ├─ StockItem
  │  │  ├─ Get last AlterID: 219
  │  │  ├─ Fetch from Tally: AlterID > 219
  │  │  └─ POST /stock-items/sync
  │  │
  │  └─ (repeat for other masters)
  │
  └─ 3. Verify sync
     └─ GET /api/companies/{cmpId}/master-mapping
        Verify counts increased
```

---

## ✅ Verification Steps

### Step 1: Check Master Mapping
```bash
curl http://localhost:8080/api/companies/9/master-mapping
```
✓ Should return 12 master counts

### Step 2: Run SQL Queries
Use `MASTER_SYNC_QUERIES.sql` to verify:
```sql
-- Check max AlterID for Units
SELECT COALESCE(MAX(alterid), 0) FROM units WHERE cmpid = 9;
-- Expected: 209

-- Check no duplicates
SELECT COUNT(*) FROM units WHERE cmpid = 9 AND masterid = 206;
-- Expected: 1
```

### Step 3: Test PowerShell Script
```powershell
PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1
```
✓ All tests should pass

---

## 🔧 Database Setup

### Create Indexes (Recommended)
```sql
CREATE INDEX idx_units_cmpid_alterid ON units(cmpid, alterid);
CREATE INDEX idx_units_cmpid_masterid ON units(cmpid, masterid);
CREATE INDEX idx_stock_items_cmpid_alterid ON stock_items(cmpid, alterid);
CREATE INDEX idx_stock_items_cmpid_masterid ON stock_items(cmpid, masterid);
CREATE INDEX idx_stock_categories_cmpid_alterid ON stock_categories(cmpid, alterid);
CREATE INDEX idx_stock_categories_cmpid_masterid ON stock_categories(cmpid, masterid);
CREATE INDEX idx_stock_groups_cmpid_alterid ON stock_groups(cmpid, alterid);
CREATE INDEX idx_stock_groups_cmpid_masterid ON stock_groups(cmpid, masterid);
```

---

## 📋 Test Data (Included in Script)

### Units (2 records)
| Name | MasterID | AlterID | GUID |
|------|----------|---------|------|
| $ | 206 | 208 | badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000ce |
| @! | 207 | 209 | badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000cf |

### StockCategory (1 record)
| Name | MasterID | AlterID |
|------|----------|---------|
| Goods | 208 | 210 |

### StockGroup (1 record)
| Name | MasterID | AlterID |
|------|----------|---------|
| Sad | 213 | 215 |

### StockItem (2 records)
| Name | MasterID | AlterID |
|------|----------|---------|
| Us | 212 | 214 |
| Wd | 215 | 219 |

---

## 🐛 Troubleshooting

### Problem: Endpoint returns 404
**Solution:** Ensure Spring Boot application is running on port 8080

### Problem: Sync endpoint returns 500
**Solution:** Check logs for missing mandatory fields in request JSON

### Problem: Master mapping shows 0 for all
**Solution:** Check database connection and verify tables exist

### Problem: Data duplication after sync
**Solution:** This should NOT happen - UPSERT uses `cmpId + masterId` for reconciliation
- Verify: `SELECT COUNT(*) FROM units WHERE cmpid = 9 AND masterid = 206;` should be 1

### Problem: Some AlterIDs missing
**Solution:** This was the old bug - verify you're using per-entity AlterID (NOT global)

---

## 📚 Documentation Files

1. **MASTER_SYNC_ENDPOINTS.md** - Complete API reference
2. **MASTER_SYNC_QUERIES.sql** - SQL verification queries
3. **test_master_sync.ps1** - Automated test script
4. **README.md** (this file) - Quick start guide

---

## ✨ Key Features Implemented

✅ Per-entity AlterID queries (correct approach)
✅ Master mapping endpoint (GET /api/companies/{cmpId}/master-mapping)
✅ Repositories with getMaxAlterIdForCompany() method (all 12)
✅ Service aggregates all master mappings
✅ Controller endpoint returns JSON mapping
✅ PowerShell test script with real Tally data
✅ SQL verification queries
✅ Complete documentation

---

## 🎓 What You Learned

1. **AlterID Scope:** Per entity, not global
2. **Database Design:** Use `cmpId + masterId` for UPSERT
3. **Sync Strategy:** Incremental sync by AlterID
4. **API Design:** Mapping endpoint for sync coordination
5. **Testing:** Both automated (PowerShell) and manual (SQL)

---

## 📞 Support

**Endpoints Ready:**
- ✅ GET /api/companies/{cmpId}/master-mapping
- ✅ POST /units/sync
- ✅ POST /stock-categories/sync
- ✅ POST /stock-groups/sync
- ✅ POST /stock-items/sync

**All 4 masters tested with sample Tally data** ✓

---

## 🎯 Next Steps

1. ✅ Test the endpoints with the PowerShell script
2. ✅ Verify database has the synced data
3. ✅ Integrate with your Tally API client
4. ✅ Implement sync scheduler
5. ✅ Monitor and log sync operations

---

**Last Updated:** January 1, 2026
**Status:** ✅ Ready for Production
