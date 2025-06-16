# API Endpoints Documentation

## User Endpoints
- `POST /api/v1/users` - Create a new user
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users` - Get all users
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

## Location Endpoints
- `POST /api/v1/locations` - Create a new location
- `GET /api/v1/locations/{id}` - Get location by ID
- `GET /api/v1/locations` - Get all locations
- `PUT /api/v1/locations/{id}` - Update location
- `DELETE /api/v1/locations/{id}` - Delete location

## Space Endpoints
- `POST /api/v1/spaces` - Create a new space
- `GET /api/v1/spaces/{id}` - Get space by ID
- `GET /api/v1/spaces` - Get all spaces
- `PUT /api/v1/spaces/{id}` - Update space
- `DELETE /api/v1/spaces/{id}` - Delete space

## Reservation Endpoints
- `POST /api/v1/reservations` - Create a new reservation
- `GET /api/v1/reservations/{id}` - Get reservation by ID
- `GET /api/v1/reservations` - Get all reservations
- `PUT /api/v1/reservations/{id}` - Update reservation
- `DELETE /api/v1/reservations/{id}` - Delete reservation

## Availability Endpoints
- `POST /api/v1/availabilities` - Create a new availability
- `GET /api/v1/availabilities/{id}` - Get availability by ID
- `GET /api/v1/availabilities` - Get all availabilities
- `GET /api/v1/availabilities/space/{spaceId}` - Get availabilities by space ID
- `PUT /api/v1/availabilities/{id}` - Update availability
- `DELETE /api/v1/availabilities/{id}` - Delete availability

## Request/Response Examples

### User
```json
// POST /api/v1/users
{
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
}

// Response
{
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2024-03-20T10:00:00"
}
```

### Location
```json
// POST /api/v1/locations
{
    "name": "Downtown Office",
    "address": "123 Main St",
    "city": "New York"
}

// Response
{
    "id": 1,
    "name": "Downtown Office",
    "address": "123 Main St",
    "city": "New York"
}
```

### Space
```json
// POST /api/v1/spaces
{
    "name": "Conference Room A",
    "capacity": 10,
    "locationId": 1
}

// Response
{
    "id": 1,
    "name": "Conference Room A",
    "capacity": 10,
    "locationId": 1
}
```

### Reservation
```json
// POST /api/v1/reservations
{
    "spaceId": 1,
    "userId": 1,
    "startTime": "2024-03-20T10:00:00",
    "endTime": "2024-03-20T12:00:00"
}

// Response
{
    "id": 1,
    "spaceId": 1,
    "userId": 1,
    "startTime": "2024-03-20T10:00:00",
    "endTime": "2024-03-20T12:00:00",
    "createdAt": "2024-03-19T15:00:00"
}
```

### Availability
```json
// POST /api/v1/availabilities
{
    "spaceId": 1,
    "startTime": "2024-03-20T09:00:00",
    "endTime": "2024-03-20T17:00:00"
}

// Response
{
    "id": 1,
    "spaceId": 1,
    "startTime": "2024-03-20T09:00:00",
    "endTime": "2024-03-20T17:00:00",
    "createdAt": "2024-03-19T15:00:00"
}
```

## Error Responses
All endpoints return standard HTTP status codes:
- 200 OK - Successful operation
- 201 Created - Resource created successfully
- 400 Bad Request - Invalid input
- 404 Not Found - Resource not found
- 500 Internal Server Error - Server error

Error response format:
```json
{
    "timestamp": "2024-03-20T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Resource not found with id: 1",
    "path": "/api/v1/users/1"
}
``` 