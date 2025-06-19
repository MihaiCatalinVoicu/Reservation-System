-- Sample data for HORECA Reservation System
-- This will be loaded only if the database is empty (first startup)

-- Sample Tenants
INSERT INTO tenants (id, name, subdomain, display_name, contact_email, contact_phone, address, city, country, status, plan, max_users, max_spaces, max_reservations_per_month, subscription_start_date, subscription_end_date, created_at, updated_at) 
VALUES 
(1, 'Restaurant Central', 'central', 'Restaurant Central', 'contact@central.ro', '+40123456789', 'Strada Centrală 123', 'București', 'România', 'ACTIVE', 'BASIC', 10, 50, 1000, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Hotel Palace', 'palace', 'Hotel Palace', 'info@palace.ro', '+40987654321', 'Bulevardul Victoriei 456', 'București', 'România', 'ACTIVE', 'PREMIUM', 25, 100, 5000, CURRENT_TIMESTAMP, DATEADD('YEAR', 1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Locations
INSERT INTO locations (id, name, address, city, tenant_id) 
VALUES 
(1, 'Restaurant Central - Sediu Principal', 'Strada Centrală 123', 'București', 1),
(2, 'Hotel Palace - Lobby', 'Bulevardul Victoriei 456', 'București', 2);

-- Sample Spaces
INSERT INTO spaces (id, name, description, capacity, price_per_hour, location_id, tenant_id) 
VALUES 
(1, 'Sala Principală', 'Sala principală cu vedere la stradă, perfectă pentru evenimente', 50, 150.0, 1, 1),
(2, 'Sala Mică', 'Sala intimă pentru grupuri mici', 20, 80.0, 1, 1),
(3, 'Terasă', 'Terasă în aer liber cu vedere panoramică', 30, 100.0, 1, 1),
(4, 'Sala de Conferințe', 'Sala modernă pentru conferințe și prezentări', 100, 200.0, 2, 2),
(5, 'Restaurant Elegant', 'Restaurant elegant cu design modern', 80, 180.0, 2, 2);

-- Sample Restaurant Tables
INSERT INTO restaurant_tables (id, name, number_of_seats, status, space_id, tenant_id, notes, created_at, updated_at) 
VALUES 
(1, 'Masa 1', 4, 'AVAILABLE', 5, 2, 'Masă lângă fereastră', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Masa 2', 6, 'AVAILABLE', 5, 2, 'Masă în centrul restaurantului', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Masa 3', 2, 'OCCUPIED', 5, 2, 'Masă intimă pentru 2 persoane', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Masa 4', 8, 'AVAILABLE', 5, 2, 'Masă mare pentru grupuri', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Terasă 1', 4, 'AVAILABLE', 3, 1, 'Masă pe terasă cu vedere', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Terasă 2', 6, 'RESERVED', 3, 1, 'Masă pe terasă', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Users (passwords are BCrypt hashed 'Password123!')
INSERT INTO users (id, email, first_name, last_name, password, tenant_id, created_at) 
VALUES 
(1, 'admin@central.ro', 'Admin', 'Central', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, CURRENT_TIMESTAMP),
(2, 'manager@central.ro', 'Manager', 'Central', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, CURRENT_TIMESTAMP),
(3, 'admin@palace.ro', 'Admin', 'Palace', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, CURRENT_TIMESTAMP),
(4, 'manager@palace.ro', 'Manager', 'Palace', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, CURRENT_TIMESTAMP);

-- Sample User Roles
INSERT INTO user_roles (user_id, roles) 
VALUES 
(1, 'ROLE_ADMIN'),
(1, 'ROLE_USER'),
(2, 'ROLE_MANAGER'),
(2, 'ROLE_USER'),
(3, 'ROLE_ADMIN'),
(3, 'ROLE_USER'),
(4, 'ROLE_MANAGER'),
(4, 'ROLE_USER');

-- Sample Customers
INSERT INTO customers (id, first_name, last_name, phone, email, notes, tenant_id, created_at, updated_at) 
VALUES 
(1, 'Ion', 'Popescu', '+40712345678', 'ion.popescu@email.com', 'Client fidel', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Maria', 'Ionescu', '+40787654321', 'maria.ionescu@email.com', 'Preferă mesele lângă fereastră', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Alexandru', 'Dumitrescu', '+40711223344', 'alex.dumitrescu@email.com', 'Client VIP', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Elena', 'Constantinescu', '+40744332211', 'elena.constantinescu@email.com', 'Alergică la fructe de mare', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Table Reservations
INSERT INTO table_reservations (id, table_id, customer_id, number_of_people, requested_time, estimated_arrival_time, status, special_requests, tenant_id, created_at, updated_at) 
VALUES 
(1, 1, 3, 4, '2024-03-20 19:00:00', '2024-03-20 19:15:00', 'CONFIRMED', 'Masă lângă fereastră dacă este posibil', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 6, 1, 6, '2024-03-21 20:00:00', '2024-03-21 20:30:00', 'PENDING', 'Sărbătoare de naștere', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 4, 2, '2024-03-20 18:30:00', '2024-03-20 18:45:00', 'COMPLETED', 'Fără fructe de mare în meniu', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Refresh Tokens (for testing purposes)
INSERT INTO refresh_tokens (id, token, expires_at, created_at, revoked, user_id, user_agent, ip_address) 
VALUES 
(1, 'test-refresh-token-1', DATEADD('DAY', 7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, false, 1, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '127.0.0.1'),
(2, 'test-refresh-token-2', DATEADD('DAY', 7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, false, 2, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '192.168.1.100');

-- Sample Blacklisted Tokens (for testing purposes)
INSERT INTO blacklisted_tokens (id, token, blacklisted_at, expires_at, user_id, reason) 
VALUES 
(1, 'expired-access-token-1', DATEADD('HOUR', -2, CURRENT_TIMESTAMP), DATEADD('HOUR', 1, CURRENT_TIMESTAMP), 1, 'LOGOUT'),
(2, 'expired-access-token-2', DATEADD('HOUR', -1, CURRENT_TIMESTAMP), DATEADD('HOUR', 1, CURRENT_TIMESTAMP), 2, 'SECURITY_BREACH'); 