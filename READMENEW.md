Slide 1 – Title
# Assignment 2 – Microservices Architecture

## Group Members

*   *[Add Group Member 1]*
*   *[Add Group Member 2]*
*   *[Add Group Member 3]*
*   *[Add Group Member 4]*

---
Slide 2 – Introduction
## Introduction

### What is Microservices Architecture?
Microservices architecture is a design approach where a single application is built as a suite of small, independently deployable services. Each service runs its own process, manages its own database, and communicates with other services through lightweight mechanisms like HTTP/REST APIs.

### Why the Hotel Booking Domain?
The Hotel Booking domain was chosen because it involves distinct, critical business operations (managing rooms, handling bookings, processing payments, and managing users). This makes it highly suitable for microservices, allowing for localized fault isolation, independent scaling (e.g., scaling the Booking or Room service during peak holiday seasons), and separation of concerns.

---
Slide 3 – System Architecture Diagram

## System Architecture Flow & Workflow Diagrams

### 1. Full Structural Architecture Diagram
Every service with its actual port, its dedicated database, the API Gateway as the single entry point, and the inter-service REST calls between microservices.

```mermaid
flowchart TD
    Client(["Client / Postman / Browser"])

    subgraph Gateway["API Gateway :8086"]
        GW_Filter["AuthenticationFilter\n- JWT Validation\n- RBAC Enforcement\n- Inject X-Auth Headers"]
        GW_CB["CircuitBreaker\nAuth route only"]
    end

    Client -- "All HTTP Requests" --> GW_Filter

    subgraph Services["Microservices Layer"]
        Auth["Auth Service :8081\n/api/auth/**\nOpen - no JWT needed"]
        Customer["Customer Service :8082\n/api/customers/**"]
        Room["Room Service :8083\n/api/rooms/**"]
        Booking["Booking Service :8084\n/api/bookings/**"]
        Payment["Payment Service :8085\n/api/payments/**"]
    end

    GW_Filter -->|CircuitBreaker| GW_CB
    GW_CB --> Auth
    GW_Filter -->|AuthenticationFilter| Customer
    GW_Filter -->|AuthenticationFilter| Room
    GW_Filter -->|AuthenticationFilter| Booking
    GW_Filter -->|AuthenticationFilter| Payment

    Auth -.- DB1[("hotel_auth_db")]
    Customer -.- DB2[("hotel_customer_db")]
    Room -.- DB3[("hotel_room_db")]
    Booking -.- DB4[("hotel_booking_db")]
    Payment -.- DB5[("hotel_payment_db")]

    Booking -- "REST: Check Availability + Update Status" --> Room
    Booking -- "REST: Validate Customer Exists" --> Customer
    Payment -- "REST: Fetch Booking Amount" --> Booking
```

---

### 2. API Gateway Internal Flow (Authentication & RBAC)
What the `AuthenticationFilter` does inside the gateway for every incoming request. Based on actual `AuthenticationFilter.java`.

```mermaid
flowchart TD
    A([Incoming Request]) --> B{Is route public?}
    B -- "YES /api/auth/**" --> C[Forward via CircuitBreaker\nNo JWT check]
    B -- "NO all other routes" --> D{Authorization\nheader present?}
    D -- "NO" --> E["401 UNAUTHORIZED\nMissing Authorization header"]
    D -- "YES" --> F{Starts with Bearer?}
    F -- "NO" --> G["401 UNAUTHORIZED\nInvalid header format"]
    F -- "YES" --> H[Extract JWT Token]
    H --> I{Token valid\nand not expired?}
    I -- "NO" --> J["401 UNAUTHORIZED\nInvalid or expired JWT"]
    I -- "YES" --> K["Extract username, role, email"]
    K --> L{RBAC Check\nRole + Path + Method}
    L -- "ROLE_ADMIN" --> M["Full Access\nAll endpoints allowed"]
    L -- "ROLE_USER + /api/rooms\nGET only" --> N["Allowed\nRead-only rooms"]
    L -- "ROLE_USER + /api/rooms\nPOST PUT DELETE" --> O["403 FORBIDDEN"]
    L -- "ROLE_USER + /api/customers\nCREATE READ UPDATE own" --> P["Allowed"]
    L -- "ROLE_USER + /api/customers\nDELETE or GET all" --> Q["403 FORBIDDEN"]
    L -- "ROLE_USER + /api/bookings or payments\nGET all" --> R["403 FORBIDDEN"]
    M --> S["Inject Headers:\nX-Auth-User\nX-Auth-Role\nX-Auth-Email"]
    N --> S
    P --> S
    S --> T([Route to Downstream Service])
```

---

### 3. Login & JWT Token Workflow
Full sequence from user registration to making an authenticated API call.

```mermaid
sequenceDiagram
    participant Client
    participant GW as API Gateway :8086
    participant Auth as Auth Service :8081
    participant DB as hotel_auth_db

    Note over Client,DB: Step 1 - Register
    Client->>GW: POST /api/auth/register
    Note right of Client: username, email, password, role
    GW->>Auth: Forward with CircuitBreaker
    Auth->>DB: Save user with BCrypt hashed password
    Auth-->>Client: 200 OK with JWT token

    Note over Client,DB: Step 2 - Login
    Client->>GW: POST /api/auth/login
    Note right of Client: username, password
    GW->>Auth: Forward with CircuitBreaker
    Auth->>DB: Lookup user, verify BCrypt
    Auth-->>Client: 200 OK with JWT Token and role

    Note over Client,GW: Step 3 - Secured API Call
    Client->>GW: GET /api/rooms with Bearer JWT
    GW->>GW: Validate JWT signature and expiry
    GW->>GW: Extract role=ROLE_ADMIN, RBAC passed
    GW->>GW: Inject X-Auth-User, X-Auth-Role, X-Auth-Email
    GW->>GW: Route to Room Service :8083
    GW-->>Client: 200 OK Room list from Room Service
```

---

### 4. End-to-End Booking & Payment Workflow
Full inter-service communication chain for a complete hotel booking transaction.

```mermaid
sequenceDiagram
    participant Client
    participant GW as API Gateway :8086
    participant Booking as Booking Service :8084
    participant Room as Room Service :8083
    participant Customer as Customer Service :8082
    participant Payment as Payment Service :8085

    Note over Client,Payment: Step 1 - Create Booking
    Client->>GW: POST /api/bookings
    Note right of Client: customerId, roomId, checkIn, checkOut
    GW->>GW: Validate JWT and RBAC
    GW->>Booking: Forward with X-Auth headers
    Booking->>Customer: GET /api/customers/id - validate exists
    Customer-->>Booking: 200 OK Customer found
    Booking->>Room: GET /api/rooms/id - check AVAILABLE
    Room-->>Booking: 200 OK Room AVAILABLE
    Booking->>Room: PATCH /api/rooms/id/status to RESERVED
    Booking->>Booking: Save booking record as CONFIRMED
    Booking-->>Client: 201 Created with bookingId and totalAmount

    Note over Client,Payment: Step 2 - Process Payment
    Client->>GW: POST /api/payments
    Note right of Client: bookingId, paymentMethod
    GW->>GW: Validate JWT and RBAC
    GW->>Payment: Forward with X-Auth headers
    Payment->>Booking: GET /api/bookings/id - fetch amount
    Booking-->>Payment: 200 OK with totalAmount
    Payment->>Payment: Process transaction, generate PAY reference
    Payment->>Room: PATCH room status to OCCUPIED
    Payment-->>Client: 201 Created with paymentReference COMPLETED
```

---

### 5. Swagger Aggregation Architecture
How the API Gateway exposes all microservice APIs under one unified Swagger UI at `http://localhost:8086/swagger-ui.html`.

```mermaid
flowchart LR
    Dev(["Developer\nlocalhost:8086/swagger-ui.html"])

    subgraph GW["API Gateway :8086"]
        SwaggerUI["Aggregated Swagger UI"]
        RewriteFilter["RewritePath Filter\n/service/v3/api-docs -> /v3/api-docs"]
    end

    SwaggerUI --> RewriteFilter

    RewriteFilter --> Auth[":8081 Auth Service"]
    RewriteFilter --> Cust[":8082 Customer Service"]
    RewriteFilter --> Room[":8083 Room Service"]
    RewriteFilter --> Book[":8084 Booking Service"]
    RewriteFilter --> Pay[":8085 Payment Service"]

    Dev --> SwaggerUI
```

> **Note:** The API Gateway acts as the single entry point for all workflows. It handles JWT validation, RBAC enforcement, and X-Auth header injection before routing to downstream services. Auth routes use a CircuitBreaker pattern for fault tolerance. Swagger UI aggregates all 5 microservice API docs into one page.

---
Slide 4–6 – Each Microservice
## Microservices Breakdown

### 1. Customer Service 
*   **Responsibility:** Manages customer profiles, personal details, and ownership of their specific data.
*   **Endpoints:** `/api/customers/**` (e.g., GET `/api/customers/{id}`)
*   **Database:** MySQL (`hotel_customer_db`)

### 2. Room Service
*   **Responsibility:** Manages hotel room catalog, room types, pricing, and availability status.
*   **Endpoints:** `/api/rooms/**` (e.g., GET `/api/rooms/available`)
*   **Database:** MySQL (`hotel_room_db`)

### 3. Booking Service
*   **Responsibility:** Handles the reservation lifecycle, checks room availability via inter-service communication, and creates booking records.
*   **Endpoints:** `/api/bookings/**` (e.g., POST `/api/bookings`)
*   **Database:** MySQL (`hotel_booking_db`)

### 4. Payment Service
*   **Responsibility:** Processes financial transactions linked to bookings and maintains payment statuses.
*   **Endpoints:** `/api/payments/**` (e.g., POST `/api/payments`)
*   **Database:** MySQL (`hotel_payment_db`)

### 5. Auth Service
*   **Responsibility:** Handles user registration, login, and issues JWT tokens containing user roles and identity claims.
*   **Endpoints:** `/api/auth/**` (e.g., POST `/api/auth/login`)
*   **Database:** MySQL (`hotel_auth_db`)

---
Slide 7 – API Gateway

## API Gateway

*   **Role:** Acts as the unified front door for the system. It handles routing, security (JWT token validation + RBAC via `AuthenticationFilter`), header injection, circuit breaking, and Swagger UI aggregation — so that individual services don't have to duplicate this logic.
*   **Routing Example:** 
  1. Client sends a request to `http://localhost:8086/api/bookings/1` with a Bearer Token.
  2. The API Gateway intercepts the request, validates the JWT, and enforces RBAC rules.
  3. If valid, injects `X-Auth-User`, `X-Auth-Role`, `X-Auth-Email` headers and routes the request to the internal Booking Service running on port `8084`.

---
Slide 8 – Swagger Screenshots
## Swagger Screenshots

*(Tip: Insert your actual screenshots below before finalizing your presentation!)*

### Direct Access
*Screenshot showing direct access to a specific microservice (e.g., Customer Service on port 8082).*
> `![Direct Swagger Access](./screenshots/swagger_direct.png)`

### Gateway Access
*Screenshot showing the aggregated API documentation accessible through the API Gateway, utilizing OpenAPI definitions from all routed services.*
> `![Gateway Swagger Access](./screenshots/swagger_gateway.png)`

---
Slide 9 – Conclusion
## Conclusion

Building the Hotel Booking System using a Microservices Architecture provides several tremendous advantages over a monolithic approach:
*   **Scalability:** Services experiencing high load (like Room Search or Booking) can be scaled horizontally independent of less frequently used services.
*   **Flexibility:** Different teams can utilize the best tool for the job. Services can easily be rewritten or upgraded without impacting the rest of the ecosystem.
*   **Independent Deployment:** A bug fix in the Payment Service only requires a redeployment of that specific service, ensuring minimal downtime and faster delivery times.

---

## Technologies Used

*   **Backend Framework:** Spring Boot (Java 17)
*   **API Gateway:** Spring Cloud Gateway
*   **API Documentation:** Swagger / OpenAPI 3.0
*   **Security:** Spring Security, JWT (JSON Web Tokens)
*   **Database:** MySQL (Database per service pattern)
*   **Build Tool:** Maven
