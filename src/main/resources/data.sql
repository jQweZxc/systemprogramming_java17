-- Очистка таблиц
DELETE FROM passenger_counts;
DELETE FROM buses;
DELETE FROM route_stops;
DELETE FROM stops;
DELETE FROM routes;

-- Создание остановок
INSERT INTO stops (name, lat, lon) VALUES 
('Центральная', 55.7558, 37.6173),
('Вокзальная', 55.7458, 37.6273),
('Университет', 55.7358, 37.6073);

-- Создание маршрутов
INSERT INTO routes (id) VALUES (1), (2);

-- Связь маршрутов и остановок
INSERT INTO route_stops (route_id, stop_id) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 3);

-- Создание автобусов
INSERT INTO buses (model, route_id) VALUES 
('Mercedes Sprinter', 1),
('ПАЗ-3205', 1),
('ЛиАЗ-5292', 2);

-- Тестовые данные пассажиропотока
INSERT INTO passenger_counts (bus_id, stop_id, entered, exited, timestamp) VALUES 
(1, 1, 5, 2, CURRENT_TIMESTAMP - INTERVAL '2' HOUR),
(1, 2, 3, 1, CURRENT_TIMESTAMP - INTERVAL '1' HOUR),
(2, 1, 7, 0, CURRENT_TIMESTAMP);