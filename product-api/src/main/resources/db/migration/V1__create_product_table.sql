CREATE SCHEMA IF NOT EXISTS products;

CREATE TABLE products."category" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE products."product" (
    id BIGSERIAL PRIMARY KEY,
    product_identifier VARCHAR NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL,
    price FLOAT NOT NULL,
    category_id BIGINT REFERENCES products.category(id)
);