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
    "danaInvestasi": 200000,
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
    "currentCycleEnd": "2026-04-30",
    "savingsAllocation": {
      "tabungan": 200000,
      "danaDarurat": 200000,
      "danaInvestasi": 200000
    },
    "investmentTracking": {
      "cycleAmounts": {
        "2026-04-25": 2500000,
        "2026-05-25": 2500000
      }
    }
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
  "text": "makan sapi 10000",
  "time": "21:17"
}
```

### 4) ChatMessagePayload (response chat)

```json
{
  "id": 1,
  "sender": "user",
  "text": "makan sapi 10000",
  "time": "21:17",
  "parsedText": "makan sapi",
  "parsedNominal": 10000
}
```

> `parsedText` dan `parsedNominal` hanya diisi otomatis saat `sender == "user"`. Pesan dari `assistant` akan selalu `null` untuk kedua field tersebut.

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

Catatan: `id` pada path dan body harus sama. Server akan **update** user yang sudah ada (bukan insert baru). Nested entities (`financialData`, `journal`, `investmentWatchlist`, `streak`) di-update in-place — ID internal database tetap sama.

### 4b. Patch Financial Data (Partial Update)
- Method: `PATCH`
- URL: `/api/users/{id}/financial-data`
- Full URL: `http://localhost:8081/api/users/user-001/financial-data`
- Body: `FinancialDataPatchPayload` (hanya field yang mau diubah)
- Success: `200 OK`

**3 Cara Update:**

**Cara 1: Patch absolut (saldo + field biasa)**
```json
{
  "pendapatan": 8000000,
  "danaDarurat": 4500000,
  "danaInvestasi": 350000,
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
→ Field dikirim langsung akan di-replace.

**Cara 2: Patch delta (akumulasi setoran)**
```json
{
  "savingsAllocationDelta": {
    "tabungan": 500000,
    "danaDarurat": 250000,
    "danaInvestasi": 100000
  }
}
```
→ Field `savingsAllocation` akan **ditambah** (bukan replace).

**Cara 3: Patch investment tracking (merge per key)**
```json
{
  "investmentTracking": {
    "cycleAmounts": {
      "2026-06-25": 3000000
    }
  }
}
```
→ Merge dengan key existing (bukan overwrite total).

Catatan: field yang tidak dikirim akan tetap memakai nilai lama. Lihat **Skenario Update FinancialData** untuk contoh real-world.



### 4c. Patch Investment Watchlist (Partial Update)
- Method: `PATCH`
- URL: `/api/users/{id}/investment-watchlist`
- Full URL: `http://localhost:8081/api/users/user-001/investment-watchlist`
- Body: `InvestmentWatchlistPatchPayload` (hanya field yang mau diubah)
- Success: `200 OK`

> **Use-case:** Frontend cukup kirim watchlist saja, tidak perlu kirim seluruh `UserPayload`.

**Patch hanya `selectedSymbol`:**
```json
{
  "selectedSymbol": "BBRI.JK"
}
```

**Patch seluruh daftar item (replace items):**
```json
{
  "items": [
    {
      "symbol": "BBCA.JK",
      "name": "Bank Central Asia",
      "type": "stock",
      "region": "ID",
      "currency": "IDR",
      "createdAt": "2026-04-20T08:30:00Z"
    },
    {
      "symbol": "TLKM.JK",
      "name": "Telkom Indonesia",
      "type": "stock",
      "region": "ID",
      "currency": "IDR",
      "createdAt": "2026-04-25T10:00:00Z"
    }
  ],
  "selectedSymbol": "TLKM.JK",
  "updatedAt": "2026-04-25T10:00:00Z"
}
```

Catatan: field yang tidak dikirim tetap memakai nilai lama. Jika `items` dikirim, seluruh daftar item akan **diganti** (replace, bukan append).

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
    "text": "makan sapi 10000",
    "time": "21:17",
    "parsedText": "makan sapi",
    "parsedNominal": 10000
  },
  {
    "id": 2,
    "sender": "assistant",
    "text": "Dicatat: Rp 10.000 untuk \"makan sapi\" kategori makanan.",
    "time": "21:17",
    "parsedText": null,
    "parsedNominal": null
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

---

## Chat Natural Language Parsing

Fitur ini memungkinkan user mengetik pesan bebas dan sistem secara otomatis memisahkan **nominal** dan **teks deskripsi**.

### Cara Kerja

- Saat POST chat message dengan `sender == "user"`, server otomatis parse `text`.
- Token yang **hanya terdiri dari angka** akan dianggap sebagai **nominal** (diambil yang pertama).
- Sisa token dijadikan **parsedText** (deskripsi).
- `sender == "assistant"` → `parsedText` dan `parsedNominal` selalu `null`.

### Contoh Input → Output

| Input (`text`) | `parsedText` | `parsedNominal` |
|----------------|--------------|-----------------|
| `makan 10000` | `makan` | `10000` |
| `10000 makan` | `makan` | `10000` |
| `makan sapi 10000` | `makan sapi` | `10000` |
| `123456789 netflix nonton tv` | `netflix nonton tv` | `123456789` |
| `pergi 12345690` | `pergi` | `12345690` |
| `halo saja` | `halo saja` | `null` |
| `100000` | `null` | `100000` |

### Format Pairing Percakapan (User + Assistant)

```json
[
  {
    "sender": "user",
    "text": "makan 100000",
    "time": "21:17"
  },
  {
    "sender": "assistant",
    "text": "Dicatat: Rp 100.000 untuk \"makan\" kategori makanan.",
    "time": "21:17"
  }
]
```

Keduanya dikirim sebagai 2 request POST terpisah (masing-masing 1 pesan), dengan `id` auto-increment oleh server (1, 2, 3, ..., n).

### Request & Response

**Request:**
```bash
POST http://localhost:8081/api/users/user-001/journal/chats?date=2026-04-20
Content-Type: application/json

{
  "sender": "user",
  "text": "makan sapi 10000",
  "time": "21:17"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "sender": "user",
  "text": "makan sapi 10000",
  "time": "21:17",
  "parsedText": "makan sapi",
  "parsedNominal": 10000
}
```

**Request (assistant reply):**
```bash
POST http://localhost:8081/api/users/user-001/journal/chats?date=2026-04-20
Content-Type: application/json

{
  "sender": "assistant",
  "text": "Dicatat: Rp 10.000 untuk \"makan sapi\" kategori makanan.",
  "time": "21:17"
}
```

**Response `201 Created`:**
```json
{
  "id": 2,
  "sender": "assistant",
  "text": "Dicatat: Rp 10.000 untuk \"makan sapi\" kategori makanan.",
  "time": "21:17",
  "parsedText": null,
  "parsedNominal": null
}
```

### Database Schema (chat_messages)

Kolom baru yang ditambahkan (auto-migrate, tidak perlu drop table):
```
parsed_text    VARCHAR(1000) NULL   -- deskripsi yang sudah dipisah
parsed_nominal BIGINT        NULL   -- nominal yang sudah dipisah
```

Kolom lama tetap ada:
```
text           VARCHAR(1000) NOT NULL  -- raw input asli dari user
```

> **Best Practice:** Simpan `text` asli apa adanya (raw), dan baca `parsedText`/`parsedNominal` di FE untuk display. Dengan begini kalau parsing logic berubah, data lama masih bisa di-reparse dari `text`.

---

### Cara Mengakses / Mengubah FinancialData

| Aksi | Method | URL |
|------|--------|-----|
| **Buat** user beserta financialData | `POST` | `/api/users` |
| **Baca** financialData user | `GET` | `/api/users/{id}` |
| **Update sebagian** financialData user | `PATCH` | `/api/users/{id}/financial-data` |
| **Update** financialData user | `PUT` | `/api/users/{id}` |
| **Hapus** user (dan semua datanya) | `DELETE` | `/api/users/{id}` |

### Cara Mengakses / Mengubah Investment Watchlist

| Aksi | Method | URL |
|------|--------|-----|
| **Buat** user beserta watchlist | `POST` | `/api/users` |
| **Baca** watchlist user | `GET` | `/api/users/{id}` |
| **Update sebagian** watchlist (tanpa kirim full user) | `PATCH` | `/api/users/{id}/investment-watchlist` |
| **Replace penuh** watchlist (via full user) | `PUT` | `/api/users/{id}` |

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
    "danaInvestasi": 200000,
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
    "currentCycleEnd": "2026-04-30",
    "savingsAllocation": {
      "tabungan": 200000,
      "danaDarurat": 200000,
      "danaInvestasi": 200000
    },
    "investmentTracking": {
      "cycleAmounts": {
        "2026-04-25": 2500000,
        "2026-05-25": 2500000
      }
    }
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
| `danaInvestasi` | `Long` | Saldo dana investasi saat ini |
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
| `savingsAllocation.tabungan` | `Long` | Akumulasi total setoran ke tabungan |
| `savingsAllocation.danaDarurat` | `Long` | Akumulasi total setoran ke dana darurat |
| `savingsAllocation.danaInvestasi` | `Long` | Akumulasi total setoran ke dana investasi |
| `investmentTracking.cycleAmounts` | `Map<String, Long>` | Riwayat nominal investasi per cycle key |

### Best Practice (Avoid Redundant)

- `estimasiTabungan`, `danaDarurat`, `danaInvestasi` = **saldo saat ini** (snapshot).
- `savingsAllocation` = **akumulasi setoran user selama pencatatan** (historical cumulative).
- Gunakan `savingsAllocationDelta` saat patch transaksi baru agar tidak overwrite total akumulasi.
- Gunakan `savingsAllocation` (non-delta) hanya saat koreksi/manual backfill.

### Skenario Update FinancialData

#### Skenario A: User top-up tabungan
**Flow:**
1. User melakukan top-up tabungan **Rp 500.000** dari gaji.
2. Client kirim PATCH dengan `savingsAllocationDelta` (bukan absolut).
3. Server akan: `savingsAllocation.tabungan += 500000`.

**Request:**
```bash
PATCH http://localhost:8081/api/users/user-001/financial-data
Content-Type: application/json

{
  "estimasiTabungan": 1500000,
  "savingsAllocationDelta": {
    "tabungan": 500000
  }
}
```

**Expected Response:** `200 OK`
- `estimasiTabungan` berubah ke `1500000` (update saldo)
- `savingsAllocation.tabungan` bertambah dari `200000` → `700000` (akumulasi)

---

#### Skenario B: User withdraw dari dana darurat
**Flow:**
1. User mengambil dana darurat **Rp 250.000** untuk kebutuhan mendesak.
2. Saldo berkurang, tapi akumulasi tetap tercatat.

**Request:**
```bash
PATCH http://localhost:8081/api/users/user-001/financial-data
Content-Type: application/json

{
  "danaDarurat": 34950000,
  "savingsAllocationDelta": {
    "danaDarurat": -250000
  }
}
```

**Expected Response:** `200 OK`
- `danaDarurat` (saldo) berubah ke `34950000` (snapshot saat ini)
- `savingsAllocation.danaDarurat` berkurang dari `200000` → `-50000` (net flow tercatat)

---

#### Skenario C: Admin backfill/koreksi
**Flow:**
1. Admin perlu set ulang `savingsAllocation` ke nilai tertentu (e.g., data sync dari sistem lain).
2. Gunakan `savingsAllocation` absolut (bukan delta).

**Request:**
```bash
PATCH http://localhost:8081/api/users/user-001/financial-data
Content-Type: application/json

{
  "savingsAllocation": {
    "tabungan": 1000000,
    "danaDarurat": 500000,
    "danaInvestasi": 300000
  }
}
```

**Expected Response:** `200 OK`
- `savingsAllocation` di-replace ke nilai yang dikirim (bukan ditambah).

---

#### Skenario D: Add investment cycle tracking
**Flow:**
1. User melakukan investasi **Rp 3.000.000** pada cycle **2026-07-25**.
2. Client patch `investmentTracking.cycleAmounts` dengan key baru.

**Request:**
```bash
PATCH http://localhost:8081/api/users/user-001/financial-data
Content-Type: application/json

{
  "danaInvestasi": 500000,
  "investmentTracking": {
    "cycleAmounts": {
      "2026-07-25": 3000000
    }
  }
}
```

**Expected Response:** `200 OK`
- `danaInvestasi` saldo update
- `investmentTracking.cycleAmounts` merge: key `2026-07-25` ditambah/update dengan value `3000000`
- Key lama (`2026-04-25`, `2026-05-25`) tetap tersimpan

---

### Tabel Perbandingan Update Strategy

| Strategy | Use Case | Behavior |
|----------|----------|----------|
| `savingsAllocation` (absolut) | Admin backfill / koreksi data | Replace total, **BUKAN** tambah |
| `savingsAllocationDelta` | User top-up / withdraw | **Tambah/kurang** ke akumulasi |
| `investmentTracking.cycleAmounts` | Tracking per cycle | Merge per key (bukan overwrite total) |

---

### Architecture Decision

**Mengapa pisahkan saldo vs akumulasi?**

```
Skenario tanpa pisahan:
- User top-up Rp 500.000 → set savingsAllocation.tabungan = 500.000
- User top-up Rp 300.000 lagi → set savingsAllocation.tabungan = 300.000 ❌
  (data sebelumnya hilang!)

Skenario dengan pisahan:
- savingsAllocation.tabungan = 500.000 (akumulasi setoran)
- estimasiTabungan = 1.500.000 (target/saldo saat ini)
- User withdraw Rp 200.000 → 
  estimasiTabungan -= 200.000 (saldo turun)
  savingsAllocationDelta.tabungan -= 200.000 (net flow tercatat)
- Hasil: savingsAllocation.tabungan = 300.000 ✅ (history preserved)
```

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

---

## Contoh cURL / Postman

### Create User dengan FinancialData
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
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
      "pendapatan": 10000000,
      "pengeluaranWajib": 3500000,
      "tanggalPemasukan": 26,
      "intendedTanggalPemasukan": 26,
      "hutangWajib": 0,
      "estimasiTabungan": 40200000,
      "danaDarurat": 35200000,
      "danaInvestasi": 200000,
      "budgetAllocation": {
        "mode": 2,
        "pengeluaran": 35,
        "wants": 0,
        "savings": 65
      },
      "currentPengeluaranLimit": 3500000,
      "currentPengeluaranUsed": 110000,
      "currentSisaSaldoPool": 12524124,
      "lastCycleCarryOverSaldo": 6500000,
      "monthlyTopUp": {
        "cycleKey": "2026-03-25",
        "fromTabunganCount": 0,
        "totalFromTabungan": 0,
        "totalFromDanaDarurat": 0
      },
      "currentCycleStart": "2026-03-26",
      "currentCycleEnd": "2026-04-25",
      "savingsAllocation": {
        "tabungan": 200000,
        "danaDarurat": 200000,
        "danaInvestasi": 200000
      },
      "investmentTracking": {
        "cycleAmounts": {
          "2026-03-25": 8400000,
          "2026-04-25": 2500000,
          "2026-05-25": 2500000
        }
      }
    }
  }'
```

### PATCH Financial Data (Top-up Tabungan)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/financial-data \
  -H "Content-Type: application/json" \
  -d '{
    "estimasiTabungan": 41200000,
    "savingsAllocationDelta": {
      "tabungan": 1000000
    }
  }'
```

### PATCH Financial Data (Withdraw Dana Darurat)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/financial-data \
  -H "Content-Type: application/json" \
  -d '{
    "danaDarurat": 35000000,
    "savingsAllocationDelta": {
      "danaDarurat": -200000
    }
  }'
```

### PATCH Financial Data (Add Investment Cycle)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/financial-data \
  -H "Content-Type: application/json" \
  -d '{
    "danaInvestasi": 500000,
    "investmentTracking": {
      "cycleAmounts": {
        "2026-06-25": 3000000
      }
    }
  }'
```

### PATCH Financial Data (Admin Backfill)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/financial-data \
  -H "Content-Type: application/json" \
  -d '{
    "savingsAllocation": {
      "tabungan": 5000000,
      "danaDarurat": 2000000,
      "danaInvestasi": 1500000
    }
  }'
```

### PATCH Investment Watchlist (Update selectedSymbol saja)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/investment-watchlist \
  -H "Content-Type: application/json" \
  -d '{
    "selectedSymbol": "BBRI.JK"
  }'
```

### PATCH Investment Watchlist (Replace items + selectedSymbol)
```bash
curl -X PATCH http://localhost:8081/api/users/user-001/investment-watchlist \
  -H "Content-Type: application/json" \
  -d '{
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
    "updatedAt": "2026-04-25T10:00:00Z"
  }'
```

### Get User (dengan FinancialData)
```bash
curl -X GET http://localhost:8081/api/users/user-001 \
  -H "Content-Type: application/json"
```

---

## Summary: Workflow Rekomendasi

### 1. Setup awal (Admin/Create User)
```
POST /api/users ← Kirim UserPayload lengkap dengan financialData
```

### 2. Daily: User top-up/withdraw
```
PATCH /api/users/{id}/financial-data
├─ Update saldo: estimasiTabungan, danaDarurat, danaInvestasi
└─ Update akumulasi: savingsAllocationDelta (tambah/kurang)
```

### 3. Monthly: Investment cycle entry
```
PATCH /api/users/{id}/financial-data
├─ Update saldo: danaInvestasi
└─ Update tracking: investmentTracking.cycleAmounts
```

### 4. Quarterly/Yearly: Data sync/backfill
```
PATCH /api/users/{id}/financial-data
├─ Koreksi akumulasi: savingsAllocation (absolut, bukan delta)
└─ Koreksi saldo: estimasiTabungan, danaDarurat, danaInvestasi
```

### 5. Reporting/Audit
```
GET /api/users/{id}
├─ Cek saldo saat ini (snapshot): estimasiTabungan, danaDarurat, danaInvestasi
├─ Cek akumulasi (history): savingsAllocation
└─ Cek timeline: investmentTracking.cycleAmounts
```

---

## Catatan Database Schema

**Tabel `financial_data`:**
```
Kolom saldo (nullable=true untuk backward compat):
- dana_darurat
- dana_investasi
- estimasi_tabungan

Kolom akumulasi (dengan prefix savings_allocation_):
- savings_allocation_tabungan
- savings_allocation_dana_darurat
- savings_allocation_dana_investasi

Kolom lainnya:
- pendapatan
- pengeluaran_wajib
- ... (existing columns)
```

**Tabel `investment_tracking_cycle_amounts` (ElementCollection):**
```
Columns:
- financial_data_id (FK ke financial_data)
- cycle_key (map key, e.g., "2026-03-25")
- amount (map value, e.g., 8400000)
```

---

## Troubleshooting

**Q: Saya PATCH dengan `savingsAllocationDelta` tapi tidak ada perubahan?**
A: Pastikan field ada di JSON, dan field-nya nullable di database. Reset DB jika ada schema conflict.

**Q: Kapan pakai delta vs absolut?**
A: 
- **Delta** → user action (top-up, withdraw) = normal flow
- **Absolut** → admin action (koreksi, sync) = exceptional flow

**Q: Bagaimana kalau ingin update multiple field sekaligus?**
A: Bisa dalam 1 PATCH request. Contoh:
```json
{
  "pendapatan": 11000000,
  "estimasiTabungan": 42200000,
  "danaInvestasi": 600000,
  "savingsAllocationDelta": {
    "tabungan": 1500000,
    "danaInvestasi": 500000
  },
  "investmentTracking": {
    "cycleAmounts": {
      "2026-07-25": 3500000
    }
  }
}
```
---

## Daftar Lengkap Endpoint

| Method | Endpoint |
|--------|----------|
| `POST` | `http://localhost:8081/api/users` |
| `GET` | `http://localhost:8081/api/users` |
| `GET` | `http://localhost:8081/api/users/{id}` |
| `PUT` | `http://localhost:8081/api/users/{id}` |
| `DELETE` | `http://localhost:8081/api/users/{id}` |
| `PATCH` | `http://localhost:8081/api/users/{id}/financial-data` |
| `PATCH` | `http://localhost:8081/api/users/{id}/investment-watchlist` |
| `GET` | `http://localhost:8081/api/users/{id}/debts` |
| `POST` | `http://localhost:8081/api/users/{id}/debts` |
| `GET` | `http://localhost:8081/api/users/{id}/journal/chats?date=yyyy-MM-dd` |
| `POST` | `http://localhost:8081/api/users/{id}/journal/chats?date=yyyy-MM-dd` |
| `GET` | `http://localhost:8081/api/users/{id}/journal/expenses?date=yyyy-MM-dd` |
| `POST` | `http://localhost:8081/api/users/{id}/journal/expenses?date=yyyy-MM-dd` |
| `GET` | `http://localhost:8081/api/users/{id}/journal/incomes?date=yyyy-MM-dd` |
| `POST` | `http://localhost:8081/api/users/{id}/journal/incomes?date=yyyy-MM-dd` |

