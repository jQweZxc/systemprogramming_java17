-- Создаем таблицу users если её нет
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role_id BIGINT
);

-- Вставляем тестового пользователя
INSERT INTO users (username, password, email, first_name, last_name) 
VALUES ('admin', '$2a$10$YourHashedPasswordHere', 'admin@system.com', 'Admin', 'User')
ON CONFLICT (username) DO NOTHING;