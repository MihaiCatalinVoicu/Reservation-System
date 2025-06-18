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

-- Sample Users (passwords are BCrypt hashed 'Password123!')
INSERT INTO users (id, email, first_name, last_name, password, tenant_id, created_at) 
VALUES 
(1, 'manager@central.ro', 'Ion', 'Popescu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 1, CURRENT_TIMESTAMP),
(2, 'chef@central.ro', 'Maria', 'Ionescu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 1, CURRENT_TIMESTAMP),
(3, 'admin@palace.ro', 'Alexandru', 'Vasilescu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 2, CURRENT_TIMESTAMP),
(4, 'reception@palace.ro', 'Elena', 'Dumitrescu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 2, CURRENT_TIMESTAMP);

-- Sample User Roles
INSERT INTO user_roles (user_id, roles) 
VALUES 
(1, 'ROLE_MANAGER'),
(2, 'ROLE_CHEF'),
(3, 'ROLE_ADMIN'),
(4, 'ROLE_RECEPTION');

-- Sample Reservations (IDs will be auto-generated)
INSERT INTO reservations (start_time, end_time, total_price, status, user_id, space_id, tenant_id, created_at) 
VALUES 
(DATEADD('HOUR', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, CURRENT_TIMESTAMP), 300.0, 'CONFIRMED', 1, 1, 1, CURRENT_TIMESTAMP),
(DATEADD('DAY', 1, DATEADD('HOUR', 18, CURRENT_TIMESTAMP)), DATEADD('DAY', 1, DATEADD('HOUR', 22, CURRENT_TIMESTAMP)), 800.0, 'PENDING', 3, 4, 2, CURRENT_TIMESTAMP); 