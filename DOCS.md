# Dokumentasi API - User

Dokumentasi ini menjelaskan cara pemakaian endpoint pada `UserController`.

## Base URL

- Local: `http://localhost:8081`
- Prefix API: `/api/users`
- Header wajib untuk request body: `Content-Type: application/json`

## Cara Pakai Singkat

1. Jalankan service.
2. Gunakan endpoint dengan base URL `http://localhost:8081`.
3. Untuk endpoint chat by date, pakai query `date` format `yyyy-MM-dd` (contoh: `2026-04-20`).

Contoh endpoint lengkap:
- `GET http://localhost:8081/api/users`
- `GET http://localhost:8081/api/users/user-001`
- `GET http://localhost:8081/api/users/user-001/journal/chats?date=2026-04-20`

## Struktur JSON

### 1) UserPayload (untuk create/update user)

```json
{
  "id": "user-001",
  "name": "Budi",
  "email": "budi@mail.com",
  "phone": "08123456789",
  "password": "rahasia",
  "onboardingCompleted": true,
  "level": 3,
  "investmentWatchlist": {
    "items": [
      {
        "symbol": "BBCA.JK",
        "name": "Bank Central Asia",
        "type": "stock",
        "region": "ID",
        "currency": "IDR",
        "createdAt": "2026-04-20T08:30:00Z"
      }
    ],
    "selectedSymbol": "BBCA.JK",
    "updatedAt": "2026-04-20T08:30:00Z"
  },
  "journal": {
    "nextChatMessageId": 3,
    "chatByDate": {
      "2026-04-20": [
        {
          "id": 1,
          "sender": "user",
          "text": "Halo",
          "time": "09:00"
        }
      ]
    },
    "expensesByDate": {
      "2026-04-20": [
        {
          "amount": 50000,
          "description": "Makan siang",
          "category": "Food"
        }
      ]
    },
    "incomesByDate": {
      "2026-04-20": [
        {
          "amount": 1000000,
          "description": "Gaji freelance",
          "source": "Freelance"
        }
      ]
    }
  },
  "financialData": {
    "pendapatan": 7000000,
    "pengeluaranWajib": 3000000,
    "tanggalPemasukan": 25,
    "intendedTanggalPemasukan": 25,
    "hutangWajib": 500000,
    "estimasiTabungan": 1500000,
    "danaDarurat": 4000000,
    "budgetAllocation": {
      "mode": 1,
      "pengeluaran": 50,
      "wants": 30,
      "savings": 20
    },
    "currentPengeluaranLimit": 2500000,
    "currentPengeluaranUsed": 1200000,
    "currentSisaSaldoPool": 1300000,
    "lastCycleCarryOverSaldo": 200000,
    "monthlyTopUp": {
      "cycleKey": "2026-04",
      "fromTabunganCount": 1,
      "totalFromTabungan": 100000,
      "totalFromDanaDarurat": 0
    },
    "currentCycleStart": "2026-04-01",
    "currentCycleEnd": "2026-04-30"
  },
  "streak": {
    "current": 4,
    "longest": 12,
    "lastActiveDate": "2026-04-20",
    "freezeUsed": false
  },
  "debts": [
    {
      "id": "debt-001",
      "name": "Kredit Motor",
      "category": "Transport",
      "debtType": "installment",
      "principalAmount": 15000000,
      "remainingAmount": 8000000,
      "monthlyInstallment": 750000,
      "dueDay": 10,
      "notes": "Bayar via autodebet"
    }
  ]
}
```

### 2) DebtPayload

```json
{
  "id": "debt-002",
  "name": "Pinjaman Keluarga",
  "category": "Personal",
  "debtType": "loan",
  "principalAmount": 5000000,
  "remainingAmount": 3000000,
  "monthlyInstallment": 500000,
  "dueDay": 15,
  "notes": "Transfer tiap tanggal 15"
}
```

### 3) CreateChatMessageRequest

```json
{
  "sender": "user",
  "text": "Hari ini pengeluaran saya berapa ya?",
  "time": "10:15"
}
```

### 4) ChatMessagePayload (response chat)

```json
{
  "id": 1,
  "sender": "user",
  "text": "Hari ini pengeluaran saya berapa ya?",
  "time": "10:15"
}
```

## Daftar Endpoint

### 1. Create User
- Method: `POST`
- URL: `/api/users`
- Full URL: `http://localhost:8081/api/users`
- Body: `UserPayload`
- Success: `201 Created`

Contoh request body: pakai JSON `UserPayload` di atas.

### 2. Get All Users
- Method: `GET`
- URL: `/api/users`
- Full URL: `http://localhost:8081/api/users`
- Success: `200 OK`

Contoh response:
```json
[
  {
    "id": "user-001",
    "name": "Budi",
    "email": "budi@mail.com",
    "phone": "08123456789",
    "password": "rahasia",
    "onboardingCompleted": true,
    "level": 3,
    "investmentWatchlist": null,
    "journal": null,
    "financialData": null,
    "streak": null,
    "debts": []
  }
]
```

### 3. Get User By ID
- Method: `GET`
- URL: `/api/users/{id}`
- Full URL: `http://localhost:8081/api/users/user-001`
- Success: `200 OK`

### 4. Update User (Replace)
- Method: `PUT`
- URL: `/api/users/{id}`
- Full URL: `http://localhost:8081/api/users/user-001`
- Body: `UserPayload`
- Success: `200 OK`

Catatan: `id` pada path dan body harus sama.

### 4b. Patch Financial Data (Partial Update)
- Method: `PATCH`
- URL: `/api/users/{id}/financial-data`
- Full URL: `http://localhost:8081/api/users/user-001/financial-data`
- Body: `FinancialDataPatchPayload` (hanya field yang mau diubah)
- Success: `200 OK`

Contoh request body:
```json
{
  "pendapatan": 8000000,
  "danaDarurat": 4500000,
  "budgetAllocation": {
    "wants": 25,
    "savings": 25
  },
  "monthlyTopUp": {
    "totalFromTabungan": 250000
  },
  "currentCycleEnd": "2026-05-31"
}
```

Catatan: field yang tidak dikirim akan tetap memakai nilai lama.

### 5. Delete User
- Method: `DELETE`
- URL: `/api/users/{id}`
- Full URL: `http://localhost:8081/api/users/user-001`
- Success: `204 No Content`

### 6. Get Debts By User ID
- Method: `GET`
- URL: `/api/users/{id}/debts`
- Full URL: `http://localhost:8081/api/users/user-001/debts`
- Success: `200 OK`

Contoh response:
```json
[
  {
    "id": "debt-001",
    "name": "Kredit Motor",
    "category": "Transport",
    "debtType": "installment",
    "principalAmount": 15000000,
    "remainingAmount": 8000000,
    "monthlyInstallment": 750000,
    "dueDay": 10,
    "notes": "Bayar via autodebet"
  }
]
```

### 7. Add Debt
- Method: `POST`
- URL: `/api/users/{id}/debts`
- Full URL: `http://localhost:8081/api/users/user-001/debts`
- Body: `DebtPayload`
- Success: `201 Created`

Contoh request body: pakai JSON `DebtPayload` di atas.

### 8. Get Chat By Date
- Method: `GET`
- URL: `/api/users/{id}/journal/chats?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/chats?date=2026-04-20`
- Success: `200 OK`
- Response: `List<ChatMessagePayload>`

Contoh response:
```json
[
  {
    "id": 1,
    "sender": "user",
    "text": "Halo",
    "time": "09:00"
  }
]
```

### 9. Add Chat Message
- Method: `POST`
- URL: `/api/users/{id}/journal/chats?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/chats?date=2026-04-20`
- Body: `CreateChatMessageRequest`
- Success: `201 Created`
- Response: `ChatMessagePayload`

Contoh request body: pakai JSON `CreateChatMessageRequest` di atas.

### 10. Get Expenses By Date
- Method: `GET`
- URL: `/api/users/{id}/journal/expenses?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/expenses?date=2026-04-20`
- Success: `200 OK`
- Response: `List<ExpensePayload>`

Contoh response:
```json
[
  {
    "amount": 50000,
    "description": "Makan siang",
    "category": "Food"
  }
]
```

### 11. Add Expense
- Method: `POST`
- URL: `/api/users/{id}/journal/expenses?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/expenses?date=2026-04-20`
- Body:
```json
{
  "amount": 50000,
  "description": "Makan siang",
  "category": "Food"
}
```
- Success: `201 Created`
- Response: `ExpensePayload`

### 12. Get Incomes By Date
- Method: `GET`
- URL: `/api/users/{id}/journal/incomes?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/incomes?date=2026-04-20`
- Success: `200 OK`
- Response: `List<IncomePayload>`

Contoh response:
```json
[
  {
    "amount": 1000000,
    "description": "Gaji freelance",
    "source": "Freelance"
  }
]
```

### 13. Add Income
- Method: `POST`
- URL: `/api/users/{id}/journal/incomes?date=yyyy-MM-dd`
- Full URL: `http://localhost:8081/api/users/user-001/journal/incomes?date=2026-04-20`
- Body:
```json
{
  "amount": 1000000,
  "description": "Gaji freelance",
  "source": "Freelance"
}
```
- Success: `201 Created`
- Response: `IncomePayload`

## FinancialData

> **Penting:** `financialData` bisa dikelola dengan 2 cara: `PATCH` khusus financial data (partial update) atau lewat `POST/GET/PUT/DELETE` pada endpoint user.

## Journal — Ringkasan Endpoint

> Data `journal`, `journal_expenses`, `journal_incomes` dikelola lewat endpoint terpisah per resource per tanggal. Format tanggal selalu `yyyy-MM-dd`.

| Aksi | Method | URL |
|------|--------|-----|
| Lihat chat per tanggal | `GET` | `/api/users/{id}/journal/chats?date=yyyy-MM-dd` |
| Tambah chat | `POST` | `/api/users/{id}/journal/chats?date=yyyy-MM-dd` |
| Lihat pengeluaran per tanggal | `GET` | `/api/users/{id}/journal/expenses?date=yyyy-MM-dd` |
| Tambah pengeluaran | `POST` | `/api/users/{id}/journal/expenses?date=yyyy-MM-dd` |
| Lihat pemasukan per tanggal | `GET` | `/api/users/{id}/journal/incomes?date=yyyy-MM-dd` |
| Tambah pemasukan | `POST` | `/api/users/{id}/journal/incomes?date=yyyy-MM-dd` |

> Catatan: data journal **bulk** (semua tanggal sekaligus) bisa dibaca lewat `GET /api/users/{id}` — field `journal.chatByDate`, `journal.expensesByDate`, `journal.incomesByDate`.

### Cara Mengakses / Mengubah FinancialData

| Aksi | Method | URL |
|------|--------|-----|
| **Buat** user beserta financialData | `POST` | `/api/users` |
| **Baca** financialData user | `GET` | `/api/users/{id}` |
| **Update sebagian** financialData user | `PATCH` | `/api/users/{id}/financial-data` |
| **Update** financialData user | `PUT` | `/api/users/{id}` |
| **Hapus** user (dan semua datanya) | `DELETE` | `/api/users/{id}` |

### Alur Update FinancialData

**Opsi A (partial update):**
1. `PATCH /api/users/{id}/financial-data`.
2. Kirim hanya field `financialData` yang mau diubah.

**Opsi B (replace penuh):**
1. `GET /api/users/{id}` → ambil data user lengkap.
2. Modifikasi bagian `financialData` di JSON.
3. `PUT /api/users/{id}` → kirim kembali seluruh `UserPayload` yang sudah diubah.

### Contoh JSON FinancialData (bagian dari UserPayload)

**Request — `PUT http://localhost:8081/api/users/user-001`**

```json
{
  "id": "user-001",
  "name": "Budi",
  "email": "budi@mail.com",
  "phone": "08123456789",
  "password": "rahasia",
  "onboardingCompleted": true,
  "level": 3,
  "investmentWatchlist": null,
  "journal": null,
  "streak": null,
  "debts": [],
  "financialData": {
    "pendapatan": 7000000,
    "pengeluaranWajib": 3000000,
    "tanggalPemasukan": 25,
    "intendedTanggalPemasukan": 25,
    "hutangWajib": 500000,
    "estimasiTabungan": 1500000,
    "danaDarurat": 4000000,
    "budgetAllocation": {
      "mode": 1,
      "pengeluaran": 50,
      "wants": 30,
      "savings": 20
    },
    "currentPengeluaranLimit": 2500000,
    "currentPengeluaranUsed": 1200000,
    "currentSisaSaldoPool": 1300000,
    "lastCycleCarryOverSaldo": 200000,
    "monthlyTopUp": {
      "cycleKey": "2026-04",
      "fromTabunganCount": 1,
      "totalFromTabungan": 100000,
      "totalFromDanaDarurat": 0
    },
    "currentCycleStart": "2026-04-01",
    "currentCycleEnd": "2026-04-30"
  }
}
```

**Response — `200 OK`** (data user lengkap termasuk `financialData` yang baru).

### Penjelasan Field FinancialData

| Field | Tipe | Keterangan |
|-------|------|-----------|
| `pendapatan` | `Long` | Total pendapatan per bulan |
| `pengeluaranWajib` | `Long` | Total pengeluaran wajib per bulan |
| `tanggalPemasukan` | `Int` | Tanggal aktual gaji masuk (1–31) |
| `intendedTanggalPemasukan` | `Int` | Tanggal pemasukan yang direncanakan |
| `hutangWajib` | `Long` | Total cicilan hutang wajib per bulan |
| `estimasiTabungan` | `Long` | Target tabungan per bulan |
| `danaDarurat` | `Long` | Saldo dana darurat saat ini |
| `currentPengeluaranLimit` | `Long` | Batas pengeluaran siklus ini |
| `currentPengeluaranUsed` | `Long` | Pengeluaran yang sudah terpakai |
| `currentSisaSaldoPool` | `Long` | Sisa saldo yang bisa dipakai |
| `lastCycleCarryOverSaldo` | `Long` | Sisa saldo dari siklus sebelumnya |
| `currentCycleStart` | `String?` | Tanggal mulai siklus (`yyyy-MM-dd`) |
| `currentCycleEnd` | `String?` | Tanggal akhir siklus (`yyyy-MM-dd`) |
| `budgetAllocation.mode` | `Int` | Mode alokasi anggaran |
| `budgetAllocation.pengeluaran` | `Int` | Persentase pengeluaran (%) |
| `budgetAllocation.wants` | `Int` | Persentase keinginan (%) |
| `budgetAllocation.savings` | `Int` | Persentase tabungan (%) |
| `monthlyTopUp.cycleKey` | `String?` | Key siklus (contoh: `"2026-04"`) |
| `monthlyTopUp.fromTabunganCount` | `Int` | Jumlah topup dari tabungan |
| `monthlyTopUp.totalFromTabungan` | `Long` | Total nominal dari tabungan |
| `monthlyTopUp.totalFromDanaDarurat` | `Long` | Total nominal dari dana darurat |

---

## Error Response Umum

Format error dari API:

```json
{
  "message": "Validation failed",
  "details": {
    "email": "must be a well-formed email address"
  }
}
```

Contoh pesan lain:
- `User not found` (`404`)
- `Email already exists` (`409`)
- `User id already exists` (`409`)
- `Path id and body id must match` (`400`)
- `Date must use ISO format yyyy-MM-dd` (`400`)
- `Timestamp must use ISO format` (`400`)

