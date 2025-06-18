# API Endpoints Documentation

## User Management

### Create User
- **POST** `/api/v1/users`
- **Description**: Create a new user
- **Request Body**:
```json
{
    "user": {
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe"
    },
    "password": {
        "password": "securePassword123"
    }
}
```
- **Response**: 201 Created
```json
{
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get User by ID
- **GET** `/api/v1/users/{id}`
- **Description**: Get user details by ID
- **Response**: 200 OK
```json
{
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get All Users
- **GET** `/api/v1/users`
- **Description**: Get all users
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Update User
- **PUT** `/api/v1/users/{id}`
- **Description**: Update user details
- **Request Body**:
```json
{
    "email": "updated@example.com",
    "firstName": "John",
    "lastName": "Doe"
}
```
- **Response**: 200 OK
```json
{
    "id": 1,
    "email": "updated@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Delete User
- **DELETE** `/api/v1/users/{id}`
- **Description**: Delete a user
- **Response**: 204 No Content

## Space Management

### Create Space
- **POST** `/api/v1/spaces`
- **Description**: Create a new space
- **Request Body**:
```json
{
    "name": "Conference Room A",
    "capacity": 10,
    "description": "Large conference room with projector",
    "pricePerHour": 50.00
}
```
- **Response**: 201 Created
```json
{
    "id": 1,
    "name": "Conference Room A",
    "capacity": 10,
    "description": "Large conference room with projector",
    "pricePerHour": 50.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get Space by ID
- **GET** `/api/v1/spaces/{id}`
- **Description**: Get space details by ID
- **Response**: 200 OK
```json
{
    "id": 1,
    "name": "Conference Room A",
    "capacity": 10,
    "description": "Large conference room with projector",
    "pricePerHour": 50.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get All Spaces
- **GET** `/api/v1/spaces`
- **Description**: Get all spaces
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "name": "Conference Room A",
        "capacity": 10,
        "description": "Large conference room with projector",
        "pricePerHour": 50.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Update Space
- **PUT** `/api/v1/spaces/{id}`
- **Description**: Update space details
- **Request Body**:
```json
{
    "name": "Conference Room A",
    "capacity": 12,
    "description": "Updated description",
    "pricePerHour": 55.00
}
```
- **Response**: 200 OK
```json
{
    "id": 1,
    "name": "Conference Room A",
    "capacity": 12,
    "description": "Updated description",
    "pricePerHour": 55.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Delete Space
- **DELETE** `/api/v1/spaces/{id}`
- **Description**: Delete a space
- **Response**: 204 No Content

## Reservation Management

### Create Reservation
- **POST** `/api/v1/reservations`
- **Description**: Create a new reservation
- **Request Body**:
```json
{
    "spaceId": 1,
    "userId": 1,
    "startTime": "2024-03-21T10:00:00",
    "endTime": "2024-03-21T12:00:00",
    "status": "PENDING"
}
```
- **Response**: 201 Created
```json
{
    "id": 1,
    "space": {
        "id": 1,
        "name": "Conference Room A",
        "capacity": 10,
        "description": "Large conference room with projector",
        "pricePerHour": 50.00
    },
    "user": {
        "id": 1,
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe"
    },
    "startTime": "2024-03-21T10:00:00",
    "endTime": "2024-03-21T12:00:00",
    "status": "PENDING",
    "totalPrice": 100.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get Reservation by ID
- **GET** `/api/v1/reservations/{id}`
- **Description**: Get reservation details by ID
- **Response**: 200 OK
```json
{
    "id": 1,
    "space": {
        "id": 1,
        "name": "Conference Room A",
        "capacity": 10,
        "description": "Large conference room with projector",
        "pricePerHour": 50.00
    },
    "user": {
        "id": 1,
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe"
    },
    "startTime": "2024-03-21T10:00:00",
    "endTime": "2024-03-21T12:00:00",
    "status": "PENDING",
    "totalPrice": 100.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Get All Reservations
- **GET** `/api/v1/reservations`
- **Description**: Get all reservations
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "space": {
            "id": 1,
            "name": "Conference Room A",
            "capacity": 10,
            "description": "Large conference room with projector",
            "pricePerHour": 50.00
        },
        "user": {
            "id": 1,
            "email": "user@example.com",
            "firstName": "John",
            "lastName": "Doe"
        },
        "startTime": "2024-03-21T10:00:00",
        "endTime": "2024-03-21T12:00:00",
        "status": "PENDING",
        "totalPrice": 100.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Update Reservation
- **PUT** `/api/v1/reservations/{id}`
- **Description**: Update reservation details
- **Request Body**:
```json
{
    "spaceId": 1,
    "userId": 1,
    "startTime": "2024-03-21T14:00:00",
    "endTime": "2024-03-21T16:00:00",
    "status": "CONFIRMED"
}
```
- **Response**: 200 OK
```json
{
    "id": 1,
    "space": {
        "id": 1,
        "name": "Conference Room A",
        "capacity": 10,
        "description": "Large conference room with projector",
        "pricePerHour": 50.00
    },
    "user": {
        "id": 1,
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe"
    },
    "startTime": "2024-03-21T14:00:00",
    "endTime": "2024-03-21T16:00:00",
    "status": "CONFIRMED",
    "totalPrice": 100.00,
    "createdAt": "2024-03-20T10:00:00",
    "updatedAt": "2024-03-20T10:00:00"
}
```

### Delete Reservation
- **DELETE** `/api/v1/reservations/{id}`
- **Description**: Delete a reservation
- **Response**: 204 No Content

### Get User's Reservations
- **GET** `/api/v1/reservations/user/{userId}`
- **Description**: Get all reservations for a specific user
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "space": {
            "id": 1,
            "name": "Conference Room A",
            "capacity": 10,
            "description": "Large conference room with projector",
            "pricePerHour": 50.00
        },
        "startTime": "2024-03-21T10:00:00",
        "endTime": "2024-03-21T12:00:00",
        "status": "PENDING",
        "totalPrice": 100.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Get Space's Reservations
- **GET** `/api/v1/reservations/space/{spaceId}`
- **Description**: Get all reservations for a specific space
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "user": {
            "id": 1,
            "email": "user@example.com",
            "firstName": "John",
            "lastName": "Doe"
        },
        "startTime": "2024-03-21T10:00:00",
        "endTime": "2024-03-21T12:00:00",
        "status": "PENDING",
        "totalPrice": 100.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Get Overlapping Reservations
- **GET** `/api/v1/reservations/overlapping`
- **Description**: Get all overlapping reservations for a space
- **Query Parameters**:
  - `spaceId`: ID of the space
  - `startTime`: Start time (ISO-8601 format)
  - `endTime`: End time (ISO-8601 format)
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "user": {
            "id": 1,
            "email": "user@example.com",
            "firstName": "John",
            "lastName": "Doe"
        },
        "startTime": "2024-03-21T10:00:00",
        "endTime": "2024-03-21T12:00:00",
        "status": "PENDING",
        "totalPrice": 100.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
```

### Get Available Spaces
- **GET** `/api/v1/spaces/available`
- **Description**: Get all available spaces for a time period
- **Query Parameters**:
  - `startTime`: Start time (ISO-8601 format)
  - `endTime`: End time (ISO-8601 format)
- **Response**: 200 OK
```json
[
    {
        "id": 1,
        "name": "Conference Room A",
        "capacity": 10,
        "description": "Large conference room with projector",
        "pricePerHour": 50.00,
        "createdAt": "2024-03-20T10:00:00",
        "updatedAt": "2024-03-20T10:00:00"
    }
]
``` 