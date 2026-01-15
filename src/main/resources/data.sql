-- Простой SQL без проблем
INSERT INTO products (title, cost) VALUES 
('Product 1', 1000);
INSERT INTO stops (name, lat, lon) VALUES 
('Stop 1', 55.7558, 37.6176);
INSERT INTO routes DEFAULT VALUES;
INSERT INTO buses (model, route_id) VALUES 
('Bus 1', 1);