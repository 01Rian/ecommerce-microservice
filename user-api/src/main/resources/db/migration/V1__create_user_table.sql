CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users."user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    address VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    data_register TIMESTAMP NOT NULL
);