CREATE SCHEMA IF NOT EXISTS products;

CREATE TABLE products.category (
    id bigserial PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);