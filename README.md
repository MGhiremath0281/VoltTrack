<div align="center">

<br/>

#  VoltTrack

### Real-Time Smart Electricity Meter Monitoring & Management System

*Bridging the gap between physical electricity infrastructure and digital intelligence*

<br/>

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![WebSocket](https://img.shields.io/badge/Realtime-WebSocket-8A2BE2?style=for-the-badge)](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

<br/>

</div>

---

## The Problem We're Solving

India's electricity distribution infrastructure faces a systemic crisis that technology has been slow to address. Utility companies operate across vast geographies with hundreds of thousands of consumers, yet most monitoring still depends on **manual meter reading** — a process that is slow, error-prone, and fundamentally incapable of detecting problems in real time.

The consequences are severe and well-documented:

**Electricity theft** costs Indian utilities an estimated ₹25,000–₹30,000 crore annually. Tampering with meters, bypassing connections, and meter manipulation go undetected for months because there is no real-time anomaly signal — only a mismatch noticed at the end of a billing cycle, by which point the damage is done.

**Billing disputes** are one of the most common consumer complaints filed with electricity regulators. Incorrect manual readings, estimated bills during missed readings, and delayed billing erode consumer trust and overload grievance systems.

**Grid inefficiency** is invisible without live data. Distribution officers managing a zone have no way to know which areas are under load stress, which meters have gone offline, or where consumption patterns are spiking — not until a fault occurs.

**Operational blindness** at the administrative level means decisions about infrastructure investment, load balancing, and tariff planning are made on stale, aggregated data rather than live intelligence.

VoltTrack was built to solve all of this — not as a prototype, but as a production-grade system that mirrors how a real electricity distribution hierarchy actually works.

---

## What VoltTrack Does

VoltTrack is a full-stack IoT monitoring and management platform that connects physical smart meters on consumer premises to a layered administrative system — giving every stakeholder in the electricity distribution chain exactly the information and control they need, in real time.

At its core, VoltTrack does five things:

**Continuous monitoring.** Every smart meter transmits voltage, current, and pulse count data through LoRaWAN gateways to the VoltTrack backend every few minutes. This data is processed, stored, and streamed live to dashboards. Meters that go silent for more than 10 minutes are automatically flagged `OFFLINE` — whether due to power failure, tampering, or connectivity loss.

**Intelligent alerting.** The system continuously evaluates incoming readings against expected baselines. Sudden current spikes, voltage anomalies, or unusual consumption patterns trigger alerts that are routed to the responsible field officer instantly via WebSocket. Tamper events are flagged before the billing cycle closes.

**Automated, accurate billing.** Bills are generated automatically on configurable cycles — monthly or every 15 days — using a tiered slab tariff. The engine reads opening and closing meter values for the cycle, applies the tariff structure, adds fixed charges and tax, and delivers a bill to the consumer's dashboard. No manual reading, no estimation.

**Hierarchical role management.** VoltTrack is not a flat system with just admins and consumers. It models the actual structure of a distribution utility: field officers manage their assigned consumers, sub-divisional officers oversee entire zones, and administrators control the full system.

**Live operational intelligence.** Every layer of the hierarchy has dashboards showing what is happening right now — consumption trends, meter health, billing status, alert queues — not a report from last week.

---

## How the Physical Infrastructure Works

Understanding VoltTrack requires understanding the hardware it connects to.

### The Smart Meter

<br/>

<div align="center">
<img src="https://github.com/MGhiremath0281/VoltTrack/blob/main/frontend/src/assets/meter.jpeg?raw=true" width="420" alt="Smart Electricity Meter"/>
</div>

<br/>

A smart meter is an advanced electricity meter installed at a consumer's premises that replaces the traditional electromechanical meter. Unlike its predecessor, a smart meter does not just accumulate a reading — it actively measures and records **voltage (V)**, **current (A)**, and **pulse count** at regular intervals and transmits this data wirelessly.

The pulse-to-unit conversion is standardised: **1,000 pulses = 1 kWh of electricity consumed**. This allows the backend to compute consumption with high precision from raw hardware signals.

Crucially, smart meters create a two-way communication channel. The meter doesn't just send data — the backend can detect when a meter stops communicating, which itself is a signal (offline meter, tamper event, power outage). This passive monitoring capability is something manual reading can never provide.

### The LoRaWAN Gateway

<br/>

<div align="center">
<img src="https://github.com/MGhiremath0281/VoltTrack/blob/main/frontend/src/assets/gateway.jpeg?raw=true" width="420" alt="LoRaWAN Gateway"/>
</div>

<br/>

LoRaWAN (Long Range Wide Area Network) is a low-power wireless protocol designed specifically for IoT deployments in urban and semi-urban environments. A single gateway placed on a rooftop or utility pole can receive transmissions from **hundreds of smart meters** within a radius of several kilometres — without requiring Wi-Fi, cellular SIM cards, or wired connections at each meter.

Gateways act as the field-to-cloud bridge. They receive radio packets from meters, package them, and forward them to the VoltTrack backend over a standard internet connection. From the backend's perspective, data arrives as structured REST payloads carrying meter ID, readings, and a hardware timestamp.

This architecture makes VoltTrack deployable in areas with inconsistent connectivity infrastructure — the meters themselves need no internet access at all.

---

## System Architecture

VoltTrack is built as a layered backend with clear separation between data ingestion, processing, persistence, and delivery.

```
┌─────────────────────────────────────────────────────────────────┐
│                        PHYSICAL LAYER                           │
│         Smart Meters  ──[LoRaWAN]──▶  Field Gateways            │
└──────────────────────────────┬──────────────────────────────────┘
                               │  HTTP / REST
┌──────────────────────────────▼──────────────────────────────────┐
│                      INGESTION LAYER                            │
│          POST /api/meter-data  ←  Raw telemetry payloads         │
│          Validation → Pulse Conversion → Anomaly Check           │
└──────────┬─────────────────────────────────┬────────────────────┘
           │                                 │
┌──────────▼──────────┐           ┌──────────▼──────────────────┐
│   PERSISTENCE LAYER │           │     PROCESSING LAYER         │
│   PostgreSQL / MySQL│           │   Alert Engine               │
│   All readings,     │           │   Billing Scheduler          │
│   bills, alerts     │           │   Heartbeat Monitor          │
└─────────────────────┘           └──────────┬───────────────────┘
                                             │
                                  ┌──────────▼───────────────────┐
                                  │      DELIVERY LAYER           │
                                  │  REST APIs  +  WebSocket      │
                                  │  Role-based response shaping  │
                                  └──────────┬────────────────────┘
                                             │
           ┌─────────────────────────────────┼──────────────────────┐
           ▼                                 ▼                      ▼
   Consumer Dashboard             Officer / Sub-Div Portal    Admin Console
   (own bills, usage)             (zone analytics, alerts)    (full system)
```

---

## The Four-Tier Role Hierarchy

This is the heart of VoltTrack's design philosophy. Most IoT monitoring tools treat access control as an afterthought — a binary of admin or user. VoltTrack was designed to mirror the actual organisational hierarchy of an electricity distribution company.

```
                          ┌─────────────┐
                          │    ADMIN    │  Full system control
                          └──────┬──────┘
                                 │  oversees
                    ┌────────────▼────────────┐
                    │   SUB_DIV_OFFICER        │  Sub-division level
                    └────────────┬────────────┘
                                 │  manages
                         ┌───────▼────────┐
                         │    OFFICER     │  Field / zone level
                         └───────┬────────┘
                                 │  serves
                          ┌──────▼───────┐
                          │   CONSUMER   │  End user
                          └─────────────┘
```

### CONSUMER
The electricity subscriber at the end of the chain. A consumer can see their own meter's live readings, track daily and monthly consumption, view their current billing cycle's running estimate, access past bills, and receive instant WebSocket notifications when a new bill is generated. They have zero visibility into any other consumer's data.

### OFFICER (Field Officer)
A field officer is responsible for a defined geographic zone containing multiple consumers. They are the first line of operational response. Through VoltTrack, an officer can monitor the real-time health of every meter in their jurisdiction, receive instant alerts when anomalies are detected, view area-level consumption summaries, and manage the consumers assigned to them. When a meter goes offline or a tamper alert fires, it lands in the officer's alert queue first.

### SUB_DIV_OFFICER (Sub-Divisional Officer)
The sub-divisional officer sits above the field officers and oversees an entire sub-division — a cluster of zones managed by multiple officers. Their view is strategic rather than operational: aggregated analytics across the sub-division, billing summaries, overdue account trends, meter health at scale, and officer-level performance breakdowns. They do not manage individual consumers directly but have full visibility into everything within their sub-division.

### ADMIN
The system administrator has unrestricted access to the entire platform. Only the admin can create and deactivate users and meters, assign consumers to officers, configure billing cycles per meter, manually trigger bill generation, modify tariff settings, and access system-wide grid analytics. The admin is responsible for the structural integrity of the platform.

---

## Core Capabilities

### Real-Time Telemetry Processing

Every reading that arrives from a gateway goes through a synchronous processing pipeline before a response is returned:

1. The meter ID is validated against the registered meter registry
2. Raw pulse count is converted to kWh units (`pulses ÷ 1000`)
3. The reading is persisted to the database with a timestamp
4. The processed data is broadcast to subscribed WebSocket clients
5. The alert engine evaluates the reading for anomalies — voltage outside tolerance, current spike beyond baseline, or a pattern consistent with tamper activity

This pipeline runs on every single ingestion request, giving the system a sub-second path from meter transmission to dashboard update.

### Heartbeat & Offline Detection

Meters in the field don't always fail loudly. A tampered meter, a communication fault, or a power outage may simply cause a meter to stop transmitting. VoltTrack's heartbeat monitor runs as a scheduled background job and evaluates the last-seen timestamp for every registered meter. Any meter that has not transmitted for **10 minutes or more** is automatically marked `OFFLINE` and a status change event is broadcast to the relevant officer's dashboard.

This passive detection capability is one of the most operationally valuable features in the system — it catches problems that would otherwise go unnoticed until the next manual inspection.

### Automated Slab-Based Billing

VoltTrack's billing engine implements India's standard tiered electricity tariff structure. Bills are not estimated — they are calculated precisely from the opening and closing meter readings at the end of each billing cycle.

**Billing cycles** are configurable per meter:
- `MONTHLY` — bill generated on the 1st of every month
- `FIFTEEN_DAYS` — bill generated on the 1st and 16th of every month

**Tariff slabs** follow a progressive structure that charges higher rates as consumption increases:

| Consumption | Rate |
|---|---|
| First 100 units | ₹3.50 / kWh |
| 101 – 300 units | ₹5.00 / kWh |
| 301 – 500 units | ₹6.50 / kWh |
| Above 500 units | ₹8.00 / kWh |

**Bill calculation example — 320 units consumed:**
```
First 100 units  →  100 × ₹3.50  =   ₹350.00
Next  200 units  →  200 × ₹5.00  = ₹1,000.00
Next   20 units  →   20 × ₹6.50  =   ₹130.00
                                    ─────────
Base Amount                         ₹1,480.00
Fixed Charge (per cycle)               ₹50.00
Tax @ 5%                               ₹74.00
                                    ─────────
Total Bill                          ₹1,604.00
```

The moment a bill is generated, it is pushed to the consumer's dashboard via WebSocket — no polling, no delay.

### Alert System

VoltTrack monitors for three categories of alert conditions:

**Tamper alerts** fire when the system detects reading patterns inconsistent with normal consumption — sudden zero readings after normal activity, implausible consumption drops, or reverse-flow indicators.

**Anomaly alerts** fire when voltage or current readings fall outside configurable thresholds for the meter's assigned zone. A voltage reading of 180V in a 230V supply area, or a current spike three times above the meter's baseline, are examples of triggering conditions.

**Offline alerts** fire from the heartbeat monitor when a meter stops transmitting. These are routed to the officer responsible for that meter's zone.

All alerts are stored persistently and broadcast in real time via WebSocket to the appropriate role level.

---

## Live Data Streaming — WebSocket Architecture

VoltTrack uses **STOMP over WebSocket** to push data to clients without polling. Each role subscribes to the topics relevant to their responsibilities.

**Endpoint:** `ws://localhost:8080/ws`

| Topic | Audience | Payload |
|---|---|---|
| `/topic/meter-data` | Officers, Admins | Live voltage, current, kWh reading per meter |
| `/topic/alerts` | Officers, Sub-Div Officers, Admins | Alert type, meter ID, message, severity |
| `/topic/device-status` | All operational roles | Meter online/offline status changes |
| `/topic/billing` | Consumers | New bill generated notification with amount |

A consumer viewing their dashboard sees their bill the moment it is generated. An officer sees a tamper alert the moment the engine flags it. Nothing waits for a page refresh.

---

## API Overview

### Authentication
```http
POST /api/auth/login
```
Returns a JWT token. All subsequent requests must carry this token in the `Authorization: Bearer <token>` header. Tokens encode the user's role, which the backend uses to enforce access boundaries on every endpoint.

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
This endpoint is called by gateways, not by human users. It is the entry point for all telemetry data and triggers the full processing pipeline described above.

### Consumer APIs
```http
GET /api/consumer/meter             # Live meter details and status
GET /api/consumer/usage             # Historical consumption by day/month
GET /api/consumer/bill-estimate     # Running estimate for the current billing cycle
GET /api/consumer/bills             # Full billing history
GET /api/consumer/bills/{id}        # Detailed breakdown of a specific bill
```

### Officer APIs
```http
GET /api/officer/consumers          # All consumers in the officer's zone
GET /api/officer/meters             # Live status of all meters in jurisdiction
GET /api/officer/alerts             # Active and historical alerts for the zone
GET /api/officer/usage-summary      # Aggregated consumption for the officer's area
```

### Sub-Divisional Officer APIs
```http
GET /api/subdiv/analytics           # Sub-division-wide consumption analytics
GET /api/subdiv/officers            # Officers and their zone summaries
GET /api/subdiv/billing/summary     # Billing status across all consumers in sub-division
GET /api/subdiv/overdue             # All overdue accounts within the sub-division
GET /api/subdiv/meter-health        # Aggregated meter health and offline status
```

### Admin APIs
```http
GET  /api/admin/meters                   # All registered meters
GET  /api/admin/users                    # All users with role management
GET  /api/admin/alerts                   # System-wide active alerts
GET  /api/admin/analytics                # Global grid analytics
GET  /api/admin/billing/summary          # Cross-system billing overview
POST /api/billing/generate/{meterId}     # Manually trigger bill generation
PUT  /api/billing/{billId}/pay           # Mark a bill as paid
GET  /api/billing/overdue                # All overdue bills system-wide
PUT  /api/admin/meter/{meterId}/cycle    # Update billing cycle for a meter
```

---

## Security Model

VoltTrack uses **stateless JWT authentication** combined with **role-based access control** enforced at every layer.

When a user logs in, the backend issues a signed JWT encoding their identity and role. Every request to a protected endpoint is validated against this token. The role encoded in the token determines not just which endpoints are accessible, but how data is shaped in responses — a consumer calling a shared endpoint receives only their own data, while an officer receives data scoped to their assigned zone.

This means access control is not just at the route level — it is enforced within service logic, ensuring that even if a route is inadvertently exposed, the data returned cannot exceed what the authenticated user's role permits.

---

## Technology Stack

| Layer | Technology | Rationale |
|---|---|---|
| Backend Framework | Spring Boot 3 | Production-grade Java framework with mature ecosystem for REST, scheduling, and security |
| Language | Java 17 | LTS release with modern language features; strong typing critical for financial calculations |
| Authentication | Spring Security + JWT | Stateless auth scales horizontally without session state |
| Database | PostgreSQL / MySQL | ACID-compliant relational DB; billing and meter data require transactional integrity |
| ORM | JPA / Hibernate | Type-safe database access with migration support |
| Real-time | WebSocket + STOMP | Push-based updates; eliminates polling overhead for dashboard clients |
| IoT Transport | LoRaWAN | Low-power, long-range radio protocol designed for dense IoT deployments |
| API Documentation | Swagger / OpenAPI | Auto-generated, always-current API reference |
| Build | Maven | Dependency management and reproducible builds |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL or MySQL running locally

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/MGhiremath0281/VoltTrack.git
cd VoltTrack

# 2. Configure your database
# Edit: volttrack/src/main/resources/application.properties

# 3. Build
cd volttrack
./mvnw clean install

# 4. Run
./mvnw spring-boot:run

# 5. API docs available at:
# http://localhost:8080/swagger-ui.html
```

### Billing Configuration

All billing parameters are externalized and configurable without code changes:

```properties
billing.cycle.default=MONTHLY
billing.fixed.charge=50.00
billing.tax.percentage=5
billing.due.days=15

billing.slab1.limit=100
billing.slab1.rate=3.50
billing.slab2.limit=300
billing.slab2.rate=5.00
billing.slab3.limit=500
billing.slab3.rate=6.50
billing.slab4.rate=8.00
```

---

## What's Next

VoltTrack's current architecture is designed to support the following capabilities as the system matures:

**AI-based theft detection** — training a model on historical reading patterns to flag statistical anomalies that rule-based alerting misses, particularly for sophisticated meter tampering.

**Predictive load forecasting** — using historical consumption data to predict zone-level load demand, enabling proactive grid management rather than reactive fault response.

**PDF bill generation with email delivery** — consumer-facing bills delivered automatically to registered email addresses at cycle end.

**Online payment gateway integration** — allowing consumers to pay bills directly through the platform.

**Mobile application for consumers and officers** — a native mobile interface for field officers receiving alerts on-site and consumers checking usage on the go.

**Government energy dashboard integration** — aggregated reporting for regulatory compliance and national grid data submissions.

---

## License

MIT License © 2026 VoltTrack — Muktananda Hiremath

---

<div align="center">

*Built for the electricity distribution ecosystem — from the meter on the wall to the dashboard in the office.*

</div>
