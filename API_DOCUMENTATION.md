# RESTful API Documentation

## Overview
This document describes the RESTful APIs implemented for the e-commerce system.

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. User Profile Management

#### Get User Profile
```http
GET /api/v1/profile
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "123-456-7890"
  },
  "path": "/api/v1/profile"
}
```

#### Update User Profile
```http
PUT /api/v1/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "John Doe Updated",
  "phone": "123-456-7890"
}
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe Updated",
    "phone": "123-456-7890"
  },
  "path": "/api/v1/profile"
}
```

### 2. Articles/News Management

#### Get All Articles
```http
GET /api/v1/articles
```
*Note: This endpoint is public and does not require authentication*

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Articles retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Article Title",
      "content": "Article content...",
      "author": "Author Name",
      "publishedAt": "2025-11-11T18:00:00Z"
    }
  ],
  "path": "/api/v1/articles"
}
```

#### Get Article by ID
```http
GET /api/v1/articles/{id}
```
*Note: This endpoint is public and does not require authentication*

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Article retrieved successfully",
  "data": {
    "id": 1,
    "title": "Article Title",
    "content": "Article content...",
    "author": "Author Name",
    "publishedAt": "2025-11-11T18:00:00Z"
  },
  "path": "/api/v1/articles/1"
}
```

### 3. Order Management

#### Create Order
```http
POST /api/v1/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 100.00
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 50.00
    }
  ]
}
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 201,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "total": 250.00,
    "status": "PENDING",
    "createdAt": "2025-11-11T18:00:00Z",
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 100.00
      },
      {
        "productId": 2,
        "quantity": 1,
        "price": 50.00
      }
    ]
  },
  "path": "/api/v1/orders"
}
```

#### Get User Orders
```http
GET /api/v1/orders
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Orders retrieved successfully",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "total": 250.00,
      "status": "PENDING",
      "createdAt": "2025-11-11T18:00:00Z",
      "items": [...]
    }
  ],
  "path": "/api/v1/orders"
}
```

#### Get Order by ID
```http
GET /api/v1/orders/{id}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Order retrieved successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "total": 250.00,
    "status": "CONFIRMED",
    "createdAt": "2025-11-11T18:00:00Z",
    "items": [...]
  },
  "path": "/api/v1/orders/1"
}
```

#### Update Order Status
```http
PATCH /api/v1/orders/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "PROCESSING"
}
```

**Order Status Values:**
- `PENDING` - Initial status
- `CONFIRMED` - Order confirmed (set automatically when payment completed)
- `PROCESSING` - Order being processed
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled
- `RETURNED` - Order returned

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Order status updated successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "total": 250.00,
    "status": "PROCESSING",
    "createdAt": "2025-11-11T18:00:00Z",
    "items": [...]
  },
  "path": "/api/v1/orders/1/status"
}
```

### 4. Payment Management

#### Create Payment
```http
POST /api/v1/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": 1,
  "amount": 250.00,
  "provider": "STRIPE",
  "currency": "USD"
}
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 201,
  "message": "Payment created successfully",
  "data": {
    "id": 1,
    "orderId": 1,
    "userId": 1,
    "amount": 250.00,
    "currency": "USD",
    "provider": "STRIPE",
    "externalId": "aaf95514-6566-441a-adb2-d9673341f65e",
    "status": "PENDING",
    "createdAt": "2025-11-11T18:00:00Z"
  },
  "path": "/api/v1/payments"
}
```

#### Get User Payments
```http
GET /api/v1/payments
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "orderId": 1,
      "userId": 1,
      "amount": 250.00,
      "currency": "USD",
      "provider": "STRIPE",
      "externalId": "aaf95514-6566-441a-adb2-d9673341f65e",
      "status": "PENDING",
      "createdAt": "2025-11-11T18:00:00Z"
    }
  ],
  "path": "/api/v1/payments"
}
```

#### Get Payment by ID
```http
GET /api/v1/payments/{id}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Payment retrieved successfully",
  "data": {
    "id": 1,
    "orderId": 1,
    "userId": 1,
    "amount": 250.00,
    "currency": "USD",
    "provider": "STRIPE",
    "externalId": "aaf95514-6566-441a-adb2-d9673341f65e",
    "status": "COMPLETED",
    "createdAt": "2025-11-11T18:00:00Z"
  },
  "path": "/api/v1/payments/1"
}
```

#### Update Payment Status
```http
PATCH /api/v1/payments/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

**Payment Status Values:**
- `PENDING` - Initial status
- `PROCESSING` - Payment being processed
- `COMPLETED` - Payment successful (automatically updates order to CONFIRMED)
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded
- `CANCELLED` - Payment cancelled

**Response:**
```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 200,
  "message": "Payment status updated successfully",
  "data": {
    "id": 1,
    "orderId": 1,
    "userId": 1,
    "amount": 250.00,
    "currency": "USD",
    "provider": "STRIPE",
    "externalId": "aaf95514-6566-441a-adb2-d9673341f65e",
    "status": "COMPLETED",
    "createdAt": "2025-11-11T18:00:00Z"
  },
  "path": "/api/v1/payments/1/status"
}
```

**Important:** When a payment status is updated to `COMPLETED`, the associated order status is automatically updated to `CONFIRMED`.

## Business Logic Flows

### E-commerce Purchase Flow

1. **User browses articles/news** (public access)
   - `GET /api/v1/articles`
   - `GET /api/v1/articles/{id}`

2. **User manages profile**
   - `GET /api/v1/profile` (view profile)
   - `PUT /api/v1/profile` (update profile)

3. **User creates an order**
   - `POST /api/v1/orders` with items
   - Order created with status `PENDING`

4. **User initiates payment**
   - `POST /api/v1/payments` with orderId
   - Payment created with status `PENDING`

5. **Payment processing completes**
   - `PATCH /api/v1/payments/{id}/status` with `COMPLETED`
   - Payment status updated to `COMPLETED`
   - **Order status automatically updated to `CONFIRMED`**

6. **Order fulfillment**
   - `PATCH /api/v1/orders/{id}/status` → `PROCESSING`
   - `PATCH /api/v1/orders/{id}/status` → `SHIPPED`
   - `PATCH /api/v1/orders/{id}/status` → `DELIVERED`

## Error Handling

All endpoints return standard error responses:

```json
{
  "timestamp": "2025-11-11T18:00:00Z",
  "status": 404,
  "message": "Order not found",
  "path": "/api/v1/orders/999"
}
```

Common HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation error)
- `401` - Unauthorized (missing or invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

## Security

- User-specific endpoints (profile, orders, payments) require JWT authentication
- Users can only access their own data
- Articles endpoints are public
- All endpoints use standard Spring Security with JWT tokens
- Database schema includes proper foreign key constraints
