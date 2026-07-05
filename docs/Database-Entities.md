# Database Entities

## User

- id
- username
- email
- password
- role
- status
- createdAt

---

## Role

- id
- roleName

---

## Department

- id
- departmentName

---

## Category

- id
- categoryName

---

## Vendor

- id
- vendorName

---

## Asset

- id
- assetCode
- assetName
- serialNumber
- purchaseDate
- purchaseCost
- warrantyExpiry
- status
- categoryId
- vendorId

---

## Asset Assignment

- id
- assetId
- employeeId
- assignedDate
- returnedDate

---

## Maintenance

- id
- assetId
- issue
- status
- scheduledDate
