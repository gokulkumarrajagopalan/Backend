# Master Sync - Quick Reference Card

## 🔗 Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/companies/{cmpId}/master-mapping` | GET | Get all 12 masters with max AlterID |
| `/units/sync` | POST | Sync Units from Tally |
| `/stock-categories/sync` | POST | Sync Stock Categories |
| `/stock-groups/sync` | POST | Sync Stock Groups |
| `/stock-items/sync` | POST | Sync Stock Items |

---

## 📋 Master Mapping Response

```json
{
  "success": true,
  "cmpId": 9,
  "masters": {
    "group": 211,              // Groups AlterID
    "ledger": 115,             // Ledgers AlterID
    "stockitem": 219,          // Stock Items AlterID
    "stockcategory": 210,      // Stock Categories AlterID
    "stockgroup": 215,         // Stock Groups AlterID
    "units": 209,              // Units AlterID
    "costcategory": 5,         // Cost Categories AlterID
    "costcenter": 22,          // Cost Centers AlterID
    "currency": 3,             // Currencies AlterID
    "godown": 8,               // Godowns AlterID
    "taxunit": 0,              // Tax Units AlterID
    "vouchertype": 7           // Voucher Types AlterID
  }
}
```

---

## 🔄 Sync Flow

```
1. GET /api/companies/9/master-mapping
   └─ Get current AlterID for each master

2. For each master:
   ├─ Get lastAlterID from mapping
   ├─ Fetch from Tally: AlterID > lastAlterID
   └─ POST to sync endpoint with records

3. GET /api/companies/9/master-mapping
   └─ Verify updated AlterIDs
```

---

## 🧪 Test Command

```powershell
PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1
```

---

## 📊 Test Data

| Master | Record Count | AlterID Range |
|--------|-------------|---------------|
| Units | 2 | 208-209 |
| StockCategory | 1 | 210 |
| StockGroup | 1 | 215 |
| StockItem | 2 | 214, 219 |

---

## ✅ Verification Queries

```sql
-- Check Units max AlterID
SELECT MAX(alterid) FROM units WHERE cmpid = 9;
-- Expected: 209

-- Check no duplicates
SELECT COUNT(*) FROM units WHERE cmpid = 9;
-- Expected: 2
```

---

## 🎯 Key Principle

**AlterID is per Entity, NOT per Company**

```
✅ CORRECT:
  Units: SELECT MAX(alterid) FROM units WHERE cmpid = 9;
  StockItem: SELECT MAX(alterid) FROM stock_items WHERE cmpid = 9;
  
❌ WRONG:
  SELECT MAX(alterid) FROM (
    SELECT alterid FROM units
    UNION ALL
    SELECT alterid FROM stock_items
  ) x;  ← This causes data loss!
```

---

## 🚀 Quick Start

1. **Start App**
   ```
   mvn spring-boot:run
   ```

2. **Run Test**
   ```powershell
   PowerShell -ExecutionPolicy Bypass -File test_master_sync.ps1
   ```

3. **Check Results**
   ```bash
   curl http://localhost:8080/api/companies/9/master-mapping
   ```

---

## 📁 Files

| File | Purpose |
|------|---------|
| `test_master_sync.ps1` | Automated test |
| `MASTER_SYNC_ENDPOINTS.md` | API docs |
| `MASTER_SYNC_QUERIES.sql` | SQL verification |
| `README_MASTER_SYNC.md` | Guide |
| `IMPLEMENTATION_SUMMARY.txt` | Summary |

---

## 🔧 Repository Methods

All 12 repositories have:
```java
Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
```

Returns: `COALESCE(MAX(alterid), 0)` for company

---

## 📦 Service Method

```java
Map<String, Long> getEntityAlterIdMapping(Long cmpId)
```

Returns map with 12 masters and their max AlterIDs

---

## ✨ Features

✅ Per-entity AlterID (Tally-compliant)
✅ 12 masters supported
✅ Fast mapping lookup
✅ No duplicate records (UPSERT)
✅ Complete documentation
✅ Automated tests
✅ SQL verification
✅ Ready for production

---

## 🐛 Common Issues

| Issue | Solution |
|-------|----------|
| 404 on endpoint | Ensure app is running on 8080 |
| 500 on sync | Check mandatory fields in JSON |
| Mapping shows 0 | Verify database connection |
| Duplicate records | Check UPSERT logic (use cmpId + masterId) |

---

**Last Updated:** January 1, 2026  
**Status:** ✅ Production Ready
