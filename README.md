# Sistem de Rezervări pentru Spații de Coworking - HORECA

## Descriere Generală

Acest sistem este o aplicație Spring Boot pentru gestionarea rezervărilor de spații în industria HORECA (Hoteluri, Restauranturi, Cafenele). Sistemul suportă arhitectura multi-tenant, permițând mai multor hoteluri/restaurante să gestioneze propriile spații și rezervări.

## Arhitectura Sistemului

### Modelul de Date

Sistemul folosește următoarele entități principale:

1. **Tenant** - Reprezintă un hotel/restaurant (ex: Hotel Exemplu)
2. **Location** - Locația fizică a hotelului (ex: București, centru)
3. **Space** - Spațiul rezervabil (ex: Sala de conferințe, Restaurant privat)
4. **User** - Utilizatorul care face rezervări
5. **Reservation** - Rezervarea efectivă
6. **Availability** - Disponibilitatea spațiilor

### Relații între Entități

```
Tenant (1) → (N) Location
Location (1) → (N) Space  
Tenant (1) → (N) User
Space (1) → (N) Reservation
User (1) → (N) Reservation
```

## API Endpoints

### 1. Gestionarea Tenant-urilor (`/api/v1/tenants`)

#### Crearea unui tenant nou
```http
POST /api/v1/tenants
Content-Type: application/json

{
  "name": "Hotel Exemplu",
  "subdomain": "hotel-exemplu",
  "displayName": "Hotel Exemplu București",
  "contactEmail": "contact@hotel-exemplu.ro",
  "contactPhone": "+40 21 123 4567",
  "address": "Strada Exemplu nr. 123",
  "city": "București",
  "country": "România",
  "status": "ACTIVE",
  "plan": "PREMIUM",
  "maxUsers": 50,
  "maxSpaces": 200,
  "maxReservationsPerMonth": 5000
}
```

**Când să folosești:** La înregistrarea unui nou hotel/restaurant în sistem.

#### Obținerea unui tenant după ID
```http
GET /api/v1/tenants/{id}
```

**Când să folosești:** Pentru a obține informațiile despre un hotel specific.

#### Obținerea unui tenant după subdomain
```http
GET /api/v1/tenants/subdomain/{subdomain}
```

**Când să folosești:** Pentru a identifica un hotel după subdomain-ul său.

### 2. Gestionarea Locațiilor (`/api/v1/locations`)

#### Crearea unei locații
```http
POST /api/v1/locations
Content-Type: application/json

{
  "name": "Sala Mare",
  "address": "Strada Exemplu 1",
  "city": "București",
  "tenantId": 1
}
```

**Când să folosești:** Când un hotel vrea să adauge o nouă locație (ex: un nou restaurant, o sală de evenimente).

#### Obținerea locațiilor unui tenant
```http
GET /api/v1/locations?tenantId=1
```

**Când să folosești:** Pentru a afișa toate locațiile unui hotel.

### 3. Gestionarea Spațiilor (`/api/v1/spaces`)

#### Crearea unui spațiu
```http
POST /api/v1/spaces
Content-Type: application/json

{
  "name": "Sala de Conferințe A",
  "description": "Sala mare cu proiector și sistem audio",
  "capacity": 50,
  "locationId": 1,
  "pricePerHour": 100.0,
  "tenantId": 1
}
```

**Când să folosești:** Când vrei să adaugi un spațiu rezervabil (sala de conferințe, restaurant privat, etc.).

#### Obținerea spațiilor după locație
```http
GET /api/v1/spaces/location/{locationId}?tenantId=1
```

**Când să folosești:** Pentru a afișa toate spațiile dintr-o anumită locație.

#### Obținerea spațiilor după capacitate
```http
GET /api/v1/spaces/capacity/{capacity}?tenantId=1
```

**Când să folosești:** Pentru a găsi spații cu o capacitate specifică (ex: săli pentru 20+ persoane).

### 4. Gestionarea Utilizatorilor (`/api/v1/users`)

#### Crearea unui utilizator
```http
POST /api/v1/users
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["ROLE_USER"],
  "tenantId": 1
}
```

**Când să folosești:** Când un hotel vrea să înregistreze un nou client sau angajat.

#### Schimbarea parolei
```http
PUT /api/v1/users/{id}/password
Content-Type: application/json

{
  "currentPassword": "parolaVeche123",
  "newPassword": "parolaNoua123"
}
```

**Când să folosești:** Când un utilizator vrea să-și schimbe parola.

### 5. Gestionarea Rezervărilor (`/api/v1/reservations`)

#### Crearea unei rezervări
```http
POST /api/v1/reservations
Content-Type: application/json

{
  "spaceId": 1,
  "userId": 1,
  "startTime": "2025-01-15T10:00:00",
  "endTime": "2025-01-15T12:00:00",
  "totalPrice": 200.0,
  "status": "PENDING",
  "tenantId": 1
}
```

**Când să folosești:** Când un client vrea să rezerve un spațiu.

**Logica din spate:**
- Sistemul verifică dacă utilizatorul și spațiul există
- Validează că data de început este în viitor
- Verifică că nu există rezervări suprapuse pentru același spațiu
- Creează rezervarea cu statusul PENDING

#### Confirmarea unei rezervări
```http
PUT /api/v1/reservations/{id}/confirm
```

**Când să folosești:** Când hotelul confirmă o rezervare.

#### Anularea unei rezervări
```http
PUT /api/v1/reservations/{id}/cancel
```

**Când să folosești:** Când un client anulează o rezervare.

#### Obținerea rezervărilor unui utilizator
```http
GET /api/v1/reservations/user/{userId}
```

**Când să folosești:** Pentru a afișa istoricul rezervărilor unui client.

#### Obținerea rezervărilor unui spațiu
```http
GET /api/v1/reservations/space/{spaceId}
```

**Când să folosești:** Pentru a vedea programul unui spațiu.

### 6. Gestionarea Disponibilității (`/api/v1/availabilities`)

#### Verificarea disponibilității unui spațiu
```http
GET /api/v1/availabilities/space/{spaceId}?date=2025-01-15
```

**Când să folosești:** Pentru a verifica ce intervale sunt disponibile într-o zi specifică.

## Fluxuri de Utilizare Tipice

### 1. Înregistrarea unui Hotel Nou

1. **Creează tenant-ul:**
   ```http
   POST /api/v1/tenants
   ```

2. **Adaugă locațiile:**
   ```http
   POST /api/v1/locations
   ```

3. **Adaugă spațiile:**
   ```http
   POST /api/v1/spaces
   ```

### 2. Procesul de Rezervare

1. **Clientul se înregistrează:**
   ```http
   POST /api/v1/users
   ```

2. **Clientul verifică disponibilitatea:**
   ```http
   GET /api/v1/availabilities/space/{spaceId}?date=2025-01-15
   ```

3. **Clientul face rezervarea:**
   ```http
   POST /api/v1/reservations
   ```

4. **Hotelul confirmă rezervarea:**
   ```http
   PUT /api/v1/reservations/{id}/confirm
   ```

### 3. Gestionarea Zilnică

1. **Vizualizează rezervările zilei:**
   ```http
   GET /api/v1/reservations/space/{spaceId}
   ```

2. **Verifică disponibilitatea pentru noi rezervări:**
   ```http
   GET /api/v1/availabilities/space/{spaceId}?date=2025-01-16
   ```

## Configurare și Instalare

### Cerințe
- Java 21
- MySQL 8.0+
- Maven 3.6+

### Configurare Bază de Date

1. **Creează baza de date MySQL:**
   ```sql
   CREATE DATABASE reservationsystem;
   ```

2. **Configurează credențialele în `application.properties`:**
   ```properties
   spring.datasource.username=root
   spring.datasource.password=password
   ```

### Rularea Aplicației

```bash
mvn spring-boot:run
```

Aplicația va rula pe `http://localhost:8080`

### Rularea Testelor

```bash
mvn test
```

## Securitate

Sistemul folosește Spring Security cu:
- Autentificare bazată pe roluri (ROLE_USER, ROLE_ADMIN)
- Izolare multi-tenant (fiecare tenant vede doar propriile date)
- Validare pentru toate input-urile

## Logging și Monitorizare

Sistemul loghează:
- Toate operațiunile CRUD
- Încercările de autentificare
- Erorile de validare
- Performanța query-urilor

## Suport și Contact

Pentru întrebări tehnice sau suport, contactează echipa de dezvoltare. 