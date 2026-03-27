# 🏨 Hotel Management System — Microservices Architecture
### IT4020 Modern Topics in IT | Assignment 2 | SLIIT 2026

---

## 📐 Architecture Overview

```
Client / Postman / Browser
        │
        ▼
┌─────────────────────────────────────────────────────┐
│              API Gateway  :8086                     │
│  • Single entry point for ALL microservices         │
│  • JWT Authentication Filter                        │
│  • Aggregated Swagger UI  → /swagger-ui.html        │
│  • Route forwarding with RewritePath                │
└──────────────┬──────────────────────────────────────┘
               │  Routes via Spring Cloud Gateway
       ┌───────┼────────────────────────┐_____________
       │       │          │             │             │
       ▼       ▼          ▼             ▼             ▼
  Auth     Customer    Room         Booking       Payment
  :8081    :8082       :8083        :8084         :8085
   │         │           │             │             │
   ▼         ▼           ▼             ▼             ▼
hotel_    hotel_      hotel_       hotel_         hotel_
auth_db   customer_db room_db      booking_db     payment_db
```

---

## 🚀 Services

| Service          | Port | Database            | Description                      |
|------------------|------|---------------------|----------------------------------|
| API Gateway      | 8086 | —                   | Single entry point + JWT filter  |
| Auth Service     | 8081 | hotel_auth_db       | Register, Login, JWT validation  |
| Customer Service | 8082 | hotel_customer_db   | Customer CRUD                    |
| Room Service     | 8083 | hotel_room_db       | Room management                  |
| Booking Service  | 8084 | hotel_booking_db    | Room booking with availability   |
| Payment Service  | 8085 | hotel_payment_db    | Payment processing & refunds     |

---

## ⚙️ Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+ (running on localhost:3306)
- Username: `root` | Password: `Razh@3165`

---

## 🗄️ Database Setup

MySQL databases are created automatically via `createDatabaseIfNotExist=true`.

Or create them manually:
```sql
CREATE DATABASE IF NOT EXISTS hotel_auth_db;
CREATE DATABASE IF NOT EXISTS hotel_customer_db;
CREATE DATABASE IF NOT EXISTS hotel_room_db;
CREATE DATABASE IF NOT EXISTS hotel_booking_db;
CREATE DATABASE IF NOT EXISTS hotel_payment_db;
```

---

## ▶️ Running the Services

### Option A — Using the startup script (Linux/Mac)
```bash
chmod +x start-all.sh
./start-all.sh
```

### Option B — Manual (run each in its own terminal)

```bash
# Terminal 1 — Auth Service
cd auth-service && mvn spring-boot:run

# Terminal 2 — Customer Service
cd customer-service && mvn spring-boot:run

# Terminal 3 — Room Service
cd room-service && mvn spring-boot:run

# Terminal 4 — Booking Service
cd booking-service && mvn spring-boot:run

# Terminal 5 — Payment Service
cd payment-service && mvn spring-boot:run

# Terminal 6 — API Gateway (start LAST)
cd api-gateway && mvn spring-boot:run
```

---

## 📖 Swagger UI Endpoints

### Individual Service Swagger (native)
| Service      | Swagger URL                               |
|--------------|-------------------------------------------|
| Auth         | http://localhost:8081/swagger-ui.html     |
| Customer     | http://localhost:8082/swagger-ui.html     |
| Room         | http://localhost:8083/swagger-ui.html     |
| Booking      | http://localhost:8084/swagger-ui.html     |
| Payment      | http://localhost:8085/swagger-ui.html     |

### 🌐 Aggregated Swagger via API Gateway (ONE URL for ALL services)
```
http://localhost:8086/swagger-ui.html
```
Use the dropdown in the top-right to switch between services.

---

## 🔐 Authentication Flow

### 1. Register
```
POST http://localhost:8086/api/auth/register
{
  "username": "admin",
  "email": "admin@hotel.com",
  "password": "admin123",
  "role": "ROLE_ADMIN"
}
```

### 2. Login → get JWT token
```
POST http://localhost:8086/api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

### 3. Use token in all protected requests
```
Authorization: Bearer <your-jwt-token>
```

---

## 🔗 API Endpoints (via Gateway :8086)

### Auth Service
| Method | Endpoint                    | Auth |
|--------|-----------------------------|------|
| POST   | /api/auth/register          | ❌   |
| POST   | /api/auth/login             | ❌   |
| POST   | /api/auth/validate          | ❌   |

### Customer Service
| Method | Endpoint                    | Auth |
|--------|-----------------------------|------|
| POST   | /api/customers              | ✅   |
| GET    | /api/customers              | ✅   |
| GET    | /api/customers/{id}         | ✅   |
| GET    | /api/customers/email/{email}| ✅   |
| PUT    | /api/customers/{id}         | ✅   |
| DELETE | /api/customers/{id}         | ✅   |

### Room Service
| Method | Endpoint                    | Auth |
|--------|-----------------------------|------|
| POST   | /api/rooms                  | ✅   |
| GET    | /api/rooms                  | ✅   |
| GET    | /api/rooms/{id}             | ✅   |
| GET    | /api/rooms/available        | ✅   |
| GET    | /api/rooms/type/{roomType}  | ✅   |
| PUT    | /api/rooms/{id}             | ✅   |
| PATCH  | /api/rooms/{id}/status      | ✅   |
| DELETE | /api/rooms/{id}             | ✅   |

### Booking Service
| Method | Endpoint                            | Auth |
|--------|-------------------------------------|------|
| POST   | /api/bookings                       | ✅   |
| GET    | /api/bookings                       | ✅   |
| GET    | /api/bookings/{id}                  | ✅   |
| GET    | /api/bookings/reference/{ref}       | ✅   |
| GET    | /api/bookings/customer/{customerId} | ✅   |
| PUT    | /api/bookings/{id}                  | ✅   |
| DELETE | /api/bookings/{id}/cancel           | ✅   |

### Payment Service
| Method | Endpoint                              | Auth |
|--------|---------------------------------------|------|
| POST   | /api/payments                         | ✅   |
| GET    | /api/payments                         | ✅   |
| GET    | /api/payments/{id}                    | ✅   |
| GET    | /api/payments/reference/{ref}         | ✅   |
| GET    | /api/payments/booking/{bookingId}     | ✅   |
| GET    | /api/payments/customer/{customerId}   | ✅   |
| POST   | /api/payments/{id}/refund             | ✅   |

---

## 🔄 Service Communication (Inter-service)

```
Booking Service ──REST──▶ Room Service     (check availability, update status)
Booking Service ──REST──▶ Customer Service (validate customer exists)
Payment Service ──REST──▶ Booking Service  (fetch booking amount)
```

---

## 📁 Project Folder Structure

```
hotel-management/
├── api-gateway/
│   └── src/main/java/com/hotel/gateway/
│       ├── ApiGatewayApplication.java
│       ├── config/
│       │   ├── JwtUtil.java
│       │   ├── RouteValidator.java
│       │   └── FallbackController.java
│       └── filter/
│           └── AuthenticationFilter.java
├── auth-service/
├── customer-service/
├── room-service/
├── booking-service/
├── payment-service/
├── start-all.sh
└── README.md
```

---

## 🧪 Step-by-Step API Testing Guide (Postman / curl)

> **Base URL via Gateway**: `http://localhost:8086`
> All protected endpoints require: `Authorization: Bearer <your-jwt-token>`

---

### 📌 STEP 1 — Register an Admin User

```
POST http://localhost:8086/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "email": "admin@hotel.com",
  "password": "admin123",
  "role": "ROLE_ADMIN"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@hotel.com",
  "role": "ROLE_ADMIN",
  "message": "User registered successfully"
}
```

---

### 📌 STEP 2 — Login & Get JWT Token

```
POST http://localhost:8086/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@hotel.com",
  "role": "ROLE_ADMIN",
  "message": "Login successful"
}
```

> ⚠️ **Copy the `token` value!** You need it for ALL the steps below.
> In Postman: Go to **Authorization** tab → Type: **Bearer Token** → Paste token.

---

### 📌 STEP 3 — Validate Token (Optional)

```
POST http://localhost:8086/api/auth/validate
Content-Type: application/json

{
  "token": "<paste-your-jwt-token>"
}
```

**Expected Response (200 OK):**
```json
{
  "valid": true,
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

---

### 📌 STEP 4 — Create a Customer

```
POST http://localhost:8086/api/customers
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+94771234567",
  "address": "123 Main Street",
  "city": "Colombo",
  "country": "Sri Lanka",
  "nationalId": "200012345678"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+94771234567",
  "address": "123 Main Street",
  "city": "Colombo",
  "country": "Sri Lanka",
  "nationalId": "200012345678",
  "status": "ACTIVE",
  "createdAt": "2026-03-27T12:00:00",
  "updatedAt": "2026-03-27T12:00:00"
}
```

> 📝 Note the `id` (e.g., `1`) — you will need it for booking.

---

### 📌 STEP 5 — Get All Customers

```
GET http://localhost:8086/api/customers
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 6 — Get Customer by ID

```
GET http://localhost:8086/api/customers/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 7 — Get Customer by Email

```
GET http://localhost:8086/api/customers/email/john.doe@example.com
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 8 — Update Customer

```
PUT http://localhost:8086/api/customers/1
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+94779999999",
  "address": "456 New Street",
  "city": "Kandy",
  "country": "Sri Lanka",
  "status": "ACTIVE"
}
```

---

### 📌 STEP 9 — Create a Room

```
POST http://localhost:8086/api/rooms
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "roomNumber": "101",
  "roomType": "DELUXE",
  "pricePerNight": 150.00,
  "capacity": 2,
  "description": "Deluxe room with ocean view",
  "amenities": "WiFi, AC, Mini Bar, TV",
  "floorNumber": 1
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "roomNumber": "101",
  "roomType": "DELUXE",
  "pricePerNight": 150.00,
  "capacity": 2,
  "description": "Deluxe room with ocean view",
  "amenities": "WiFi, AC, Mini Bar, TV",
  "floorNumber": 1,
  "status": "AVAILABLE",
  "createdAt": "2026-03-27T12:00:00",
  "updatedAt": "2026-03-27T12:00:00"
}
```

> 📝 Note the room `id` (e.g., `1`) — you will need it for booking.
> **Room Types**: `SINGLE`, `DOUBLE`, `SUITE`, `DELUXE`, `PRESIDENTIAL`

---

### 📌 STEP 10 — Create More Rooms (Optional)

```
POST http://localhost:8086/api/rooms
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "roomNumber": "201",
  "roomType": "SUITE",
  "pricePerNight": 300.00,
  "capacity": 4,
  "description": "Presidential suite with balcony",
  "amenities": "WiFi, AC, Jacuzzi, Mini Bar, TV, Room Service",
  "floorNumber": 2
}
```

---

### 📌 STEP 11 — Get All Rooms

```
GET http://localhost:8086/api/rooms
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 12 — Get Available Rooms

```
GET http://localhost:8086/api/rooms/available
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 13 — Get Rooms by Type

```
GET http://localhost:8086/api/rooms/type/DELUXE
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 14 — Get Room by ID

```
GET http://localhost:8086/api/rooms/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 15 — Get Room by Room Number

```
GET http://localhost:8086/api/rooms/number/101
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 16 — Update Room

```
PUT http://localhost:8086/api/rooms/1
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "roomType": "DELUXE",
  "pricePerNight": 175.00,
  "capacity": 3,
  "description": "Updated deluxe room with ocean view",
  "amenities": "WiFi, AC, Mini Bar, TV, Balcony",
  "floorNumber": 1
}
```

---

### 📌 STEP 17 — Update Room Status

```
PATCH http://localhost:8086/api/rooms/1/status?status=MAINTENANCE
Authorization: Bearer <your-jwt-token>
```

> **Room Statuses**: `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`, `RESERVED`

---

### 📌 STEP 18 — Create a Booking

> ⚠️ The room must be `AVAILABLE` and the customer must exist.

```
POST http://localhost:8086/api/bookings
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "customerId": 1,
  "roomId": 1,
  "checkInDate": "2026-04-01",
  "checkOutDate": "2026-04-05",
  "numberOfGuests": 2,
  "specialRequests": "Late check-in please"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "bookingReference": "BK-xxxxxxxx",
  "customerId": 1,
  "roomId": 1,
  "roomNumber": "101",
  "checkInDate": "2026-04-01",
  "checkOutDate": "2026-04-05",
  "numberOfGuests": 2,
  "pricePerNight": 150.00,
  "totalAmount": 600.00,
  "status": "CONFIRMED",
  "specialRequests": "Late check-in please",
  "createdAt": "2026-03-27T12:00:00",
  "updatedAt": "2026-03-27T12:00:00"
}
```

> 📝 Note the booking `id` (e.g., `1`) — you need it for payment.

---

### 📌 STEP 19 — Get All Bookings

```
GET http://localhost:8086/api/bookings
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 20 — Get Booking by ID

```
GET http://localhost:8086/api/bookings/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 21 — Get Bookings by Customer

```
GET http://localhost:8086/api/bookings/customer/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 22 — Update Booking

```
PUT http://localhost:8086/api/bookings/1
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "checkInDate": "2026-04-02",
  "checkOutDate": "2026-04-06",
  "numberOfGuests": 3,
  "specialRequests": "Extra pillows please"
}
```

---

### 📌 STEP 23 — Process Payment

> ⚠️ The booking must exist and have a `CONFIRMED` status.

```
POST http://localhost:8086/api/payments
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "bookingId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "paymentReference": "PAY-xxxxxxxx",
  "bookingId": 1,
  "bookingReference": "BK-xxxxxxxx",
  "customerId": 1,
  "amount": 600.00,
  "paymentMethod": "CREDIT_CARD",
  "status": "COMPLETED",
  "transactionId": "TXN-xxxxxxxx",
  "paidAt": "2026-03-27T12:00:00",
  "createdAt": "2026-03-27T12:00:00",
  "updatedAt": "2026-03-27T12:00:00"
}
```

> **Payment Methods**: `CREDIT_CARD`, `DEBIT_CARD`, `CASH`, `BANK_TRANSFER`, `ONLINE`

---

### 📌 STEP 24 — Get All Payments

```
GET http://localhost:8086/api/payments
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 25 — Get Payment by ID

```
GET http://localhost:8086/api/payments/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 26 — Get Payments by Booking

```
GET http://localhost:8086/api/payments/booking/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 27 — Get Payments by Customer

```
GET http://localhost:8086/api/payments/customer/1
Authorization: Bearer <your-jwt-token>
```

---

### 📌 STEP 28 — Refund Payment

```
POST http://localhost:8086/api/payments/1/refund
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "reason": "Customer requested cancellation"
}
```

---

### 📌 STEP 29 — Cancel Booking

```
DELETE http://localhost:8086/api/bookings/1/cancel
Authorization: Bearer <your-jwt-token>
```

**Expected Response: 204 No Content**

---

### 📌 STEP 30 — Delete a Room

```
DELETE http://localhost:8086/api/rooms/1
Authorization: Bearer <your-jwt-token>
```

**Expected Response: 204 No Content**

---

### 📌 STEP 31 — Delete a Customer

```
DELETE http://localhost:8086/api/customers/1
Authorization: Bearer <your-jwt-token>
```

**Expected Response: 204 No Content**

---

## ✅ Complete End-to-End Flow Summary

```
1. Register User     → POST /api/auth/register
2. Login             → POST /api/auth/login          → Get JWT Token
3. Create Customer   → POST /api/customers            → Get customer ID
4. Create Room       → POST /api/rooms                → Get room ID
5. Create Booking    → POST /api/bookings             → Get booking ID
6. Process Payment   → POST /api/payments             → Payment completed
7. (Optional) Refund → POST /api/payments/{id}/refund
8. (Optional) Cancel → DELETE /api/bookings/{id}/cancel
```
