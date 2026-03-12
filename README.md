#  Smart Electricity Meter Monitoring System (SEMMS)

A production-grade **Spring Boot** backend for real-time electricity monitoring using IoT smart meters. The system collects meter telemetry, processes consumption data, generates intelligent alerts, streams live updates to dashboards via WebSocket, and automates billing on 15-day or monthly cycles.

---

##  System Architecture

```
IoT Layer        → Smart meters transmit via LoRaWAN gateways
Ingestion Layer  → REST APIs receive voltage, current, pulse count, timestamp
Processing Layer → Pulse conversion, anomaly detection, alert generation
Billing Layer    → Automated bill generation on 15-day or monthly cycles
Real-Time Layer  → WebSocket broadcasts live updates to dashboards
Persistence Layer→ PostgreSQL/MySQL stores all readings, bills, and alerts
```

---

##  Core Features

-  **Live Monitoring** — Real-time voltage, current, and power tracking
-  **Pulse to kWh Conversion** — 1000 pulses = 1 electricity unit (kWh)
-  **Intelligent Alerts** — Tamper detection, anomaly spikes, device offline detection
-  **Automated Billing** — Bill generation on 15-day or monthly cycles with slab-based tariff
-  **Consumer Dashboard** — Usage history, daily/monthly consumption, bill estimation
-  **Admin Dashboard** — Area-wise usage, meter health status, grid analytics
-  **Heartbeat Monitoring** — Meters inactive for 10+ minutes are auto-marked OFFLINE

---

##  Technology Stack

| Category | Technology |
|---|---|
| Backend | Spring Boot 3 |
| Language | Java 17 |
| Security | Spring Security + JWT |
| Database | PostgreSQL / MySQL |
| Real-time | WebSocket + STOMP |
| API Docs | Swagger / OpenAPI |
| ORM | JPA / Hibernate |
| Build Tool | Maven |

---

##  Project Structure

```
com.gescom.smartmeter
├── config/
│   ├── SecurityConfig
│   ├── WebSocketConfig
│   └── AppConfig
├── controller/
│   ├── AuthController
│   ├── MeterController
│   ├── ConsumerController
│   ├── AdminController
│   └── BillingController
├── service/
│   ├── MeterService
│   ├── PulseConversionService
│   ├── AlertService
│   ├── AnalyticsService
│   └── BillingService
├── repository/
│   ├── UserRepository
│   ├── MeterRepository
│   ├── MeterReadingRepository
│   ├── AlertRepository
│   └── BillRepository
├── entity/
│   ├── User
│   ├── Meter
│   ├── MeterReading
│   ├── Alert
│   └── Bill
├── dto/
│   ├── MeterDataRequest
│   ├── AuthRequest
│   ├── AuthResponse
│   └── BillResponse
└── websocket/
    ├── MeterDataPublisher
    └── AlertPublisher
```

---

## Database Schema

### User
| Field | Type |
|---|---|
| id | Long |
| name | String |
| email | String |
| password | String |
| role | `ADMIN` / `CONSUMER` |

### Meter
| Field | Type |
|---|---|
| id | Long |
| meterId | String |
| location | String |
| consumerId | Long |
| status | `ONLINE` / `OFFLINE` |
| billingCycle | `MONTHLY` / `FIFTEEN_DAYS` |

### MeterReading
| Field | Type |
|---|---|
| id | Long |
| meterId | String |
| pulseCount | Integer |
| voltage | Double |
| current | Double |
| unitsConsumed | Double |
| timestamp | DateTime |

### Alert
| Field | Type |
|---|---|
| id | Long |
| meterId | String |
| alertType | String |
| message | String |
| createdAt | DateTime |

### Bill
| Field | Type |
|---|---|
| id | Long |
| meterId | String |
| consumerId | Long |
| billingCycle | `MONTHLY` / `FIFTEEN_DAYS` |
| cycleStartDate | DateTime |
| cycleEndDate | DateTime |
| openingReading | Double |
| closingReading | Double |
| unitsConsumed | Double |
| baseAmount | Double |
| fixedCharges | Double |
| taxAmount | Double |
| totalAmount | Double |
| status | `UNPAID` / `PAID` / `OVERDUE` |
| generatedAt | DateTime |
| dueDate | DateTime |

---

##  Billing System

The billing engine automatically generates bills based on the configured cycle for each meter — either **every 15 days** or **monthly**.

---

### Billing Cycle Options

| Cycle | Description |
|---|---|
| `MONTHLY` | Bill generated on the 1st of every month |
| `FIFTEEN_DAYS` | Bill generated on the 1st and 16th of every month |

Each meter can be individually configured with its billing cycle via the Admin API.

---

### How Billing Works

```
1. Scheduled job triggers at midnight on billing dates
2. Reads opening meter value (last cycle's closing reading)
3. Reads closing meter value (current reading at cycle end)
4. Calculates units consumed = closing reading - opening reading
5. Applies slab-based tariff to compute base amount
6. Adds fixed charges + tax
7. Saves Bill record to database
8. Pushes bill notification to consumer dashboard via WebSocket
```

---

### Slab-Based Tariff Structure

Bills are calculated using a tiered slab system:

| Units Consumed (kWh) | Rate per Unit |
|---|---|
| 0 – 100 units | ₹3.50 / unit |
| 101 – 300 units | ₹5.00 / unit |
| 301 – 500 units | ₹6.50 / unit |
| Above 500 units | ₹8.00 / unit |

> Tariff rates are fully configurable via `application.properties`.
> For 15-day billing, slabs are applied proportionally to half-month consumption.

---

### Bill Amount Calculation

```
Base Amount   = Units consumed × applicable slab rate (tiered)
Fixed Charges = ₹50.00 (per billing cycle, configurable)
Tax           = 5% of Base Amount
─────────────────────────────────────────────────────────────
Total Amount  = Base Amount + Fixed Charges + Tax
```

**Example — Monthly Bill (320 units consumed):**

```
First 100 units  →  100 × ₹3.50  =  ₹350.00
Next  200 units  →  200 × ₹5.00  =  ₹1000.00
Next   20 units  →   20 × ₹6.50  =  ₹130.00
                                   ──────────
Base Amount                        ₹1480.00
Fixed Charges                      ₹50.00
Tax (5%)                           ₹74.00
                                   ──────────
Total Bill                         ₹1604.00
```

**Example — 15-Day Bill (140 units consumed):**

```
First 100 units  →  100 × ₹3.50  =  ₹350.00
Next   40 units  →   40 × ₹5.00  =  ₹200.00
                                   ──────────
Base Amount                        ₹550.00
Fixed Charges                      ₹50.00
Tax (5%)                           ₹27.50
                                   ──────────
Total Bill                         ₹627.50
```

---

### Billing Configuration

```properties
# Billing Settings — application.properties

billing.cycle.default=MONTHLY           # MONTHLY or FIFTEEN_DAYS
billing.fixed.charge=50.00
billing.tax.percentage=5
billing.due.days=15                     # Days after generation until due date

# Slab Rates (₹ per unit)
billing.slab1.limit=100
billing.slab1.rate=3.50

billing.slab2.limit=300
billing.slab2.rate=5.00

billing.slab3.limit=500
billing.slab3.rate=6.50

billing.slab4.rate=8.00
```

---

## 🔌 REST API Reference

### Authentication

```http
POST /api/auth/login
```

```json
// Request
{ "email": "admin@example.com", "password": "password" }

// Response
{ "token": "JWT_TOKEN" }
```

---

### IoT Data Ingestion

```http
POST /api/meter-data
```

```json
{
  "meterId": "MTR-1002",
  "pulseCount": 50,
  "voltage": 230.5,
  "current": 4.2,
  "timestamp": "2026-03-12T11:22:50Z"
}
```

**Processing pipeline:**
1. Validate meter ID
2. Convert pulses to electricity units
3. Store reading in database
4. Broadcast via WebSocket
5. Trigger alert if anomaly detected

---

### Consumer Endpoints

```http
GET /api/consumer/meter               # Meter details
GET /api/consumer/usage               # Consumption history
GET /api/consumer/bill-estimate       # Real-time bill estimate for current cycle
GET /api/consumer/bills               # All past bills
GET /api/consumer/bills/{billId}      # Specific bill details
```

---

### Billing Endpoints

```http
GET  /api/billing/current/{meterId}      # Current cycle bill estimate
GET  /api/billing/history/{meterId}      # Full billing history for a meter
POST /api/billing/generate/{meterId}     # Manually trigger bill generation (Admin only)
PUT  /api/billing/{billId}/pay           # Mark a bill as paid
GET  /api/billing/overdue                # List all overdue bills (Admin only)
PUT  /api/admin/meter/{meterId}/cycle    # Update billing cycle for a meter (Admin only)
```

**Bill Estimate Response Example:**

```json
{
  "meterId": "MTR-1002",
  "billingCycle": "MONTHLY",
  "cycleStart": "2026-03-01",
  "cycleEnd": "2026-03-31",
  "openingReading": 1450.00,
  "currentReading": 1660.50,
  "unitsConsumedSoFar": 210.50,
  "estimatedUnitsTotal": 320.00,
  "baseAmount": 1480.00,
  "fixedCharges": 50.00,
  "taxAmount": 74.00,
  "estimatedTotalAmount": 1604.00,
  "dueDate": "2026-04-15",
  "status": "UNPAID"
}
```

---

### Admin Endpoints

```http
GET /api/admin/meters             # All meters
GET /api/admin/alerts             # All alerts
GET /api/admin/analytics          # Consumption analytics
GET /api/admin/billing/summary    # Billing summary across all consumers
```

---

##  WebSocket Topics

**Endpoint:** `ws://localhost:8080/ws`
**Protocol:** STOMP

| Topic | Purpose |
|---|---|
| `/topic/meter-data` | Live meter readings |
| `/topic/alerts` | Alert notifications |
| `/topic/device-status` | Online/offline updates |
| `/topic/billing` | New bill generated notifications |

---

##  Security

- **JWT-based stateless authentication**
- **Role-based access control** (`ADMIN`, `CONSUMER`)
- Secure REST endpoints with token authorization

---

##  Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/your-username/smart-meter-monitoring.git
cd smart-meter-monitoring
```

### 2. Configure the database
Update `src/main/resources/application.properties` with your DB credentials.

### 3. Build the project
```bash
./mvnw clean install
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

### 5. View API documentation
```
http://localhost:8080/swagger-ui.html
```

---

##  Roadmap

- [ ] AI-based electricity theft detection
- [ ] Smart grid analytics
- [ ] Mobile consumer application
- [ ] Predictive load forecasting
- [ ] Government energy dashboard integration
- [ ] Online bill payment gateway integration
- [ ] PDF bill generation and email delivery

---

##  License

MIT License © 2026 Smart Meter Monitoring Team
