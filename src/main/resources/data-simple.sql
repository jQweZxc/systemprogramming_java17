-- Минимальный data.sql для тестирования
INSERT INTO products (title, cost) VALUES 
('Test 1', 1000),
('Test 2', 2000);

INSERT INTO stops (name, lat, lon) VALUES 
('Stop 1', 55.7558, 37.6176),
('Stop 2', 55.7658, 37.6276);

INSERT INTO routes DEFAULT VALUES;

INSERT INTO buses (model, route_id) VALUES
('Bus 1', 1),
('Bus 2', 1);
